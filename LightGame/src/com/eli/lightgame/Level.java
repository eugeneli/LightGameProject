package com.eli.lightgame;

import java.util.LinkedList;
import java.util.Queue;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.eli.lightgame.EntityHandler.EntityType;
import com.eli.lightgame.entities.Blinker;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.Player;
import com.eli.lightgame.ui.LightGameStage;
import com.eli.lightgame.winconditions.NoOtherLumis;
import com.eli.lightgame.winconditions.OnlyEntityLeft;
import com.eli.lightgame.winconditions.NoMoreBullets;
import com.eli.lightgame.winconditions.PlayerIsLargest;

public class Level
{
	private Texture background;
	private Texture outerBackground;
	private ParticleEffect particles;
	
	private World world;
	private float width;
	private float height;
	private boolean isPresentation = false;
	
	private float minZoom;
	private float maxZoom;
	private float defZoom;
	
	private LevelStateManager levelState;
	private EntityHandler entityHandler;
	private BulletHandler bulletHandler;
	private RayHandler rayHandler;
	
	private String[] titleMessage = new String[2];
	private Queue<String> tips = new LinkedList<String>();
	
	public Level(LevelStateManager levstate, EntityHandler eh, BulletHandler bh, RayHandler rh, World theWorld, float w, float h)
	{
		entityHandler = eh;
		bulletHandler = bh;
		rayHandler = rh;
		world = theWorld;
		width = w;
		height = h;
		levelState = levstate;
	}

