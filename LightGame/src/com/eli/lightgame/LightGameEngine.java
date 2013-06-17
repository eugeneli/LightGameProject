package com.eli.lightgame;

import box2dLight.RayHandler;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.NPC;
import com.eli.lightgame.entities.Player;

public class LightGameEngine
{
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private float width;
	private float height;
	
	//Box2D stuff
	private World world;
	private Box2DDebugRenderer renderer;
	private RayHandler rayHandler; //handles lights
	
	//UI stuff
	private LightGameStage LGstage;
	
	//Game Stuff
	Level level;
	Patterns pattern = new Patterns();
	BulletHandler bulletHandler;
	EntityHandler entityHandler;
	Player player;
		
	public LightGameEngine(SpriteBatch sb, float w, float h)
	{
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		width = w;
		height = h;
		batch = sb;
		
		//Create and position camera
		camera = new OrthographicCamera(width, height);
		camera.position.set(width*0.5f, height*0.5f, 0);
		camera.zoom = 2.5f;
		camera.update();
		
		//Create Box2D world
		world = new World(new Vector2(0,0), false);
		renderer = new Box2DDebugRenderer();
		//renderer.setDrawBodies(false);
		rayHandler = new RayHandler(world);
		rayHandler.setCombinedMatrix(camera.combined);
		
		//Create handlers and game objects
		createCollisionListener();
		bulletHandler = new BulletHandler(world, rayHandler);
		entityHandler = new EntityHandler(world, rayHandler, bulletHandler, width, height);
		
		//Create level
		level = new Level(entityHandler, world, width, height);
		level.loadLevel(Gdx.files.internal("data/levels/level1/level1.json"));
		player = level.getPlayer();
		
		
		//The ground. remove this later
		/*BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(0,3);
		
		Body groundBody = world.createBody(groundBodyDef);
		
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(camera.viewportWidth * 2, 3.0f);
		//groundBody.createFixture(groundBox, 0.0f);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.shape = groundBox;
		groundFixture.filter.categoryBits = LightGameEngine.CATEGORY_ENVIRONMENT;
		groundFixture.filter.maskBits = LightGameEngine.MASK_ENVIRONMENT;
		groundBody.createFixture(groundFixture); */
		
		//System.out.println(jsonVal.get("Enemies").next().get("x").asString());
		//System.out.println(jsonVal.get("Enemies").next().get("x").asString());
			
		//Create the stage for UI objects
		LGstage = new LightGameStage("data/touchBackground.png", "data/touchKnob.png", camera, batch, player);
        Gdx.input.setInputProcessor(LGstage.getStage());
        
        //rayHandler.setAmbientLight(0.2f, 0.1f, 0.5f, 0.3f);
	}
	
	public void dispose()
	{
		renderer.dispose();
		world.dispose();
		rayHandler.dispose();
	}
	
	public void update()
	{
		//Step box2D physics
		world.step(1/30f, 8, 3);
				
		player.move((float)Math.atan2(LGstage.getTouchpad().getKnobPercentY(),LGstage.getTouchpad().getKnobPercentX()));

		//Update bullets
		bulletHandler.updateBullets();
		
		//Update Entities
		entityHandler.update();
		
		camera.position.set(player.getPosition().x, player.getPosition().y, 0);
	}
	
	public void render()
	{
		update();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		rayHandler.setCombinedMatrix(camera.combined);
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		//draw level bg
		level.draw(batch);

		//draw entities
		entityHandler.draw(batch);
		
		//draw bullets
		bulletHandler.drawBullets(batch);
		
		batch.end();
		
		//Render box2D physics and lights
		renderer.render(world,  camera.combined);
		rayHandler.updateAndRender();
		
		//Draw the UI
		LGstage.getStage().act(Gdx.graphics.getDeltaTime());        
		LGstage.getStage().draw();
	}
	
	public void createCollisionListener()
	{
		world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact)
            {
            	Object ObjA = contact.getFixtureA().getBody().getUserData();
        		Object ObjB = contact.getFixtureB().getBody().getUserData();
        		
        		if(ObjA != null && ObjB != null)
        		{
        			//Check bullet collision
                	if(ObjA instanceof Bullet && !(ObjB instanceof Bullet))
                	{
                		entityHandler.collideWithBullet((Integer)contact.getFixtureB().getBody().getUserData(), (Bullet) contact.getFixtureA().getBody().getUserData());
                	}
                	else if(ObjB instanceof Bullet && !(ObjA instanceof Bullet))
                	{
                		entityHandler.collideWithBullet((Integer)contact.getFixtureA().getBody().getUserData(), (Bullet) contact.getFixtureB().getBody().getUserData());
                	}
                	else if(ObjA instanceof Integer || ObjB instanceof Integer) //Check body collision
                	{
						int objAId = (Integer)ObjA;
						int objBId = (Integer)ObjB;
						
						if(objAId != 0 && objBId != 0) //Collide two NPCs
							entityHandler.collideNPCs(objAId, objBId);
                	}
                	
        		}
            }

            @Override
            public void endContact(Contact contact) {
            }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}

        });
	}
}
