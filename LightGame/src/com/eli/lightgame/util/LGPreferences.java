package com.eli.lightgame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LGPreferences
{
    // constants
    private static final String PREF_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SCREENCONTROLS_ENABLED = "screencontrols.enabled";
    private static final String PREFS_NAME = "Lumiverse";
    private Preferences pref;
 
    public LGPreferences()
    {
    	pref = Gdx.app.getPreferences(PREFS_NAME);
    }
 
    public boolean isSoundEffectsEnabled()
    {
        return pref.getBoolean( PREF_SOUND_ENABLED, true );
    }
 
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled )
    {
    	pref.putBoolean( PREF_SOUND_ENABLED, soundEffectsEnabled );
    	pref.flush();
    }
 
    public boolean isMusicEnabled()
    {
        return pref.getBoolean( PREF_MUSIC_ENABLED, true );
    }
 
    public void setMusicEnabled(boolean musicEnabled)
    {
    	pref.putBoolean( PREF_MUSIC_ENABLED, musicEnabled );
    	pref.flush();
    }
    
    public boolean useOnScreenControls()
    {
    	return pref.getBoolean(PREF_SCREENCONTROLS_ENABLED,true);
    }
    
    public void setOnScreenControls(boolean oscEnabled)
    {
    	pref.putBoolean(PREF_SCREENCONTROLS_ENABLED, oscEnabled);
    	pref.flush();
    }
 
    public float getVolume()
    {
        return pref.getFloat( PREF_VOLUME, 0.5f );
    }
 
    public void setVolume(
        float volume )
    {
    	pref.putFloat( PREF_VOLUME, volume );
    	pref.flush();
    }
}
