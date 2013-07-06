package com.eli.lightgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class AudioHandler //is singleton
{
	private static AudioHandler instance = null;
	
	private static Music backgroundMusic;
	
	protected AudioHandler() {}
	
	public static AudioHandler getInstance() 
	{
	      if(instance == null) 
	         instance = new AudioHandler();
	      
	      return instance;
	}
	
	public void loadBackgroundMusic(String path)
	{
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
	}
	
	public void startBackgroundMusic()
	{
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
	}
}
