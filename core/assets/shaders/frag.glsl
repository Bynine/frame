#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform float alpha = 1;
uniform float red = 1;
uniform float gb = 1;

void main() {
		vec3 color = texture2D(u_texture, v_texCoords).rgb * v_color;
		float grey = (color.g + color.b + color.r) / 3.0f;
		if (grey < 0.14f){
		    color.r = 0;
		    color.g = 0.03f;
       		color.b = 0.06f;
		}
        gl_FragColor = vec4(color, texture2D(u_texture, v_texCoords).a * alpha);
}