package com.eli.lightgame.util;

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
}
