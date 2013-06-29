package com.eli.lightgame.entities;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.LightGameFilters;

public class LightCore extends Entity
{
	private ParticleEffect particleEffect;
	
	public LightCore(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos, float facingDirection, float velocity)
	{
		super("data/bubble.png", aColor, rad/4);
		criticalRadius = critSize;
		canChangeColor = true;
		canChangeSize = true;
		lightSize = 6f * radius;
		ignoreSize = true;
		ignoreExistence = true;
		
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		circleDef.position.set(xPos, yPos);
		
		Body circleBody = world.createBody(circleDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(rad/4);
		
		FixtureDef circleFixture = new FixtureDef();
		circleFixture.shape = circleShape;
		circleFixture.density = 1.0f;
		circleFixture.friction = 1.0f;
		circleFixture.restitution = 0.0f;
		circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_NEUTRAL_ENTITY;
		circleFixture.filter.maskBits = LightGameFilters.MASK_NEUTRAL_ENTITY;
		
		circleBody.createFixture(circleFixture);
		
		sprite.setSize(size, size);
		
		//Lights
		ArrayList<Light> coreLights = new ArrayList<Light>();
		PointLight pl = new PointLight(rayHandler, 200, aColor, lightSize, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		coreLights.add(pl);
		
		//Set the variables in Entity
		entityBody = circleBody;
		lights = coreLights;
		
		particleEffect = new ParticleEffect();
	    particleEffect.load(Gdx.files.internal("data/particleeffects/insides.p"), Gdx.files.internal("data"));
	    particleEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
	    particleEffect.start();
	    
	    //core particle size
	    particleEffect.getEmitters().get(0).getScale().setLow(2*radius);
	    particleEffect.getEmitters().get(0).getScale().setHigh(2*radius);
	    particleEffect.getEmitters().get(1).getScale().setLow(2*radius);
	    particleEffect.getEmitters().get(1).getScale().setHigh(2*radius);
	}

	@Override
	public void updateSizes() {
		// TODO Auto-generated method stub
		
	}
	
	public void update()
	{
		super.update();
		
		//update particle position
		particleEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		particleEffect.update(Gdx.graphics.getDeltaTime());
		
		//flicker the core's light
		float currentCoreLightDistance = lights.get(0).getDistance();
		
		if(currentCoreLightDistance <= radius*5)
		{
			dimCoreLight = false;
		}
		else if(currentCoreLightDistance >= radius*12)
		{
			dimCoreLight = true;
		}
		
		if(dimCoreLight)
		{
			lights.get(0).setDistance(currentCoreLightDistance-(flickerRate*5f));
		}
		else
			lights.get(0).setDistance(currentCoreLightDistance+(flickerRate*5f));
	}
	
	public void draw(SpriteBatch batch)
	{
		super.draw(batch);
		particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
	}
	
}
