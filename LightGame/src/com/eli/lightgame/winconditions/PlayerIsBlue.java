package com.eli.lightgame.winconditions;

import com.badlogic.gdx.graphics.Color;
import com.eli.lightgame.BulletHandler;
import com.eli.lightgame.EntityHandler;

public class PlayerIsBlue extends WinCondition
{
	public PlayerIsBlue(EntityHandler eh, BulletHandler bh) {
		super(eh, bh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSatisfied() 
	{
		return entityHandler.getPlayer().getColor().equals(Color.BLUE);
	}
}

