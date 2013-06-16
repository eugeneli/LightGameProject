package com.eli.lightgame;

import java.util.ArrayList;

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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.Patterns.Pattern;
import com.eli.lightgame.entities.Bullet;

public class BulletHandler
{
	public ArrayList<Bullet> bullets;
	
	private World world;
	private RayHandler rayHandler;
	
	public BulletHandler(World w, RayHandler rh)
	{
		world = w;
		rayHandler = rh;
		bullets = new ArrayList<Bullet>();
	}
	
	public void createBulletsAndFire(Pattern bulletPattern, float shooterRadius, Color aColor, float entityX, float entityY, int forceScalar, float rotAngle)
	{
		for(int i = 0; i < bulletPattern.numBullets; i++)
		{	
			//Box2D stuff
			BodyDef bulletDef = new BodyDef();
			bulletDef.type = BodyType.DynamicBody;
			bulletDef.position.set(entityX, entityY);
			
			Body bulletBody = world.createBody(bulletDef);
			
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(0.5f);
			
			FixtureDef bulletFixture = new FixtureDef();
			bulletFixture.shape = circleShape;
			bulletFixture.density = 0.1f;
			bulletFixture.friction = 1.0f;
			bulletFixture.restitution = 0.0f;
			bulletFixture.filter.categoryBits = LightGameFilters.CATEGORY_PLAYER;
			bulletFixture.filter.maskBits = LightGameFilters.MASK_PLAYER;
			
			bulletBody.createFixture(bulletFixture);
			
			//Lighting stuff
			ArrayList<Light> bulletLights = new ArrayList<Light>();
			PointLight bl = new PointLight(rayHandler, 10, aColor, shooterRadius*20.5f, 0, 0);
			bl.attachToBody(bulletBody, 0,  0);
			bulletLights.add(bl);
			
			//Rotate bullet
			bulletBody.setTransform(bulletBody.getPosition(), rotAngle);
			bulletBody.setAngularVelocity(0);
			
			//Bullet b = new Bullet("data/blankbullet.png", bulletBody, bulletLights, 200, bulletPattern.angles.get(i).floatValue());
			Bullet b = new Bullet("data/blankbullet.png", aColor, shooterRadius, bulletBody, bulletLights, (int)(shooterRadius*70), bulletBody.getAngle());
			bullets.add(b);
			
			//Let the bullet body carry a pointer back to the bullet object
			bulletBody.setUserData(b);
			
			fireSingle(b, forceScalar);
		}
	}
	
	public void fireSingle(Bullet bullet, int forceScalar)
	{
		bullet.move(new Vector2(bullet.getAngleX() * forceScalar, bullet.getAngleY() * forceScalar));
	}
	
	public void fire(int forceScalar)
	{
		for(Bullet bullet : bullets)
		{
			bullet.move(new Vector2(bullet.getAngleX() * forceScalar, bullet.getAngleY() * forceScalar));
		}
	}
	
	public void directedFire(int forceScalar, float rotAngle)
	{
		for(Bullet bullet : bullets)
		{
			bullet.move(new Vector2(rotAngle * forceScalar, rotAngle * forceScalar));
		}
	}
	
	public void removeBullet(Bullet b)
	{
		b.dispose(); //Disables the lights - inherited from Entity
		world.destroyBody(b.getBody());
		bullets.remove(b);
	}
	
	public void updateBullets()
	{
		for(int i = 0; i < bullets.size(); i++)
		{
			bullets.get(i).update();
			bullets.get(i).decrementLife();
			
			if(bullets.get(i).getLife() <= 0)
			{
				removeBullet(bullets.get(i));
				i--;
			}
		}
	}
	
	public void drawBullets(SpriteBatch batch)
	{
		for(Bullet bullet : bullets)
		{
			bullet.draw(batch);
		}
	}
	
}
