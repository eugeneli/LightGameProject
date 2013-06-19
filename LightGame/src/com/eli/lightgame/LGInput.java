package com.eli.lightgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.eli.lightgame.entities.Player;

public class LGInput implements GestureListener 
{
	private final OrthographicCamera camera;
	private final EntityHandler entityHandler;
	private final Player player;
	
	public LGInput(OrthographicCamera cam, EntityHandler entHandler)
	{
		camera = cam;
		entityHandler = entHandler;
		player = entityHandler.getPlayer();
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button)
	{
		Vector3 worldCoordinates = new Vector3(x, y, 0);
		camera.unproject(worldCoordinates);
		
		float toTargetX = (float)((worldCoordinates.x - player.getPosition().x));
		float toTargetY = (float)((worldCoordinates.y - player.getPosition().y));
		float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
		
		if(!entityHandler.isTargetingEntity(worldCoordinates.x,worldCoordinates.y))
		{
			player.move(rotAngle);
		}
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button)
	{
		Vector3 worldCoordinates = new Vector3(x, y, 0);
		camera.unproject(worldCoordinates);
		
		float toTargetX = (float)((worldCoordinates.x - player.getPosition().x));
		float toTargetY = (float)((worldCoordinates.y - player.getPosition().y));
		float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
		
		if(entityHandler.isTargetingEntity(worldCoordinates.x,worldCoordinates.y))
		{
			player.turnAndShoot(rotAngle);
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		Vector3 worldCoordinates = new Vector3(x, y, 0);
		camera.unproject(worldCoordinates);
		
		float toTargetX = (float)((worldCoordinates.x - player.getPosition().x));
		float toTargetY = (float)((worldCoordinates.y - player.getPosition().y));
		float rotAngle = (float)Math.atan2(toTargetY,toTargetX);
		
		if(!entityHandler.isTargetingEntity(worldCoordinates.x,worldCoordinates.y))
		{
			player.move(rotAngle);
		}
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		float scalar = ((initialDistance-distance)/initialDistance)/2;

        camera.zoom += scalar;
        if(camera.zoom <= 2.5f)
                camera.zoom = 2.5f;
        if(camera.zoom >= 8.0f)
                camera.zoom = 8.0f;
        camera.update();
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
