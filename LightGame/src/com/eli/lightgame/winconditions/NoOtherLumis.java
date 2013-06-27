package com.eli.lightgame.winconditions;

import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;

public class NoOtherLumis extends WinCondition
{

	public NoOtherLumis(EntityHandler eh, BulletHandler bh) {
		super(eh, bh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSatisfied() 
	{
		return (entityHandler.getEntities().size() == 1 && entityHandler.getPlayer().getRadius() > 0);
	}
}