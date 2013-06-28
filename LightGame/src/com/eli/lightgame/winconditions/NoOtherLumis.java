package com.eli.lightgame.winconditions;

import java.util.HashMap;
import java.util.Iterator;

import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;
import com.eli.lightgame.entities.Entity;

public class NoOtherLumis extends WinCondition
{

	public NoOtherLumis(EntityHandler eh, BulletHandler bh) {
		super(eh, bh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSatisfied() 
	{
		HashMap<Integer, Entity> entities = entityHandler.getEntities();
		int entityCount = 0;
		
		Iterator<Integer> it = entities.keySet().iterator();
	    while (it.hasNext())
	    {
	    	Integer entityID = (Integer)it.next();
	    	
	    	if(!entities.get(entityID).ignoreExistence())
	    	{
	    		entityCount++;
	    	}
	    }
		return (entityCount == 1 && entityHandler.getPlayer().getRadius() > 0);
	}
}