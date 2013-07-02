#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture;
varying vec2 v_texCoords;

uniform float distortion = 10.3;
uniform float zoom = 1;
uniform float radius;
uniform vec2 center;
uniform float scale = 1;

vec2 radialDistortion(vec2 coord)
{
	vec2 cc = coord - 0.5;
	float dist = dot(cc, cc) * distortion;
	return (coord + cc * (1.0 + dist) * dist);
}

vec2 Distort(vec2 p)
{
    float theta  = atan(p.y, p.x);
    float radius = length(p);
    radius = pow(radius, 100);
    p.x = radius * cos(theta);
    p.y = radius * sin(theta);
    return 0.5 * (p + 1.0);
}

 void main()
 {
 	float aspectRatio = 1280.0/720.0;
    vec2 v_texCoordsToUse = vec2(v_texCoords.x, (v_texCoords.y * aspectRatio + 0.5 - 0.5 * aspectRatio));
    float dist = distance(center, v_texCoordsToUse);
   // v_texCoordsToUse = v_texCoords;
    
    if (dist < radius)
    {
        v_texCoordsToUse -= center;
        highp float percent = 1.0 - ((radius - dist) / radius) * scale;
        percent = percent * percent;
        
        v_texCoordsToUse = v_texCoordsToUse * percent;
        v_texCoordsToUse += center;
    }
    
    gl_FragColor = texture2D(u_texture, v_texCoordsToUse );    
 }