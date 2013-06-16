package com.eli.lightgame;

public class LightGameFilters
{
	public static final short CATEGORY_PLAYER = 0x0001; //box2d collision filtering
	public static final short CATEGORY_ENEMY = 0x0002;
	public static final short CATEGORY_ENVIRONMENT = 0x0004;
	public static final short MASK_PLAYER = CATEGORY_ENEMY | CATEGORY_ENVIRONMENT;
	public static final short MASK_ENEMY = CATEGORY_PLAYER | CATEGORY_ENVIRONMENT;
	public static final short MASK_ENVIRONMENT = -1;
}
