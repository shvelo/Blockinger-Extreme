package org.blockinger.extreme.components;

import org.blockinger.extreme.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;

public class Sound implements OnAudioFocusChangeListener {

	private Activity host;
	private AudioManager audioCEO;
	private int soundID_tetrisSoundPlayer;
	private int soundID_dropSoundPlayer;
	private int soundID_clearSoundPlayer;
	private int soundID_gameOverPlayer;
	private int soundID_buttonSoundPlayer;
	private MediaPlayer musicPlayer;
	private boolean noFocus;
	private boolean isMusicReady;
	private BroadcastReceiver noisyAudioStreamReceiver;
	private BroadcastReceiver ringerModeReceiver;
	private BroadcastReceiver headsetPlugReceiver;
	private SoundPool soundPool;
	private int songtime;
	private int musicType;
	private boolean isInactive;

	public static final int NO_MUSIC = 0x0;
	public static final int MENU_MUSIC = 0x1;
	public static final int GAME_MUSIC = 0x2;
	
	public Sound(Activity c) {
		host = c;
		
		audioCEO = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		c.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// Request AudioFocus if The Music Volume is greater than zero
		requestFocus();

		IntentFilter intentFilter;
		/*Noise Receiver (when unplugging headphones) */
		intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		noisyAudioStreamReceiver = new BroadcastReceiver() {
				public void onReceive(Context context, android.content.Intent intent) {
					Sound.this.pauseMusic();
				}
			};
		c.registerReceiver(noisyAudioStreamReceiver, intentFilter);

		/* Headphone Receiver (when headphone state changes) */
		intentFilter = new IntentFilter(android.content.Intent.ACTION_HEADSET_PLUG );
		headsetPlugReceiver = new BroadcastReceiver() {
			
				public void onReceive(Context context, android.content.Intent intent) {
					if (intent.getAction().equals(android.content.Intent.ACTION_HEADSET_PLUG)) {
			            int state = intent.getIntExtra("state", -1);
			            switch (state) {
			            case 0:
			                // Headset is unplugged
			            	// this event is broadcasted later than ACTION_AUDIO_BECOMING_NOISY
			            	// and is hence the inferior choice
			                break;
			            case 1:
			                // Headset is plugged
			            	Sound.this.startMusic(musicType,songtime);
			                break;
			            default:
			                // I have no idea what the headset state is
			            }
			        }
				}
				
			};
		c.registerReceiver(headsetPlugReceiver, intentFilter);
		
		/* Ringer Mode Receiver (when the user changes audio mode to silent or back to normal) */
		intentFilter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
		ringerModeReceiver = new BroadcastReceiver() {
			
			public void onReceive(Context context, android.content.Intent intent) {
				songtime = getSongtime();
            	Sound.this.pauseMusic();
				Sound.this.startMusic(musicType,songtime);
			}
			
		};
		c.registerReceiver(ringerModeReceiver,intentFilter);
		
		soundPool = new SoundPool(c.getResources().getInteger(R.integer.audio_streams),AudioManager.STREAM_MUSIC,0);

		soundID_tetrisSoundPlayer = -1;
		soundID_dropSoundPlayer = -1;
		soundID_clearSoundPlayer = -1;
		soundID_gameOverPlayer = -1;
		soundID_buttonSoundPlayer = -1;
		
		songtime = 0;
		musicType = 0;
		isMusicReady = false;
		isInactive = false;
	}
	
