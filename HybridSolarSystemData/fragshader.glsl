#version 410

in vec2 tc;
out vec4 out_color;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
//layout (binding=0) uniform sampler2D s;
uniform sampler2D s;
uniform bool isTexture;
uniform vec3 color;

void main(void)
{
   if(isTexture){
       out_color = texture(s,tc);
   }
    else{
       out_color = vec4(color, 1);
   }

}
