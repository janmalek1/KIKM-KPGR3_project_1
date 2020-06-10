#version 150
in vec2 inPosition;// input from the vertex buffer

uniform mat4 view;
uniform mat4 projection;
uniform float type;
uniform float time;

const float PI = 3.1415;
const float e = 2.71;

//rovinná podložka
vec3 getPlane1(vec2 vec) {
    return vec3(vec * 2.5, -1);
}

//rovinná podložka s proměnlivou hodnotou Z
vec3 getPlane2(vec2 vec) {
    return vec3(vec * 2.5, -2 + sin(0.01 * time));
}

//bramboroid vytvořený na cvičení
vec3 getSphere1(vec2 vec) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI / 2.0;// <-1;1> -> <-PI/2;PI/2>
    float r = 0.2;

    float x = r * cos(az) * cos(ze);
    float y = 2 * r * sin(az) * cos(ze);
    float z = 0.5 * r * sin(ze);
    return vec3(x, y, z);
}

//koule
vec3 getSphere2(vec2 vec) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI;// <-1;1> -> <-PI/2;PI/2>
    float r = 0.5;

    float x = r * cos(az) * cos(ze) + sin (0.1 * time);
    float y = r * sin(az) * cos(ze) + cos (0.1 * time);
    float z = r * sin(ze);
    return vec3(x, y, z);
}

//válec
vec3 getCylinder1(vec2 vec) {
    float r = 1;
    float t = vec.x * 2 - 2;
    float s = vec.y * 2 * PI;

    float theta = s;
    float x = r*cos(theta);
    float y = r*sin(theta);
    float z = t;

    return vec3(x, y, z);
}

//mořská škeble
vec3 getCylinder2(vec2 vec) {
    float u = vec.x * 6*PI;
    float v = vec.y * 2*PI;

    float x = 2*(1-pow(e, u/(6*PI)))*cos(u)*pow(cos(0.5*v), 2);
    float y = 2*(-1+pow(e, u/(6*PI)))*sin(u)*pow(cos(0.5*v), 2);
    float z = 1 - pow(e, u/(3*PI)) - sin(v) + pow(e, u/(6*PI)) * sin(v);
    return vec3(x, y, z);
}

vec3 getCoordinates(vec2 vec, float typeSystem) {

    vec3 coord;

    if (typeSystem == 1.0) {
        coord = getPlane1(vec);
    } else if (typeSystem == 2.0) {
        coord = getPlane2(vec);
    } else if (typeSystem == 3.0) {
        coord = getSphere1(vec);
    } else if (typeSystem == 4.0) {
        coord = getSphere2(vec);
    } else if (typeSystem == 5.0) {
        coord = getCylinder1(vec);
    } else if (typeSystem == 6.0) {
        coord = getCylinder2(vec);
    };

    return coord;
}

void main() {
    // grid máme od 0 do 1 a chceme od -1 od 1
    vec2 position = inPosition * 2.0 - 1.0;
    vec4 pos4;
    pos4 = vec4(getCoordinates(position, type), 1.0);

    gl_Position = projection * view * pos4;
} 