	private void requestFocus() {
		SharedPreferences prefs;
		try{
			prefs = PreferenceManager.getDefaultSharedPreferences(host);
				
		} catch(Exception e) {
			noFocus = true;
			return;
		}
		if(prefs == null) {
			noFocus = true;
			return;
		}
		if(prefs.getInt("pref_musicvolume", 60) > 0) {
			int result = audioCEO.requestAudioFocus(this,
		                // Use the music stream.
		                AudioManager.STREAM_MUSIC,
		                // Request permanent focus.
		                AudioManager.AUDIOFOCUS_GAIN);
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				noFocus = false;
			} else
				noFocus = true;
		}
	}

	public void setInactive(boolean b) {
		isInactive = b;
	}
	
	public void loadEffects() {
		soundID_tetrisSoundPlayer = soundPool.load(host, R.raw.tetris_free, 1);
		soundID_dropSoundPlayer = soundPool.load(host, R.raw.drop_free, 1);
		soundID_buttonSoundPlayer = soundPool.load(host, R.raw.key_free, 1);
		soundID_clearSoundPlayer = soundPool.load(host, R.raw.clear2_free, 1);
		soundID_gameOverPlayer = soundPool.load(host, R.raw.gameover2_free, 1);
	}
	
	public void loadMusic(int type, int startTime) {
		
		/* Reset previous Music */
		isMusicReady = false;
		if(musicPlayer != null)
			musicPlayer.release();
		musicPlayer = null;
		
		/* Check if Music is allowed to start */
		requestFocus();
		if(noFocus)
			return;
		if(isInactive)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		
		/* Start Music */
		songtime = startTime;
		musicType = type;
		switch(type) {
			case MENU_MUSIC :
				musicPlayer = MediaPlayer.create(host, R.raw.lemmings03);
				break;
			case GAME_MUSIC :
				musicPlayer = MediaPlayer.create(host, R.raw.sadrobot01);
				break;
			default :
				musicPlayer = new MediaPlayer();
				break;
		}
		musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		musicPlayer.setLooping(true);
		musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
		musicPlayer.seekTo(songtime);
		isMusicReady = true;
	}
	
	public void startMusic(int type, int startTime) {
		/* Check if Music is allowed to start */
		requestFocus();
		if(noFocus)
			return;
		if(isInactive)
			return;
		
		if(isMusicReady) {
			/* NOP */
		} else {
			loadMusic(type,startTime);
		}
		if(isMusicReady) {
			if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
				return;
			
			musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
			musicPlayer.start();
		}
	}
	
	public void clearSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_clearSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}
	
	public void buttonSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		if(!PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_button_sound", true))
			return;
		soundPool.play(
			soundID_buttonSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}
	
	public void dropSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_dropSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}

	public void tetrisSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_tetrisSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}

	public void gameOverSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		pause(); // pause music to make the end of the game feel more dramatic. hhheheh.
		soundPool.play(
			soundID_gameOverPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}

	public void resume() {
		if(isInactive)
			return;
		
		soundPool.autoResume();
		startMusic(musicType,songtime);
	}
	
	public void pauseMusic() {
		isMusicReady = false;
		if(musicPlayer != null) {
			try{
				musicPlayer.pause();
				isMusicReady = true;
			} catch(IllegalStateException e) {
				isMusicReady = false;
			}
		}
	}

	public void pause() {
		soundPool.autoPause();
		pauseMusic();
	}
	
	public void release() {
		soundPool.autoPause();
		soundPool.release();
		soundPool = null;
		isMusicReady = false;
		if(musicPlayer != null)
			musicPlayer.release();
		musicPlayer = null;

		host.unregisterReceiver(noisyAudioStreamReceiver);
		host.unregisterReceiver(ringerModeReceiver);
		host.unregisterReceiver(headsetPlugReceiver);
		audioCEO.abandonAudioFocus(this);
		audioCEO = null;
		host = null;
		noFocus = true;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
        	noFocus = true;
    		if(musicPlayer != null) {
    			try{
    				musicPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    	        } catch(IllegalStateException e) {
    				
    			}
    		}
        	soundPool.setVolume(soundID_tetrisSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_dropSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_clearSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_gameOverPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_buttonSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
        	noFocus = true;
            pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
        	noFocus = false;
    		if(musicPlayer != null) {
    			try{
    				musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    	        } catch(IllegalStateException e) {
    				
    			}
    		}
    		soundPool.setVolume(soundID_tetrisSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_dropSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_clearSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_gameOverPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_buttonSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	resume();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
        	noFocus = true;
        	pause();
        }
	}

	public int getSongtime() {
		if(musicPlayer != null) {
			try{
				return musicPlayer.getCurrentPosition();
			} catch(IllegalStateException e) {
				
			}
		}
		return 0;
	}
}