	public void loadLevel(FileHandle levelJson)
	{
		Json json = new Json();
		JsonValue jsonVal = json.fromJson(null, levelJson);
		
		String levelType = jsonVal.getString("LevelType");
		
		background = new Texture(jsonVal.getString("Background"));
		outerBackground = new Texture("data/levels/outerspace.gif");
		
		if(jsonVal.getString("WorldShape").equals("RECTANGLE"))
			createWorldBoundary(0);
		
		//Get player data and create player
		if(!levelType.equals("NONE"))
		{
			Color PlayerColor = convertStringToColor(jsonVal.get("PlayerColor").asString());
			float playerRadius = jsonVal.get("PlayerRadius").asFloat();
			float playerCritMult = jsonVal.getFloat("PlayerCritRadiusMult");
			float playerSpawnX = jsonVal.get("PlayerSpawn").getFloat(0);
			float playerSpawnY = jsonVal.get("PlayerSpawn").getFloat(1);
			entityHandler.createEntity(EntityType.PLAYER, PlayerColor, playerRadius, playerCritMult, playerSpawnX, playerSpawnY, 0f, 0f); //create player
		}
		else
			isPresentation = true;
		
		//Store level's title message
		titleMessage[0] = jsonVal.getString("Title");
		titleMessage[1] = jsonVal.getString("Mission");
		
		//Store level's tips, if any
		for(int i = 0; i < jsonVal.get("Tips").size; i++)
		{
			tips.add(jsonVal.get("Tips").getString(i));
		}
		
		//Load particle effect if needed
		String particlePath = jsonVal.get("ParticleEffect").getString("Path");
		if(!particlePath.equals(""))
		{
			particles = new ParticleEffect();
			particles.load(Gdx.files.internal(particlePath), Gdx.files.internal("data"));
			particles.setPosition(0, 0);
			particles.start();
		}
		else
			particles = null;
		
		//Set world ambience
		float ambienceR = jsonVal.get("Ambience").getFloat(0);
		float ambienceG = jsonVal.get("Ambience").getFloat(1);
		float ambienceB = jsonVal.get("Ambience").getFloat(2);
		float ambienceA = jsonVal.get("Ambience").getFloat(3);
		
		rayHandler.setAmbientLight(ambienceR, ambienceG, ambienceB, ambienceA);
		
		//Set zoom bounds
		minZoom = jsonVal.getFloat("minZoom");
		maxZoom = jsonVal.getFloat("maxZoom");
		defZoom = jsonVal.getFloat("defZoom");
		
		for(int i = 0; i < jsonVal.get("Enemies").size; i++)
		{
			JsonValue enemy = jsonVal.get("Enemies").get(i);
			int enemyType = enemy.getInt("EnemyType");
			switch(enemyType)
			{
				case 0: //Case 0: Red Giant
					Color rgColor = convertStringToColor(enemy.getString("Color"));
					float rgRadius = enemy.getFloat("Radius");
					float rgSpawnX = enemy.getFloat("x");
					float rgSpawnY = enemy.getFloat("y");
					
					entityHandler.createEntity(EntityType.RED_GIANT, rgColor, rgRadius, 1.3f, rgSpawnX, rgSpawnY, 0f, 0f); //create a red giant
					break;
				case 1: //Case 1: Drifter
					Color dColor = convertStringToColor(enemy.getString("Color"));
					float dRadius = enemy.getFloat("Radius");
					float dCritMult = enemy.getFloat("CritRadiusMult");
					float dSpawnX = enemy.getFloat("x");
					float dSpawnY = enemy.getFloat("y");
					float dFacingDirec = enemy.getFloat("Direction");
					float dVelocity = enemy.getFloat("Velocity");
					
					entityHandler.createEntity(EntityType.DRIFTER, dColor, dRadius, dCritMult, dSpawnX, dSpawnY, dFacingDirec, dVelocity); //create a drifter
					break;
				case 2: //Case 2: Blinker
					Color bColor = convertStringToColor(enemy.getString("Color"));
					float bRadius = enemy.getFloat("Radius");
					float bSpawnX = enemy.getFloat("x");
					float bSpawnY = enemy.getFloat("y");
					float bFlickerRate = enemy.getFloat("FlickerRate");
					
					Blinker bl = (Blinker) entityHandler.createEntity(EntityType.BLINKER, bColor, bRadius, 1.1f, bSpawnX, bSpawnY,  0f, 0f); //create a drifter
					bl.setFlickerRate(bFlickerRate);
					break;
				case 3: //Case 3: Bullet-like entities that don't die over time
					Color bulColor = convertStringToColor(enemy.getString("Color"));
					float bulRadius = enemy.getFloat("Radius");
					float bulSpawnX = enemy.getFloat("x");
					float bulSpawnY = enemy.getFloat("y");
					float bulFlickerRate = enemy.getFloat("FlickerRate");
					float bulFacingDirec = enemy.getFloat("Direction");
					int bulVelocity = enemy.getInt("Velocity");
					
					Bullet b = bulletHandler.createBulletsAndFire(entityHandler.getPlayer(), bulRadius, bulColor, bulSpawnX, bulSpawnY, bulVelocity, bulFacingDirec);
					b.setFlickerRate(bulFlickerRate);
					b.setImmortal(true);
					break;
				case 4: //Case 4: Chasers. They chase bullets to get big.
					Color cColor = convertStringToColor(enemy.getString("Color"));
					float cRadius = enemy.getFloat("Radius");
					float cCritMult = enemy.getFloat("CritRadiusMult");
					float cSpawnX = enemy.getFloat("x");
					float cSpawnY = enemy.getFloat("y");
					float cFacingDirec = enemy.getFloat("Direction");
					
					entityHandler.createEntity(EntityType.CHASER, cColor, cRadius, cCritMult, cSpawnX, cSpawnY, cFacingDirec, 0f); //create a drifter
					break;
			}
		}
		
		//Set win conditions
		for(int i = 0; i < jsonVal.get("WinConditions").size; i++)
			addWinCondition(jsonVal.get("WinConditions").get(i).asString());
	}
	
	public Player getPlayer()
	{
		return entityHandler.getPlayer();
	}
	
	public String[] getTitleMessage()
	{
		return titleMessage;
	}
	
	public Queue<String> getTips()
	{
		return tips;
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(outerBackground, -outerBackground.getWidth()/2, -outerBackground.getHeight()/2);
		batch.draw(background,-background.getWidth()/2,-background.getHeight()/2);
		
		if(particles != null)
			particles.draw(batch, Gdx.graphics.getDeltaTime());
	}
	
	public void setZoomBounds(LightGameStage stage)
	{
		stage.setZoomBounds(minZoom, maxZoom, defZoom);
	}
	
