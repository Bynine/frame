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

uniform float offset[5] = float[]( 0.0, 1.0, 2.0, 3.0, 4.0 );
uniform float weight[5] = float[]( 0.2270270270, 0.1945945946, 0.1216216216,
                                   0.0540540541, 0.0162162162 );

void main(){
//    v_color = texture2D( v_texCoords, vec2(gl_FragCoord)/1024.0 ) * weight[0];
//    for (int i=1; i<5; i++) {
//        v_color +=
//            texture2D( v_texCoords, ( vec2(gl_FragCoord)+vec2(0.0, offset[i]) )/1024.0 )
//                * weight[i];
//        v_color +=
//            texture2D( v_texCoords, ( vec2(gl_FragCoord)-vec2(0.0, offset[i]) )/1024.0 )
//                * weight[i];
//    }
    vec3 color = texture2D(u_texture, v_texCoords).rgb * v_color;
    for (int i=1; i<2; i++) {
    	color += texture2D(v_texCoords, v_texCoords).rgb * v_color;
    }
    gl_FragColor = vec4(color, texture2D(u_texture, v_texCoords).a * alpha);
}