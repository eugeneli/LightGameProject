package com.eli.lightgame.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.entities.Entity;
import com.eli.lightgame.entities.Player;

public class LGInput extends ActorGestureListener
{
	private final OrthographicCamera camera;
	private final Stage stage;
	private final EntityHandler entityHandler;
	private final Player player;
	
	private float minZoom = 2.5f;
    private float maxZoom = 8.0f;
	
	public LGInput(OrthographicCamera cam, Stage aStage, EntityHandler entHandler)
	{
		camera = cam;
		stage = aStage;
		entityHandler = entHandler;
		player = entityHandler.getPlayer();
	}
	
	public void touchDown(InputEvent event, float x, float y, int pointer, int button) 
	{
		
	}
	
	public void touchUp(InputEvent event, float x, float y, int pointer, int button)
	{
		Vector2 stageToScreenCoords = stage.stageToScreenCoordinates(new Vector2(x,y));
		Vector3 worldCoordinates = new Vector3(stageToScreenCoords.x, stageToScreenCoords.y, 0);
		camera.unproject(worldCoordinates);
		
		float toTargetX = (float)((worldCoordinates.x - player.getPosition().x));
		float toTargetY = (float)((worldCoordinates.y - player.getPosition().y));
		float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
		
		if(entityHandler.targetingEntity(worldCoordinates.x,worldCoordinates.y) == null)
		{
			player.move(rotAngle);
		}
	}

	public void tap(InputEvent event, float x, float y, int count, int button) 
	{
		Vector2 stageToScreenCoords = stage.stageToScreenCoordinates(new Vector2(x,y));
		Vector3 worldCoordinates = new Vector3(stageToScreenCoords.x, stageToScreenCoords.y, 0);
		camera.unproject(worldCoordinates);
		
		float toTargetX = (float)((worldCoordinates.x - player.getPosition().x));
		float toTargetY = (float)((worldCoordinates.y - player.getPosition().y));
		float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
		
		Entity targettedEntity = entityHandler.targetingEntity(worldCoordinates.x,worldCoordinates.y);
		if(targettedEntity != null)
		{
			player.turnAndShootTracking(rotAngle,targettedEntity);
			//player.turnAndShoot(rotAngle);
		}
	}

	public boolean longPress(Actor actor, float x, float y)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void fling(InputEvent event, float velocityX, float velocityY, int button)
	{
		// TODO Auto-generated method stub
	}

	public void pan(InputEvent event, float x, float y, float deltaX, float deltaY)
	{
		Vector2 stageToScreenCoords = stage.stageToScreenCoordinates(new Vector2(x,y));
		Vector3 worldCoordinates = new Vector3(stageToScreenCoords.x, stageToScreenCoords.y, 0);
		camera.unproject(worldCoordinates);
		
		float toTargetX = (float)((worldCoordinates.x - player.getPosition().x));
		float toTargetY = (float)((worldCoordinates.y - player.getPosition().y));
		float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
		
		if(entityHandler.targetingEntity(worldCoordinates.x,worldCoordinates.y) == null)
		{
			player.move(rotAngle);
		}
	}

	public void zoom(InputEvent event, float initialDistance, float distance)
	{
		float scalar = ((initialDistance-distance)/initialDistance)/2;

        camera.zoom += scalar;
        if(camera.zoom <= minZoom)
        {
        	camera.position.set(player.getPosition().x,player.getPosition().y,0);
        	camera.zoom = minZoom;
        }
        if(camera.zoom >= maxZoom)
        {
        	camera.position.set(0,0,0);
        	camera.zoom = maxZoom;
        }
        camera.update();
	}

	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setZoomBounds(float min, float max)
    {
    	minZoom = min;
    	maxZoom = max;
    }
}
