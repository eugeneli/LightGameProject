package com.eli.lightgame.entities;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.LightGameFilters;

public class Blinker extends NPC
{
	public Blinker(World world, RayHandler rayHandler, BulletHandler bh, Color aColor, float rad, float xPos, float yPos)
	{
		super("data/blankbullet.png", aColor, rad);
		lightSize = 5f * radius;
		criticalRadius = 10*radius;
		
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
		circleFixture.restitution = 1.0f;
		circleFixture.filter.categoryBits = LightGameFilters.CATEGORY_GHOST;
		circleFixture.filter.maskBits = LightGameFilters.MASK_GHOST;
		
		circleBody.createFixture(circleFixture);
		
		//Lights
		ArrayList<Light> blinkerLights = new ArrayList<Light>();
		PointLight pl = new PointLight(rayHandler, 50, color, lightSize, 0, 0);
		pl.attachToBody(circleBody, 0,  0);
		blinkerLights.add(pl);
		
		//Set the variables in Entity
		entityBody = circleBody;
		lights = blinkerLights;
	}
	
	public void setFlickerRate(float flicker)
	{
		flickerRate = flicker;
	}
	
	public void update()
	{
		super.update();
		
		//flicker the core's light
		float currentCoreLightDistance = lights.get(0).getDistance();
		
		if(currentCoreLightDistance <= radius)
		{
			dimCoreLight = false;
		}
		else if(currentCoreLightDistance >= radius*5)
		{
			dimCoreLight = true;
		}
		
		if(dimCoreLight)
		{
			lights.get(0).setDistance(currentCoreLightDistance-flickerRate);
		}
		else
			lights.get(0).setDistance(currentCoreLightDistance+flickerRate);
	}

	@Override
	public void doAI()
	{
		// TODO Auto-generated method stub
		
	}

}
