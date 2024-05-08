#version 300 es

precision highp float;

in vec4 texCoord;
in vec4 modelPosition;
in vec4 worldPosition;

uniform struct {
  vec3 lightWoodColor;
  vec3 darkWoodColor;
  float freq;
  float noiseFreq;
  float noiseExp;
  float noiseAmp;
} material;

uniform struct {
  mat4 shadowMatrix;
  vec3 shadowColor;
} scene;

out vec4 fragmentColor;

float snoise(vec3 r) {
  vec3 s = vec3(7502, 22777, 4767);
  float f = 0.0;
  for(int i=0; i<16; i++) {
    f += sin(dot(s - vec3(32768, 32768, 32768), r) / 65536.0);
    s = mod(s, 32768.0) * 2.0 + floor(s / 32768.0);
  }
  return f / 32.0 + 0.5;
}


void main(void) {
  // change to worldPosition instead of modelPosition for cool effect
  float w = fract(modelPosition.x * material.freq + pow( 
     snoise(modelPosition.xyz * material.noiseFreq),
     material.noiseExp) * material.noiseAmp
  );

  vec3 color = mix(material.lightWoodColor, material.darkWoodColor, w); 

  fragmentColor = vec4(color, 1.0f);
  // fragmentColor = vec4(fract(modelPosition.x * 10.0f), 1.0f, 1.0f, 1.0f);
  fragmentColor.rgb *= scene.shadowColor;
}