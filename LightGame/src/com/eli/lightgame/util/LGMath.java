package com.eli.lightgame.util;

import com.badlogic.gdx.math.Vector2;

public class LGMath
{
	public static float normalizeAngle(float angleRadians)
	{
		float newAngle = angleRadians;
	    while (newAngle <= Math.toRadians(-180)) newAngle += Math.toRadians(360);
	    while (newAngle > Math.toRadians(180)) newAngle -= Math.toRadians(360);
	    return newAngle;
	}
	
	public static float roundFloat(float value, int numPlaces)
	{
		double precision = Math.pow(10, numPlaces);
		return (float)((float)(Math.round(value * precision))/precision);
	}
	
	public static float normalizeAndRoundAngle(float angleRadians)
	{
		float unRounded = normalizeAngle(angleRadians);
		return roundFloat(unRounded, 2);
	}
	
	public static float distanceBetween(Vector2 p1, Vector2 p2)
	{
		return (float)Math.sqrt((Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y),2)));
	}
	
	public static float getRotationTo(Vector2 me, Vector2 target)
	{
		float toTargetX = (float)((target.x - me.x));
		float toTargetY = (float)((target.y - me.y));
		return (float)Math.atan2(toTargetY,toTargetX);
	}
}
