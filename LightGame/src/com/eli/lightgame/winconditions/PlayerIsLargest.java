package com.eli.lightgame.winconditions;

import java.util.HashMap;
import java.util.Iterator;

import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.entities.Entity;

public class PlayerIsLargest extends WinCondition
{

	public PlayerIsLargest(EntityHandler eh, BulletHandler bh) {
		super(eh, bh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSatisfied() 
	{
		HashMap<Integer, Entity> entities = entityHandler.getEntities();
		float largestRadius = 0;
		Entity largestEntity = null;
		
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	
	    	if(!entities.get(entityID).ignoreSize())
	    	{
	    		float currentRadius = entities.get(entityID).getRadius();
		    	if(currentRadius > largestRadius)
		    	{
		    		largestEntity = entities.get(entityID);
		    		largestRadius = currentRadius;
		    	}
	    	}
	    }
	    return largestEntity == entityHandler.getPlayer();	
	}

}
