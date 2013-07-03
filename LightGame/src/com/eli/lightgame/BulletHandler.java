package com.eli.lightgame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
import com.eli.lightgame.entities.BlackHole;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.Entity;
import com.eli.lightgame.entities.MassiveEntity;
import com.eli.lightgame.util.LGMath;

public class BulletHandler
{
	private class BulletDefinition
	{
		public float radius;
		public Color color;
		public Vector2 position;
		public int force;
		public float rotAngle;
	}
	
	private ArrayList<Bullet> bullets;
	
	private World world;
	private RayHandler rayHandler;
	private Bullet recentlyReturnedBullet;
	private EntityHandler entityHandler;
	
	private Queue<BulletDefinition> queuedExplosion = new LinkedList<BulletDefinition>();
	
	public BulletHandler(World w, RayHandler rh)
	{
		world = w;
		rayHandler = rh;
		bullets = new ArrayList<Bullet>();
	}
	
	public void setEntityHandler(EntityHandler eh)
	{
		entityHandler = eh;
	}
	
	public Bullet createBulletsAndFire(float shooterRadius, Color aColor, float entityX, float entityY, int forceScalar, float rotAngle)
	{
		return createBulletsAndFire(null, shooterRadius, aColor, entityX, entityY, forceScalar, rotAngle, 3);
	}
	
	public Bullet createBulletsAndFire(Entity target, float shooterRadius, Color aColor, float entityX, float entityY, int forceScalar, float rotAngle)
	{
		return createBulletsAndFire(target, shooterRadius, aColor, entityX, entityY, forceScalar, rotAngle, 3);
	}
	