	private void createWorldBoundary(int worldType)
	{
		switch(worldType)
		{
			case 0:
				BodyDef worldEdgeDef = new BodyDef();
				worldEdgeDef.type = BodyType.StaticBody;
				
				Body worldEdgeBody = world.createBody(worldEdgeDef);
				
				FixtureDef worldEdgeFixture = new FixtureDef();
				worldEdgeFixture.density = 1.0f;
				worldEdgeFixture.friction = 1.0f;
				worldEdgeFixture.restitution = 1.0f;
				worldEdgeFixture.filter.categoryBits = LightGameFilters.CATEGORY_ENVIRONMENT;
				worldEdgeFixture.filter.maskBits = LightGameFilters.MASK_ENVIRONMENT;
					
				EdgeShape borderEdge = new EdgeShape();
				
				borderEdge.set(-background.getWidth()/2, background.getHeight()/2, background.getWidth()/2, background.getHeight()/2);
				worldEdgeFixture.shape = borderEdge;
				worldEdgeBody.createFixture(worldEdgeFixture);
				
				borderEdge.set(-background.getWidth()/2, -background.getHeight()/2, background.getWidth()/2, -background.getHeight()/2);
				worldEdgeBody.createFixture(worldEdgeFixture);
				
				borderEdge.set(-background.getWidth()/2, background.getHeight()/2, -background.getWidth()/2, -background.getHeight()/2);
				worldEdgeBody.createFixture(worldEdgeFixture);
				
				borderEdge.set(background.getWidth()/2, background.getHeight()/2, background.getWidth()/2, -background.getHeight()/2);
				worldEdgeBody.createFixture(worldEdgeFixture);
				break;
		}
	}
	
	public float[] getZoomLevels()
	{
		float[] zooms = new float[2];
		zooms[0] = minZoom;
		zooms[1] = maxZoom;
		return zooms;
	}
	
	public BoundingBox[] getLevelBoundingBox()
	{
		BoundingBox[] boxes = new BoundingBox[4];
		
		//Left bound
		boxes[0] = new BoundingBox(new Vector3(-background.getWidth()/2 - 10, -background.getHeight()/2,0), new Vector3(-background.getWidth()/2 - 9, background.getHeight()/2, 0));
		
		//Top bound
		boxes[1] = new BoundingBox(new Vector3(-background.getWidth()/2, background.getHeight()/2 + 9, 0), new Vector3(background.getWidth()/2, background.getHeight()/2 + 10,0));
		
		//Right bound
		boxes[2] = new BoundingBox(new Vector3(background.getWidth()/2 + 9, -background.getHeight()/2, 0), new Vector3(background.getWidth()/2 + 10, background.getHeight()/2,0));
		
		//Bottom bound
		boxes[3] = new BoundingBox(new Vector3(-background.getWidth()/2, -background.getHeight()/2 - 10, 0), new Vector3(background.getWidth()/2, -background.getHeight()/2 - 9,0));
		
		return boxes;
	}
	
	public boolean isOver()
	{
		if(!isPresentation)
			return levelState.allConditionsSatisfied();
		else return false;
	}
	
	private Color convertStringToColor(String colorString)
	{
		if(colorString.equals("WHITE"))
			return Color.WHITE;
		else if(colorString.equals("RED"))
			return Color.RED;
		else if(colorString.equals("BLUE"))
			return Color.BLUE;
		else if(colorString.equals("YELLOW"))
			return Color.YELLOW;
		else if(colorString.equals("ORANGE"))
			return Color.ORANGE;
		else if(colorString.equals("GREEN"))
			return Color.GREEN;
		
		return Color.WHITE;
	}
	
	private void addWinCondition(String condition)
	{
		if(condition.equals("OnlyEntityLeft"))
			levelState.addWinCondition(new OnlyEntityLeft(entityHandler, bulletHandler));	
		else if(condition.equals("NoMoreBullets"))
			levelState.addWinCondition(new NoMoreBullets(entityHandler, bulletHandler));
		else if(condition.equals("PlayerIsLargest"))
			levelState.addWinCondition(new PlayerIsLargest(entityHandler, bulletHandler));
		else if(condition.equals("NoOtherLumis"))
			levelState.addWinCondition(new NoOtherLumis(entityHandler, bulletHandler));
	}
}
