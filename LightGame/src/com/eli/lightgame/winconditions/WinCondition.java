package com.eli.lightgame.winconditions;

import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;

public abstract class WinCondition
{
	protected final EntityHandler entityHandler;
	protected final BulletHandler bulletHandler;
	
	public WinCondition(EntityHandler eh, BulletHandler bh)
	{
		entityHandler = eh;
		bulletHandler = bh;
	}
	
	//Write body for this in subclasses for each different win condition
	public abstract boolean isSatisfied();
}
