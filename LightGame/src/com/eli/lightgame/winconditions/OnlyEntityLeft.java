package com.eli.lightgame.winconditions;

import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;

public class OnlyEntityLeft extends WinCondition
{
	public OnlyEntityLeft(EntityHandler eh, BulletHandler bh) {
		super(eh, bh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSatisfied()
	{
		return (entityHandler.getEntityNumber() == 1 && bulletHandler.getBulletNumber() == 0);
	}

}
