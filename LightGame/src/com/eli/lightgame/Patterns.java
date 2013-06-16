package com.eli.lightgame;

import java.util.ArrayList;

public class Patterns
{
	public static class Pattern
	{
		//Make sure numBullets is equal to the size of angles!!!!!
		public int numBullets;
		public ArrayList<Float> angles;
		
		public Pattern() {}
		
		public Pattern(int num, ArrayList<Float> ang)
		{
			numBullets = num;
			angles = ang;
		}
	}
	
	public static Pattern SingleShot = new Pattern();
	
	public Patterns()
	{
		ArrayList<Float> triangleAngles = new ArrayList<Float>();
		triangleAngles.add(new Float(1));
		//triangleAngles.add(new Float(-1.7));
		//triangleAngles.add(new Float(10));
		SingleShot = new Pattern(1, triangleAngles);
	}
}
