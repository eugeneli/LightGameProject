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

public class RedGiant extends NPC
{
	private ParticleEffect redGiantEffect;

	public RedGiant(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float critSize, float xPos, float yPos)
	{
		super("data/particle-fire.png", aColor, rad);
		
		bulletHandler = bh;
		criticalRadius = critSize;
		canChangeColor = false;
		canChangeSize = true;
		lightSize = 10 * radius;
		
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.StaticBody;
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
		ArrayList<Light> redGiantLights = new ArrayList<Light>();
		PointLight pl = new PointLight(rayHandler, 400, color, rad*10, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		redGiantLights.add(pl);
		
		//Set the variables in Entity
		entityBody = circleBody;
		lights = redGiantLights;
		
		redGiantEffect = new ParticleEffect();
		redGiantEffect.load(Gdx.files.internal("data/particleeffects/redgiant2.p"), Gdx.files.internal("data"));
		redGiantEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		redGiantEffect.start();
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

		//Increase texture size
		sprite.setSize(size, size);
		
		//Increase particle size
		//Fireball	
		redGiantEffect.getEmitters().get(0).getScale().setLow(3 * radius);
		redGiantEffect.getEmitters().get(0).getScale().setHigh(3 * radius);
		
		//Core
		redGiantEffect.getEmitters().get(1).getScale().setLow(radius);
		redGiantEffect.getEmitters().get(1).getScale().setHigh(radius);
	}
	
	public float getCritSize()
	{
		return criticalRadius;
	}
	
	public void update()
	{
		super.update();
		
		//update particle emitter's position
		redGiantEffect.setPosition(entityBody.getPosition().x, entityBody.getPosition().y);
		redGiantEffect.update(Gdx.graphics.getDeltaTime());
	}
	
	public void draw(SpriteBatch batch)
	{
		super.draw(batch);
		
		redGiantEffect.draw(batch, Gdx.graphics.getDeltaTime());
	}

	@Override
	public void doAI() {
		// TODO Auto-generated method stub
		
	}
	
}