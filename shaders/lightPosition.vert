#version 150
in vec2 inPosition;// input from the vertex buffer

uniform mat4 view;
uniform mat4 projection;
uniform vec3 lightPosition;

const float PI = 3.1415;

//definuje světlo jako žlutou kouli se středem v umístění kamery
vec3 getSphere(vec2 vec) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI;// <-1;1> -> <-PI;PI>
    float r = 0.2;

    float x = r * cos(az) * cos(ze) + lightPosition.x;
    float y = r * sin(az) * cos(ze) + lightPosition.y;
    float z = r * sin(ze) + lightPosition.z;
    return vec3(x, y, z);
}

void main() {
    // grid máme od 0 do 1 a chceme od -1 od 1
    vec2 position = inPosition * 2.0 - 1.0;
    vec4 pos4 = vec4(getSphere(position), 1.0);

    gl_Position = projection * view * pos4;

}

