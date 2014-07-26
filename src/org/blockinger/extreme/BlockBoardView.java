package org.blockinger.extreme;

import org.blockinger.extreme.activities.GameActivity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;


public class BlockBoardView extends SurfaceView implements Callback {

	private GameActivity host;
	
	public BlockBoardView(Context context) {
		super(context);
	}

	public BlockBoardView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}

	public BlockBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	public void setHost(GameActivity ga) {
		host = ga;
	}
	
	public void init() {
		setZOrderOnTop(true);
		getHolder().addCallback(this);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		host.startGame(this);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		host.destroyWorkThread();
	}
}

