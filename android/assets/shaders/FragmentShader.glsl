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
uniform sampler2D u_texPalette;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords); // gets the color of the pixel that it's drawing; this is the grayscale sprite's color before repaletting
    vec2 index = vec2(color.r, 0); // index refers to where to look in the palette image to find which color to draw
    gl_FragColor = vec4(texture2D(u_texPalette, index).rgba);
}