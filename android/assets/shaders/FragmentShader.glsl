#version 120
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform vec4 u_palette[4];
uniform sampler2D u_texture;

void main() {
    gl_FragColor = u_palette[int(texture2D(u_texture, v_texCoords).r * 3.9999)];
}