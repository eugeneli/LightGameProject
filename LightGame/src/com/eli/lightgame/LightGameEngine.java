package com.eli.lightgame;

import box2dLight.RayHandler;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.NPC;
import com.eli.lightgame.entities.Player;
import com.eli.lightgame.ui.LGInput;
import com.eli.lightgame.ui.LightGameStage;
import com.eli.lightgame.util.LGPreferences;

public class LightGameEngine
{
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private float width;
	private float height;
	
	private LGPreferences preferences;
	private boolean presentationMode = false;
	
	//Box2D stuff
	private World world;
	private Box2DDebugRenderer renderer;
	private RayHandler rayHandler; //handles lights
	
	//Touch stuff
	private LightGameStage LGstage;
	
	//Game Stuff
	private LevelStateManager levelState;
	private Level level;
	private BulletHandler bulletHandler;
	private EntityHandler entityHandler;
	private Player player;
	private int levelOverTimer = 200;
		
	public LightGameEngine(SpriteBatch sb, float w, float h, LGPreferences pref, boolean presentation)
	{
		presentationMode = presentation;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		width = w;
		height = h;
		batch = sb;
		preferences = pref;
		
		initialize(presentation, "data/levels/presentation.json");
	}
	
	public void initialize(boolean presentation, String levelJsonPath)
	{
		presentationMode = presentation;
		
		//Create and position camera
		camera = new OrthographicCamera(width, height);
		camera.position.set(width*0.5f, height*0.5f, 0);
		camera.update();
		
		//Create Box2D world
		world = new World(new Vector2(0,0), false);
		renderer = new Box2DDebugRenderer();
		renderer.setDrawBodies(false);
		rayHandler = new RayHandler(world);
		rayHandler.setCombinedMatrix(camera.combined);
		
		//Create handlers and game objects
		createCollisionListener();
		bulletHandler = new BulletHandler(world, rayHandler);
		entityHandler = new EntityHandler(world, rayHandler, bulletHandler, width, height);
		
		levelState = new LevelStateManager();
		
		//Create level object
		level = new Level(levelState, entityHandler, bulletHandler, rayHandler, world, width, height);
		
		
		if(!presentationMode)
		{
			level.loadLevel(Gdx.files.internal(levelJsonPath));
			player = level.getPlayer();
			
			//Create the stage for UI objects
			LGstage = new LightGameStage(camera, batch, entityHandler);
	        Gdx.input.setInputProcessor(LGstage.getStage());
	        
	        level.setZoomBounds(LGstage);
	        
	        //Show level title message 
	        LGstage.displayTitle(level.getTitleMessage()[0], level.getTitleMessage()[1]);
		}
		else
		{
			level.loadLevel(Gdx.files.internal("data/levels/presentation.json"));
			rayHandler.setAmbientLight(0.2f, 0.1f, 0.5f, 0.3f);
			camera.zoom = 8.0f;
			camera.position.set(0, 0, 0);
		}
	}
	
	public void setOnScreenControls(boolean bool)
	{
	//	usingOnScreenControls = bool;
	/*	
		if(usingOnScreenControls)
			Gdx.input.setInputProcessor(LGstage.getStage());
		else
			Gdx.input.setInputProcessor(new GestureDetector(input));*/
	}
	
	public void dispose()
	{
		renderer.dispose();
		world.dispose();
		entityHandler.dispose();
		rayHandler.dispose();
	}
	
	public void update()
	{
		//Step box2D physics
		world.step(1/30f, 8, 3);

		//Update bullets
		bulletHandler.updateBullets();
		
		//Update Entities
		entityHandler.update();
		
		if(!presentationMode)
		{
		//	if(usingOnScreenControls)
			//	player.moveJoystick((float)Math.atan2(LGstage.getTouchpad().getKnobPercentY(),LGstage.getTouchpad().getKnobPercentX()));
			
			camera.position.set(player.getPosition().x, player.getPosition().y, 0);
			
			LGstage.act(Gdx.graphics.getDeltaTime());
		}
		
		
	}
	
	public void render()
	{
		if(levelOverTimer >= 0 || !level.isOver())
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
		}
		
		if(level.isOver())
			levelOverTimer--;
		
		if(!presentationMode)
		{
			//Draw the UI
			LGstage.draw();
		}
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
						
						//f(objAId != 0 && objBId != 0) //Collide two NPCs
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
