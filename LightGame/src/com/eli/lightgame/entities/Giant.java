package com.eli.lightgame.entities;

import java.util.ArrayList;
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
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.EntityHandler.EntityType;
import com.eli.lightgame.LightGameFilters;

public class Giant extends MassiveEntity
{
	private ParticleEffect giantEffect;
	private boolean dying = false;
	private boolean alreadyMoving = false;
	private float originalRadius;
	private float originalDirection;
	private float originalVelocity;
	private float angularVelocity;
	
	private EntityHandler entityHandler;
	private RayHandler rayHandler;

	public Giant(World world, RayHandler rh, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos, String particlePath, EntityHandler eh, float facingDirection, float velocity, float angularVel)
	{
		super("data/particle-fire.png", aColor, rad);
		
		bulletHandler = bh;
		criticalRadius = critSize;
		canChangeColor = false;
		canChangeSize = true;
		lightSize = 10 * radius;
		ignoreSize = true;
		ignoreExistence = true;
		
		originalDirection = facingDirection;
		originalVelocity = velocity;
		angularVelocity = angularVel;
		
		entityHandler = eh;
		rayHandler = rh;
		originalRadius = rad; //Used for exploding
		
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
		
		//Lights
		ArrayList<Light> blueGiantLights = new ArrayList<Light>();
		PointLight pl = new PointLight(rayHandler, 400, aColor, rad*10, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		blueGiantLights.add(pl);
		
		//Set the variables in Entity
		entityBody = circleBody;
		lights = blueGiantLights;
		
		giantEffect = new ParticleEffect();
		giantEffect.load(Gdx.files.internal(particlePath), Gdx.files.internal("data"));
		giantEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		giantEffect.start();
	}
	
	public float getOriginalRadius()
	{
		return originalRadius;
	}
	
	public void doExplosion()
	{
		if(canBlackHole)
			entityHandler.queueEntityCreation(EntityType.BLACKHOLE, this);
		else
			entityHandler.queueEntityCreation(EntityType.CORE, this);
		
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 0f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 0.4f); //
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 0.79f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 1.0f);//
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 1.57f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 2.0f);//
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 2.36f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 2.7f);//
		
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 3.14f);
		
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 5.76f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 5.5f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 5f);//
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 4.71f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 4.3f);//
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 3.93f);
		bulletHandler.queueExplosionBullet(originalRadius*0.7f, color, getPosition().x, getPosition().y, (int) (2 * originalRadius), 3.5f); //
		
		radius = 0;
		toBeDeleted(true);
		updateSizes();
	}
	
	public void explode()
	{
		dying = true;
	}
	
	@Override
	public void updateSizes()
	{
		waitingToUpdateSize = true;
		
		size = 2 * radius;
		lightSize = 10 * radius;
		
		//increase the distance of the lights based on entity's size
		for(Light aLight : lights)
		{
			aLight.setDistance(lightSize);
		}
		
		if(dying)
		{
			if(originalRadius/4 > radius)
			{
				for(Light aLight : lights)
					aLight.setDistance(lightSize*0.8f);
			}
			else
			{
				for(Light aLight : lights)
					aLight.setDistance(lightSize*3f);
			}
		}

		//Increase texture size
		sprite.setSize(size, size);
		
		//Increase particle size
		//Fireball	
		giantEffect.getEmitters().get(0).getScale().setLow(3 * radius);
		giantEffect.getEmitters().get(0).getScale().setHigh(3 * radius);
		
		//Core
		giantEffect.getEmitters().get(1).getScale().setLow(radius);
		giantEffect.getEmitters().get(1).getScale().setHigh(radius);
	}
	
	public void update()
	{
		super.update();
		
		if(dying)
		{
			addToRadius(-3);
			
			if(radius - 3 <= 2)
				doExplosion();
		}
		
		if(!alreadyMoving)
		{
			if(angularVelocity == -1)
				entityBody.setAngularVelocity(new Random().nextFloat()*3.0f+0.35f);
			else
				entityBody.setAngularVelocity(angularVelocity);
			
			entityBody.setLinearVelocity(new Vector2((float)(Math.cos(originalDirection) * originalVelocity),(float)(Math.sin(originalDirection) * originalVelocity)));
			alreadyMoving = true;
		}
		
		//update particle emitter's position
		giantEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		giantEffect.update(Gdx.graphics.getDeltaTime());
	}
	
	public void draw(SpriteBatch batch)
	{
		super.draw(batch);
		
		giantEffect.draw(batch, Gdx.graphics.getDeltaTime());
	}
}