package com.eli.lightgame.winconditions;

import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;

public class OnlyPlayerLeft extends WinCondition
{

	public OnlyPlayerLeft(EntityHandler eh, BulletHandler bh) {
		super(eh, bh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSatisfied() 
	{
		return bulletHandler.getBulletNumber() == 0;
	}
}

