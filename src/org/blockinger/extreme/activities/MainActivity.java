package org.blockinger.extreme.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.blockinger.extreme.R;
import org.blockinger.extreme.components.GameState;
import org.blockinger.extreme.components.Sound;

public class MainActivity extends ListActivity {

	public static final int SCORE_REQUEST = 0x0;
	
	/** This key is used to access the player name, which is returned as an Intent from the gameactivity upon completion (gameover).
	 *  The Package Prefix is mandatory for Intent data
	 */
	public static final String PLAYERNAME_KEY = "org.blockinger.game.activities.playername";
	
	/** This key is used to access the player name, which is returned as an Intent from the gameactivity upon completion (gameover).
	 *  The Package Prefix is mandatory for Intent data
	 */
	public static final String SCORE_KEY = "org.blockinger.game.activities.score";

	private AlertDialog.Builder startLevelDialog;
	private AlertDialog.Builder donateDialog;
	private int startLevel;
    private TextView leveldialogtext;
	private Sound sound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PreferenceManager.setDefaultValues(this, R.xml.simple_preferences, true);
		PreferenceManager.setDefaultValues(this, R.xml.advanced_preferences, true);

		/* Create Music */
		sound = new Sound(this);
		sound.startMusic(Sound.MENU_MUSIC, 0);
	    
	    /* Create Startlevel Dialog */
	    startLevel = 0;
	    startLevelDialog = new AlertDialog.Builder(this);
		startLevelDialog.setTitle(R.string.startLevelDialogTitle);
		startLevelDialog.setCancelable(false);
		startLevelDialog.setNegativeButton(R.string.startLevelDialogCancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		startLevelDialog.setPositiveButton(R.string.startLevelDialogStart, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.start();
			}
		});
	    
		/* Create Donate Dialog */
	    donateDialog = new AlertDialog.Builder(this);
	    donateDialog.setTitle(R.string.pref_donate_title);
	    donateDialog.setMessage(R.string.pref_donate_summary);
	    donateDialog.setNegativeButton(R.string.startLevelDialogCancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	    donateDialog.setPositiveButton(R.string.donate_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = getResources().getString(R.string.donation_url);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_about:
				Intent intent1 = new Intent(this, AboutActivity.class);
				startActivity(intent1);
				return true;
			case R.id.action_donate:
				donateDialog.show();
				return true;
			case R.id.action_help:
				Intent intent2 = new Intent(this, HelpActivity.class);
				startActivity(intent2);
				return true;
			case R.id.action_exit:
			    GameState.destroy();
			    MainActivity.this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void start() {
		Intent intent = new Intent(this, GameActivity.class);
		Bundle b = new Bundle();
		b.putInt("mode", GameActivity.NEW_GAME);
		b.putInt("level", startLevel);
		intent.putExtras(b);
		startActivityForResult(intent,SCORE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != SCORE_REQUEST)
			return;
		if(resultCode != RESULT_OK)
			return;
	}


    public void onClickStart(View view) {
        View dialogView = getLayoutInflater().inflate(R.layout.seek_bar_dialog, null);
		leveldialogtext = ((TextView) dialogView.findViewById(R.id.leveldialogleveldisplay));
        SeekBar leveldialogBar = ((SeekBar) dialogView.findViewById(R.id.levelseekbar));
		leveldialogBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                leveldialogtext.setText("" + arg1);
                startLevel = arg1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

        });
		leveldialogBar.setProgress(startLevel);
		leveldialogtext.setText("" + startLevel);
		startLevelDialog.setView(dialogView);
		startLevelDialog.show();
    }

    public void onClickResume(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		Bundle b = new Bundle();
		b.putInt("mode", GameActivity.RESUME_GAME);
		intent.putExtras(b);
		startActivityForResult(intent,SCORE_REQUEST);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	sound.pause();
    	sound.setInactive(true);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	sound.pause();
    	sound.setInactive(true);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	sound.release();
    	sound = null;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	sound.setInactive(false);
    	sound.resume();
	    
	    if(!GameState.isFinished()) {
	    	findViewById(R.id.resumeButton).setEnabled(true);
	    	((Button)findViewById(R.id.resumeButton)).setTextColor(getResources().getColor(R.color.square_error));
	    } else {
	    	findViewById(R.id.resumeButton).setEnabled(false);
	    	((Button)findViewById(R.id.resumeButton)).setTextColor(getResources().getColor(R.color.holo_grey));
	    }
    }

}
