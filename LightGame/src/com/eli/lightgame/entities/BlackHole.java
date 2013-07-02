package com.eli.lightgame.entities;

import java.util.Random;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

public class BlackHole extends MassiveEntity
{
	private ParticleEffect giantEffect;
	private boolean alreadyMoving = false;
	private float originalDirection;
	private float originalVelocity;
	
	private RayHandler rayHandler;

	public BlackHole(World world, RayHandler rh, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos, String particlePath, float facingDirection, float velocity)
	{
		super("data/blackhole.png", aColor, rad);
		
		bulletHandler = bh;
		criticalRadius = critSize;
		canChangeColor = false;
		canChangeSize = true;
		lightSize = 10 * radius;
		ignoreSize = true;
		ignoreExistence = true;
		
		originalDirection = facingDirection;
		originalVelocity = velocity;

		rayHandler = rh;
		
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		circleDef.position.set(xPos, yPos);
		
		Body circleBody = world.createBody(circleDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(rad);
		
		FixtureDef circleFixture = new FixtureDef();
		circleFixture.shape = circleShape;
		circleFixture.density = 1.0f;
		circleFixture.friction = 1.0f;
		circleFixture.restitution = 0.0f;
		circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_ENTITY;
		circleFixture.filter.maskBits = LightGameFilters.MASK_ENTITY;
		
		circleBody.createFixture(circleFixture);
		
		PointLight pl = new PointLight(rayHandler, 200, Color.BLACK, 5*radius, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		lights.add(pl);
		
		//Set the variables in Entity
		entityBody = circleBody;
		
		giantEffect = new ParticleEffect();
		giantEffect.load(Gdx.files.internal(particlePath), Gdx.files.internal("data"));
		giantEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		giantEffect.start();
	}
	
	@Override
	public void updateSizes()
	{
		waitingToUpdateSize = true;
		
		size = 2 * radius;
		lightSize = 5 * radius;
		
		//increase the distance of the lights based on entity's size
		for(Light aLight : lights)
		{
			aLight.setDistance(lightSize);
		}
		
		//Increase texture size
		sprite.setSize(size, size);
		
		//Increase particle size
		//Fireball	
		giantEffect.getEmitters().get(0).getScale().setLow(2.5f * radius);
		giantEffect.getEmitters().get(0).getScale().setHigh(2.5f * radius);
	}
	
	public void update()
	{
		super.update();
		
		if(!alreadyMoving)
		{
			if(new Random().nextFloat() >= 0.5)
				entityBody.setAngularVelocity(-0.5f);
			else
				entityBody.setAngularVelocity(0.5f);
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(originalDirection) * originalVelocity),(float)(Math.sin(originalDirection) * originalVelocity)));
			alreadyMoving = true;
		}
		
		//update particle emitter's position
		giantEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		giantEffect.update(Gdx.graphics.getDeltaTime());
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.setColor(color);
		super.draw(batch);
		giantEffect.draw(batch, Gdx.graphics.getDeltaTime());
		batch.setColor(Color.WHITE);
	}
}