package com.eli.lightgame;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.eli.lightgame.EntityHandler.EntityType;
import com.eli.lightgame.entities.Blinker;
import com.eli.lightgame.entities.Bullet;
import com.eli.lightgame.entities.Entity;
import com.eli.lightgame.entities.Player;

public class Level
{
	private Texture background;
	
	private World world;
	private float width;
	private float height;
	
	private LevelStateManager levelState;
	private EntityHandler entityHandler;
	private BulletHandler bulletHandler;
	
	public Level(LevelStateManager levstate, EntityHandler eh, BulletHandler bh, World theWorld, float w, float h)
	{
		entityHandler = eh;
		bulletHandler = bh;
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
		
		if(jsonVal.getString("WorldShape").equals("RECTANGLE"))
			createWorldBoundary(0);
		
		//Get player data and create player
		if(!levelType.equals("NONE"))
		{
			Color PlayerColor = convertStringToColor(jsonVal.get("PlayerColor").asString());
			float playerRadius = jsonVal.get("PlayerRadius").asFloat();
			float playerSpawnX = jsonVal.get("PlayerSpawn").getFloat(0);
			float playerSpawnY = jsonVal.get("PlayerSpawn").getFloat(1);
			entityHandler.createEntity(EntityType.PLAYER, PlayerColor, playerRadius, playerSpawnX, playerSpawnY, 0f, 0f); //create player
		}
		
		
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
					
					entityHandler.createEntity(EntityType.RED_GIANT, rgColor, rgRadius, rgSpawnX, rgSpawnY, 0f, 0f); //create a red giant
					break;
				case 1: //Case 1: Drifter
					Color dColor = convertStringToColor(enemy.getString("Color"));
					float dRadius = enemy.getFloat("Radius");
					float dSpawnX = enemy.getFloat("x");
					float dSpawnY = enemy.getFloat("y");
					float dFacingDirec = enemy.getFloat("Direction");
					float dVelocity = enemy.getFloat("Velocity");
					
					entityHandler.createEntity(EntityType.DRIFTER, dColor, dRadius, dSpawnX, dSpawnY, dFacingDirec, dVelocity); //create a drifter
					break;
				case 2: //Case 2: Blinker
					Color bColor = convertStringToColor(enemy.getString("Color"));
					float bRadius = enemy.getFloat("Radius");
					float bSpawnX = enemy.getFloat("x");
					float bSpawnY = enemy.getFloat("y");
					float bFlickerRate = enemy.getFloat("FlickerRate");
					
					Blinker bl = (Blinker) entityHandler.createEntity(EntityType.BLINKER, bColor, bRadius, bSpawnX, bSpawnY,  0f, 0f); //create a drifter
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
			}
		}
	}
	
	public Player getPlayer()
	{
		return entityHandler.getPlayer();
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(background,-background.getWidth()/2,-background.getHeight()/2);
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
}
