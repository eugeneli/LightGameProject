package com.eli.lightgame;

import java.util.ArrayList;

import box2dLight.RayHandler;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.eli.lightgame.LightGame.GAMESTATE;
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
	private OrthographicCamera playerCamera; //Always follows player. Used to keep original camera within bounds
	
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
	private ArrayList<String> levelPaths = new ArrayList<String>();
	private int currentLevel;
	private BoundingBox[] levelBounds= new BoundingBox[4]; //4 boxes to determine how far camera is allowed to see. Clockwise starting from left
	private BulletHandler bulletHandler;
	private EntityHandler entityHandler;
	private Player player;
	private int levelOverTimer = 100;
	
	public LightGameEngine(SpriteBatch sb, float w, float h, LGPreferences pref, boolean presentation)
	{
		presentationMode = presentation;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		width = w;
		height = h;
		batch = sb;
		preferences = pref;
		
		//Read level index and store in array
        Json json = new Json();
		JsonValue levelIndex = json.fromJson(null, Gdx.files.internal("data/levels/levelindex.json"));
		
		JsonValue levels = levelIndex.get("Levels");
		for(int i = 0; i < levels.size; i++)
			levelPaths.add(levels.get(i).getString("Path"));
		
		initialize(presentation, -1);
	}
	
	public void initialize(boolean presentation, int levelIndex)
	{
		presentationMode = presentation;
		
		//Create and position camera
		camera = new OrthographicCamera(width, height);
		camera.position.set(width*0.5f, height*0.5f, 0);
		camera.update();
		
		playerCamera = new OrthographicCamera(width, height);
		playerCamera.position.set(width*0.5f, height*0.5f, 0);
		playerCamera.update();
		
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
			level.loadLevel(Gdx.files.internal(levelPaths.get(levelIndex)));
			currentLevel = levelIndex;
			
			player = level.getPlayer();
			levelBounds = level.getLevelBoundingBox();
			
			//Create the stage for UI objects
			LGstage = new LightGameStage(this, camera, batch, entityHandler);
	        Gdx.input.setInputProcessor(LGstage.getStage());
	        
	        level.setZoomBounds(LGstage);
	        
	        //Show level title message 
	        LGstage.displayTitle(level.getTitleMessage()[0], level.getTitleMessage()[1]);
	        
	        //Show tip if any
	        LGstage.displayTip(level.getTip());
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
	
	public boolean hasNextLevel()
	{
		return currentLevel < levelPaths.size();
	}
	
	public void loadNextLevel()
	{
		dispose();
		initialize(false, ++currentLevel);
		//Engine.setOnScreenControls(preferences.useOnScreenControls());
		LightGame.CURRENT_GAMESTATE = GAMESTATE.INGAME;
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
			
			playerCamera.zoom = camera.zoom;
			playerCamera.position.set(player.getPosition().x, player.getPosition().y, 0);
			
			if(playerCamera.frustum.boundsInFrustum(levelBounds[0]))
			{
				if(playerCamera.frustum.boundsInFrustum(levelBounds[1]))
				{
					camera.position.set(camera.position.x, camera.position.y, 0);
				}
				else if(playerCamera.frustum.boundsInFrustum(levelBounds[3]))
				{
					camera.position.set(camera.position.x, camera.position.y, 0);
				}
				else
					camera.position.set(camera.position.x, player.getPosition().y, 0);
			}
			else if(playerCamera.frustum.boundsInFrustum(levelBounds[1]))
			{
				if(playerCamera.frustum.boundsInFrustum(levelBounds[2]))
				{
					camera.position.set(camera.position.x, camera.position.y, 0);
				}
				else
					camera.position.set(player.getPosition().x, camera.position.y, 0);
			}
			else if(playerCamera.frustum.boundsInFrustum(levelBounds[2]))
			{
				if(playerCamera.frustum.boundsInFrustum(levelBounds[3]))
				{
					camera.position.set(camera.position.x, camera.position.y, 0);
				}
				else
					camera.position.set(camera.position.x, player.getPosition().y, 0);
			}
			else if(playerCamera.frustum.boundsInFrustum(levelBounds[3]))
			{
				if(playerCamera.frustum.boundsInFrustum(levelBounds[1]))
				{
					camera.position.set(camera.position.x, camera.position.y, 0);
				}
				else
					camera.position.set(player.getPosition().x, camera.position.y, 0);
			}
			
			if(!playerCamera.frustum.boundsInFrustum(levelBounds[0]) && !playerCamera.frustum.boundsInFrustum(levelBounds[1]) 
				&& !playerCamera.frustum.boundsInFrustum(levelBounds[2]) && !playerCamera.frustum.boundsInFrustum(levelBounds[3]))
				camera.position.set(playerCamera.position);
		}
		
		
	}
	
	public void render()
	{
		if(levelOverTimer >= 0 || !level.isOver())
		{
			update();
		}
		else
		{
			LGstage.displayEndMenu();
		}
		
		camera.update();
		playerCamera.update();
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
		
		if(level.isOver())
			levelOverTimer--;
		
		if(!presentationMode)
		{
			//Draw the UI
			LGstage.act(Gdx.graphics.getDeltaTime());
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
