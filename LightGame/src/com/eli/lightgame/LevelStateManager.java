package com.eli.lightgame;

import java.util.ArrayList;

import com.eli.lightgame.winconditions.WinCondition;

public class LevelStateManager
{
	ArrayList<WinCondition> winConditions = new ArrayList<WinCondition>();
	
	public void addWinCondition(WinCondition condition)
	{
		winConditions.add(condition);
	}
	
	public boolean allConditionsSatisfied()
	{
		boolean yup = true;
		for(WinCondition condition : winConditions)
		{
			if(!condition.isSatisfied())
				yup = false;
		}
		return yup;
	}
}
