#version 150

in vec4 depthTextureCoord;
in vec2 texCoord;

in vec4 finalColor;
in vec4 ambient;

uniform sampler2D depthTexture;
uniform sampler2D textureMosaic;

out vec4 outColor;// (vždy jediný) výstup z fragment shaderu

void main() {

    vec4 textureColor = texture(textureMosaic, texCoord);

    // "z" hodnota z textury
    // R, G, i B složky jsou stejné
    float zLight = texture(depthTexture, depthTextureCoord.xy).r;

    // 0.001 - bias na odstranění tzv.akné
    // lze vyzkoušet různé hodnoty (0.01, 0.001, 0.0001, 0.00001)
    //float zActual = depthTextureCoord.z - 0.001;
    float zActual = depthTextureCoord.z - 0.00001;

    bool shadow = zLight < zActual;

    if (shadow) {
         //outColor = vec4(0, 0, 1, 1);
        outColor = textureColor * ambient;
    } else {
        //outColor = vec4(1, 1, 0, 1);
        outColor = textureColor * finalColor;
    }

} 
