attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_color = COLOR_ATTRIBUTE;
    v_color.a = v_color.a * 1.0039216; //* (256.0/255.0)
    v_texCoords = TEXCOORD_ATTRIBUTE0;
    gl_Position = u_projTrans * POSITION_ATTRIBUTE;
}