package com.eli.lightgame.entities;


import java.util.Random;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.util.LGMath;

public class GiantBoss extends Giant 
{
	private long lastTimeFired;
	private Random rand = new Random();
	
	public GiantBoss(World world, RayHandler rh, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos,
			String particlePath, EntityHandler eh, float facingDirection,
			float velocity, float angularVel) 
	{
		super(world, rh, bh, aColor, rad, critSize, xPos, yPos, particlePath, eh, facingDirection, velocity, angularVel);
		// TODO Auto-generated constructor stub
	}

	public void update()
	{
		super.update();
		
		//Attacks
		if(rand.nextDouble() > 0.99)
		{
			long currentFireTime = System.currentTimeMillis();
			if(currentFireTime - lastTimeFired > 300)
			{
				bulletHandler.createBossBulletsAndFire(radius, color, getPosition().x, getPosition().y, 500, LGMath.getRotationTo(getPosition(), entityHandler.getPlayer().getPosition()), 20, "data/particleeffects/redgiant2.p");
				addToRadius(-radius/50);
				lastTimeFired = currentFireTime;
			}
		}
	}
}
