#version 120
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec4 palette[4];

void main() {
    gl_FragColor = palette[floor(texture2D(u_texture, v_texCoords).r * 3.9999)];
}