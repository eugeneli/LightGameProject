package com.eli.lightgame.entities;

import java.util.ArrayList;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.LightGameFilters;
import com.eli.lightgame.Patterns.Pattern;

public class Player extends Entity
{
	private BulletHandler bulletHandler;
	//private ParticleEffect particleEffect;
	
	@SuppressWarnings("unchecked")
	public Player(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float xPos, float yPos)
	{
		super("data/galaxy.png", aColor, rad);
		
		bulletHandler = bh;
		canChangeColor = true;
		canChangeSize = true;
		lightSize = 4 * radius;
		
		//Create player
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		circleDef.position.set(xPos, yPos);
		
		Body circleBody = world.createBody(circleDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(rad);
		
		FixtureDef circleFixture = new FixtureDef();
		circleFixture.shape = circleShape;
		circleFixture.density = 0.4f;
		circleFixture.friction = 1.0f;
		circleFixture.restitution = 0.0f;
		//circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_PLAYER;
		//circleFixture.filter.maskBits = LightGameFilters.MASK_PLAYER;
		
		circleBody.createFixture(circleFixture);
		
		//player lights
		ArrayList<Light> playerLights = new ArrayList<Light>();
		ConeLight cl = new ConeLight(rayHandler, 50, color, lightSize, 0, 0, 0, 30);
		cl.attachToBody(circleBody, 2, 0);
		playerLights.add(cl);
		
		PointLight pl = new PointLight(rayHandler, 60, color, lightSize, 0, 0);
		//PointLight pl2 = new PointLight(rayHandler, 100, Color.DARK_GRAY, 10, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		//pl2.attachToBody(circleBody, 0, 0);
		playerLights.add(pl);
		//playerLights.add(pl2);
		
		//Set the variables in Entity
		entityBody = circleBody;
		lights = (ArrayList<Light>) playerLights.clone();
		
		/*particleEffect = new ParticleEffect();
	    particleEffect.load(Gdx.files.internal("data/particleeffects/effect.p"), Gdx.files.internal("data"));
	    particleEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
	    particleEffect.start();*/
	}
	
	public void fire(Pattern pattern, int forceScalar)
	{
		bulletHandler.createBulletsAndFire(this, pattern, radius, color, getPosition().x, getPosition().y, forceScalar, entityBody.getAngle());
		
		addToRadius(-radius/10); //Decrease in radius equal to bullet's radius
		System.out.println("Shrunk radius: " + radius);
	}
	
	public void move(float rotAngle)
	{
		if(rotAngle != 0)
		{
			entityBody.setAngularVelocity(0);
			float totalRotation = rotAngle - entityBody.getAngle();
			
			while ( totalRotation < Math.toRadians(-180)) totalRotation += Math.toRadians(360);
			while ( totalRotation > Math.toRadians(180)) totalRotation -= Math.toRadians(360);
			
			float change = 0.3f;
			float newAngle = entityBody.getAngle() + Math.min(change,  Math.max(-change,totalRotation));
			
			entityBody.setTransform(entityBody.getPosition(), newAngle);
			entityBody.applyForceToCenter(new Vector2((float)(Math.cos(entityBody.getAngle()) * (10*radius)),(float)(Math.sin(entityBody.getAngle()) * (10*radius))), true);
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(entityBody.getAngle()) * (20)),(float)(Math.sin(entityBody.getAngle()) * (20))));
		}

		this.update();
	}
	
	@Override
	public void updateSizes()
	{
		waitingToUpdateSize = true;
		
		size = 2 * radius;
		lightSize = 4 * radius;
		
		//increase the distance of the lights based on entity's size
		for(Light aLight : lights)
		{
			aLight.setDistance(lightSize);
		}
		
		//Increase texture size
		sprite.setSize(size, size);
		
		System.out.println("Update radius: " + radius);
	}
	
	public void update()
	{
		super.update();
		
		//update particle position
		//particleEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		//particleEffect.update(Gdx.graphics.getDeltaTime());
	}
	
	public void draw(SpriteBatch batch)
	{
		super.draw(batch);
		//particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
	}
}
