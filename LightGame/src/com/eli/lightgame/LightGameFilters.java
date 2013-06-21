package com.eli.lightgame;

public class LightGameFilters
{
	public static final short CATEGORY_ENTITY = 0x0001; //box2d collision filtering
	public static final short CATEGORY_NEUTRAL_ENTITY = 0x0002;
	public static final short CATEGORY_ENVIRONMENT = 0x0004;
	public static final short CATEGORY_GHOST = 0x0008;
	public static final short MASK_ENTITY = -1;
	public static final short MASK_NEUTRAL_ENTITY = ~CATEGORY_NEUTRAL_ENTITY;
	public static final short MASK_ENVIRONMENT = -1;
	public static final short MASK_GHOST = 0;
	
}