	public Bullet createBulletsAndFire(Entity target, float shooterRadius, Color aColor, float entityX, float entityY, int forceScalar, float rotAngle, int bulRadScalar)
	{
		//Box2D stuff
		BodyDef bulletDef = new BodyDef();
		bulletDef.type = BodyType.DynamicBody;
		bulletDef.position.set((float)(entityX + (2*shooterRadius)*Math.cos(rotAngle)), (float)(entityY + (2*shooterRadius)*Math.sin(rotAngle)));
		
		Body bulletBody = world.createBody(bulletDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(shooterRadius/bulRadScalar);

		FixtureDef bulletFixture = new FixtureDef();
		bulletFixture.shape = circleShape;
		bulletFixture.density = 0.1f;
		bulletFixture.friction = 1.0f;
		bulletFixture.restitution = 0.0f;
		bulletFixture.filter.categoryBits = LightGameFilters.CATEGORY_NEUTRAL_ENTITY;
		bulletFixture.filter.maskBits = LightGameFilters.MASK_NEUTRAL_ENTITY;
		
		bulletBody.createFixture(bulletFixture);
		
		//Lighting stuff
		ArrayList<Light> bulletLights = new ArrayList<Light>();
		PointLight bl = new PointLight(rayHandler, 20, aColor, shooterRadius*5f, 0, 0);
		bl.attachToBody(bulletBody, 0,  0);
		bulletLights.add(bl);
		
		//Rotate bullet
		bulletBody.setTransform(bulletBody.getPosition(), rotAngle);
		
		//Bullet b = new Bullet("data/blankbullet.png", bulletBody, bulletLights, 200, bulletPattern.angles.get(i).floatValue());
		Bullet b = new Bullet("data/blankbullet.png", aColor, shooterRadius/10, bulletBody, bulletLights, (int)(shooterRadius*50), bulletBody.getAngle());
		
		b.setTarget(target);
		
		bullets.add(b);
		
		//Let the bullet body carry a pointer back to the bullet object
		bulletBody.setUserData(b);
		
		fireSingle(b, forceScalar);
		
		return b;
	}
	
	//new method for boss bullet
	public Bullet createBossBulletsAndFire(float shooterRadius, Color aColor, float entityX, float entityY, int forceScalar, float rotAngle, int bulRadScalar, String particlePath)
	{
		//Box2D stuff
		BodyDef bulletDef = new BodyDef();
		bulletDef.type = BodyType.DynamicBody;
		bulletDef.position.set((float)(entityX + (1.3*shooterRadius)*Math.cos(rotAngle)), (float)(entityY + (1.3*shooterRadius)*Math.sin(rotAngle)));
		
		Body bulletBody = world.createBody(bulletDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(shooterRadius/bulRadScalar);

		FixtureDef bulletFixture = new FixtureDef();
		bulletFixture.shape = circleShape;
		bulletFixture.density = 0.1f;
		bulletFixture.friction = 1.0f;
		bulletFixture.restitution = 0.0f;
		bulletFixture.filter.categoryBits = LightGameFilters.CATEGORY_NEUTRAL_ENTITY;
		bulletFixture.filter.maskBits = LightGameFilters.MASK_NEUTRAL_ENTITY;
		
		bulletBody.createFixture(bulletFixture);
		
		//Lighting stuff
		ArrayList<Light> bulletLights = new ArrayList<Light>();
		PointLight bl = new PointLight(rayHandler, 20, aColor, shooterRadius, 0, 0);
		bl.attachToBody(bulletBody, 0,  0);
		bulletLights.add(bl);
		
		//Rotate bullet
		bulletBody.setTransform(bulletBody.getPosition(), rotAngle);
		
		//Bullet b = new Bullet("data/blankbullet.png", bulletBody, bulletLights, 200, bulletPattern.angles.get(i).floatValue());
		Bullet b = new Bullet("data/blankbullet.png", aColor, shooterRadius/10, bulletBody, bulletLights, (int)(shooterRadius*10.5), bulletBody.getAngle());
		b.loadParticle(particlePath);
		b.setTarget(null);
		b.setImmortal(false);
		
		bullets.add(b);
		
		//Let the bullet body carry a pointer back to the bullet object
		bulletBody.setUserData(b);
		
		fireSingle(b, forceScalar);
		
		return b;
	}
	
	public void queueExplosionBullet(float shooterRadius, Color aColor, float entityX, float entityY, int forceScalar, float rotAngle)
	{
		BulletDefinition bulDef = new BulletDefinition();
		bulDef.radius = shooterRadius;
		bulDef.color = aColor;
		bulDef.position = new Vector2(entityX, entityY);
		bulDef.force = forceScalar;
		bulDef.rotAngle = rotAngle;
		queuedExplosion.add(bulDef);
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
	
	public Bullet getSomeBullet(Bullet currentTarget)
	{
		if(bullets.size() == 1)
			return bullets.get(0);
		
		if(bullets.contains(currentTarget))
			return currentTarget;
		else
		{
			for(int i = 0; i < bullets.size(); i++)
			{
				Bullet b = bullets.get(i);
				if(b != null && b != recentlyReturnedBullet)
				{
					recentlyReturnedBullet = b;
					return bullets.get(i);
				}
			}
		}
		return null;
	}
	
	public int getBulletNumber()
	{
		return bullets.size();
	}
	
	public void doGravity(Bullet bullet)
	{
		ArrayList<MassiveEntity> massiveEnts = entityHandler.getGravityEntities();
		for(MassiveEntity gravEntity : massiveEnts)
		{
			if(gravEntity instanceof BlackHole)
			{
				float distance = LGMath.distanceBetween(bullet.getPosition(),gravEntity.getPosition());
				if(distance <= 7*gravEntity.getRadius())
				{
					Body entityBody = bullet.getBody();
					
					float rotAngle = LGMath.getRotationTo(bullet.getPosition(), gravEntity.getPosition());
					float forceMultiplier = (float)(0.3f*gravEntity.getGravityMagnitude()*bullet.getMass()*gravEntity.getRadius() * 1/Math.pow(distance,2));
		
					if(forceMultiplier != (Float.POSITIVE_INFINITY))
						entityBody.applyForceToCenter(new Vector2((float)(Math.cos(rotAngle) * forceMultiplier),(float)(Math.sin(rotAngle) * forceMultiplier)), true);
				}
			}
			
		}
	}
	
	public void updateBullets()
	{
		for(int i = 0; i < bullets.size(); i++)
		{
			Bullet b = bullets.get(i);
			doGravity(b);
			b.update();
			
			if(!b.isImmortal())	
				b.decrementLife();
			
			if(b.getLife() <= 0)
			{
				removeBullet(b);
				i--;
			}
		}
				
		if(queuedExplosion.size() > 0)
		{
			if(!world.isLocked())
			{
				BulletDefinition bulDef = queuedExplosion.remove();
				createBulletsAndFire(bulDef.radius, bulDef.color, bulDef.position.x, bulDef.position.y, bulDef.force, bulDef.rotAngle);
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
