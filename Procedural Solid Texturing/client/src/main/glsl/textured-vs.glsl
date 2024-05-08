#version 300 es

in vec4 vertexPosition; //#vec4# A four-element vector [x,y,z,w].; We leave z and w alone.; They will be useful later for 3D graphics and transformations. #vertexPosition# attribute fetched from vertex buffer according to input layout spec
in vec4 vertexTexCoord;

uniform struct{
  mat4 modelMatrix;
} gameObject;

uniform struct{
  mat4 viewProjMatrix; 
} camera;

uniform struct {
  mat4 shadowMatrix;
  vec3 shadowColor;
} scene;

out vec4 modelPosition;
out vec4 worldPosition;
out vec4 texCoord;

void main(void) {
    mat4 translationMatrix = mat4(
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
        0.0, 2.5, 0.0, 1.0
    );

  modelPosition = vertexPosition;
  gl_Position = translationMatrix * vertexPosition * gameObject.modelMatrix * scene.shadowMatrix * camera.viewProjMatrix;
  worldPosition = vertexPosition * gameObject.modelMatrix;
  texCoord = vertexTexCoord;
}