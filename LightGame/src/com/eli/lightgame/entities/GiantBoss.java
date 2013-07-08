package com.eli.lightgame.entities;


import java.util.Random;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.AudioHandler;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.EntityHandler.EntityType;
import com.eli.lightgame.util.LGMath;

public class GiantBoss extends Giant 
{
	private long lastTimeFired = 0;
	private long lastTimeWaved = 0;
	private long lastTimeGravIncreased = 0;
	private long gravityIncreaseStart = 0;
	private boolean gravityIncreased = false;
	private Random rand = new Random();
	private AudioHandler audio;
	
	private final float percentageOfOriginal = 0.7f; //Percentage of original radius to allow shrinking from own attacks
	private final float percentageOfCritRadius = 0.9f; //Percentage of critical radius to allow growing from own attacks
	
	public GiantBoss(World world, RayHandler rh, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos,
			String particlePath, EntityHandler eh, float facingDirection,
			float velocity, float angularVel, boolean isDynamic) 
	{
		super(world, rh, bh, aColor, rad, critSize, xPos, yPos, particlePath, eh, facingDirection, velocity, angularVel, isDynamic);
		
		ignoreExistence = false;
		updateSizes();
		
		audio = AudioHandler.getInstance();
		audio.loadTmpSound("fireball", "data/audio/sounds/fireball.ogg");
		audio.loadTmpSound("fireblast", "data/audio/sounds/fireblast.ogg");
		audio.loadTmpSound("sunnoise", "data/audio/sounds/sunnoise.ogg");
		
		audio.loopTmpSound("sunnoise");
	}
	
	public void doExplosion()
	{
		if(canBlackHole)
			entityHandler.queueEntityCreation(EntityType.BLACKHOLE, this);
		else
			entityHandler.queueEntityCreation(EntityType.CORE, this);
		
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 0f, "data/particleeffects/redgiant2.p");
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 0.79f, "data/particleeffects/redgiant2.p");
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 1.57f, "data/particleeffects/redgiant2.p");
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 2.36f, "data/particleeffects/redgiant2.p");
		
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 3.14f, "data/particleeffects/redgiant2.p");
		
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 5.76f, "data/particleeffects/redgiant2.p");
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 5.5f, "data/particleeffects/redgiant2.p");
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 4.71f, "data/particleeffects/redgiant2.p");
		bulletHandler.createBossBulletsAndFire(originalRadius*0.7f, originalRadius/15, color, getPosition().x, getPosition().y, 500, 3.93f, "data/particleeffects/redgiant2.p");
		
		radius = 0;
		toBeDeleted(true);
		updateSizes();
	}

	public void update()
	{
		super.update();
		
		//How long should increased gravity be turned on?
		if(gravityIncreased && System.currentTimeMillis() - gravityIncreaseStart > 6000)
		{
			setGravityMagnitude(5000);
			entityBody.setAngularVelocity(new Random().nextFloat()*3.0f+0.35f);
			gravityIncreaseStart = System.currentTimeMillis();
			gravityIncreased = false;
			
			audio.stopTmpSound("sunnoise");
			audio.loopTmpSound("sunnoise");
		}
		
		//Chance of attacking
		if(rand.nextDouble() > 0.96)
		{
			//Directed fire
			if(rand.nextDouble() > 0.65)
			{
				long currentFireTime = System.currentTimeMillis();
				if(currentFireTime - lastTimeFired > 380)
				{
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/10, color, getPosition().x, getPosition().y, 500, LGMath.getRotationTo(getPosition(), entityHandler.getPlayer().getPosition()), "data/particleeffects/redgiant2.p");
					addToRadius(-radius/60);
					lastTimeFired = currentFireTime;
					
					audio.playTmpSound("fireball");
					
					//fire another 2 times?
					for(int i = 0; i < 3; i++)
					{
						currentFireTime = System.currentTimeMillis();
						if(rand.nextDouble() > 0.5 && currentFireTime - lastTimeFired > 300)
						{
							bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/10, color, getPosition().x, getPosition().y, 500, LGMath.getRotationTo(getPosition(), entityHandler.getPlayer().getPosition()), "data/particleeffects/redgiant2.p");
							lastTimeFired = currentFireTime;
							
							//Only decrease size if over a certain radius
							if(radius >= percentageOfOriginal * criticalRadius)
								addToRadius(-radius/50);
							
							audio.playTmpSound("fireball");
						}
					}
				}
			}
			
			if(rand.nextDouble() > 0.97) //Semicircle fireballs
			{
				long currentFireTime = System.currentTimeMillis();
				if(currentFireTime - lastTimeWaved > 7000)
				{
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 2.36f, "data/particleeffects/redgiant2.p");
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 2.61f, "data/particleeffects/redgiant2.p");
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 2.88f, "data/particleeffects/redgiant2.p");
					
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 3.14f, "data/particleeffects/redgiant2.p");
					
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 3.4f, "data/particleeffects/redgiant2.p");
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 3.67f, "data/particleeffects/redgiant2.p");
					bulletHandler.createBossBulletsAndFire(radius*0.8f, radius/15, color, getPosition().x, getPosition().y, 500, 3.93f, "data/particleeffects/redgiant2.p");
					
					//Only decrease size if over a certain radius
					if(radius >= percentageOfOriginal * criticalRadius)
						addToRadius(-radius/20);
					
					lastTimeWaved = currentFireTime;
					
					audio.playTmpSound("fireblast");
				}
			}
			
			if(rand.nextDouble() > 0.96) //Gravity increase
			{
				long currentFireTime = System.currentTimeMillis();
				if(currentFireTime - lastTimeGravIncreased > 6000)
				{
					setGravityMagnitude(120000);
					entityBody.setAngularVelocity(10f);
					gravityIncreaseStart = currentFireTime;
					gravityIncreased = true;
					
					//Only increase size if over a certain radius
					if(radius <= percentageOfCritRadius * criticalRadius)
						addToRadius(radius/20);
					
					lastTimeGravIncreased = currentFireTime;
					
					audio.stopTmpSound("sunnoise");
					audio.loopTmpSound("sunnoise", 0.5f, 1.5f, 0);
				}
			}
		}	
	}
	
	public void dispose()
	{
		super.dispose();
		
		audio.removeTmpSound("sunnoise");
		audio.removeTmpSound("fireball");
		audio.removeTmpSound("fireblast");
	}
}
