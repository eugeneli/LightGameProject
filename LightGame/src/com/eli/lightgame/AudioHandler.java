package com.eli.lightgame;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioHandler //is singleton
{
	private static AudioHandler instance = null;
	
	private Music backgroundMusic;
	private Sound ding = Gdx.audio.newSound(Gdx.files.internal("data/audio/sounds/ding.ogg"));
	private Sound ding2 = Gdx.audio.newSound(Gdx.files.internal("data/audio/sounds/ding2.ogg"));
	private Sound explode = Gdx.audio.newSound(Gdx.files.internal("data/audio/sounds/player_explode.ogg"));
	
	private HashMap<String, Sound> tmpSounds = new HashMap<String, Sound>();
	
	protected AudioHandler() {}
	
	public static AudioHandler getInstance() 
	{
	    if(instance == null) 
	    	instance = new AudioHandler();
	      
	    return instance;
	}
	
	public void loadBackgroundMusic(String path)
	{
		stopBackgroundMusic();
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
	}
	
	public void startBackgroundMusic()
	{
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.4f);
		backgroundMusic.play();
	}
	
	public void stopBackgroundMusic()
	{
		if(backgroundMusic != null && backgroundMusic.isPlaying())
		{
			backgroundMusic.stop();
			backgroundMusic.dispose();
		}
	}
	
	public void playDing(float radius, float criticalRadius)
	{
		//pitch range is 0.5 - 2
		//Normalize radius's range (2 - criticalRadius) to pitch range
		
		float pitch = ((radius - 2)*(2 - 0.5f))/(criticalRadius - 2) + 0.5f; 

		ding.play(0.25f, 2-pitch, 0);
	}
	
	public void playDing2()
	{
		ding.play(0.25f, (float)(Math.random() * (2 - 0.5) + 1), (float)(Math.random() * 2 - 1));
	}
	
	public void playExplosion()
	{
		explode.play(0.3f, (float)(Math.random() * (2 - 0.5) + 1), 0);
	}
	
	public void loadTmpSound(String key, String soundPath)
	{
		tmpSounds.put(key, Gdx.audio.newSound(Gdx.files.internal(soundPath)));
	}
	
	public void playTmpSound(String key)
	{
		tmpSounds.get(key).play(0.2f, 1, 0);
	}
	
	public void playTmpSound(String key, float volume, float pitch, float pan)
	{
		tmpSounds.get(key).play(volume, pitch, pan);
	}
	
	public void loopTmpSound(String key)
	{
		tmpSounds.get(key).loop(0.3f, 1, 0);
	}
	
	public void loopTmpSound(String key, float volume, float pitch, float pan)
	{
		tmpSounds.get(key).loop(volume, pitch, pan);
	}
	
	public void stopTmpSound(String key)
	{
		tmpSounds.get(key).stop();
	}
	
	public void removeTmpSound(String key)
	{
		tmpSounds.get(key).stop();
		tmpSounds.get(key).dispose();
		tmpSounds.remove(key);
	}
	
	public void clearTmpSounds()
	{
		Iterator<String> it = tmpSounds.keySet().iterator();
	    while (it.hasNext())
	    {
	    	String key = (String)it.next();
	    	tmpSounds.get(key).dispose();
	    }
		tmpSounds.clear();
	}
}
