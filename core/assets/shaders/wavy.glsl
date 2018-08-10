#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform float time;
uniform float resolutionx;
uniform float resolutiony;
uniform float positionx;
uniform float positiony;
uniform float rippleSpeed = 640;

void main() {
	vec3 color = texture2D(u_texture, v_texCoords).rgb * v_color;
	
	vec2 resolution = vec2(resolutionx, resolutiony);
	vec2 position = vec2(positionx, positiony);
	
	vec2 cPos = -1.0 + 2.0 * gl_FragCoord.xy / resolution.xy;
	float cLength = length(cPos);

	position.x = mod(positionx, resolutionx);
	position.y = mod(positiony, resolutiony);
	vec2 uv = -position.xy/320.0f + gl_FragCoord.xy / resolution.xy + (cPos/cLength) * cos(cLength * 16.0 - time * 4.0) *0.03;
	vec3 col = texture2D(u_texture,uv).xyz;
	vec3 coldcopy = col;

	gl_FragColor = vec4(col, texture2D(u_texture, v_texCoords).a);
	
}