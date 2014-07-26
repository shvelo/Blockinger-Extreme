package org.blockinger.extreme;

import org.blockinger.extreme.components.Board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Animator {

	public static final int animationStageIdle = 0;
	public static final int animationStageFlash = 1;
	public static final int animationStageBurst = 2;
	
	// Config
	private long flashInterval;
	private long flashFinishTime;
	private int squareSize;
	
	// State
	private long startTime;
	private int stage;
	private boolean drawEnable;
	private long nextFlash;
	
	// Data
	private Row row;
	private Bitmap bitmapRow;
	private int flashCount;
	private int rawFlashInterval;
	
	// Constructor
	public Animator(Context c, Row r) {
		rawFlashInterval = c.getResources().getInteger(R.integer.clearAnimation_flashInterval);
		flashCount = c.getResources().getInteger(R.integer.clearAnimation_flashCount);
		stage = animationStageIdle;
		this.row = r;
		drawEnable = true;
		startTime = 0;
		flashFinishTime = 0;
		nextFlash = 0;
		flashInterval = 0;
		squareSize = 0;
	}
	
	public void cycle(long time, Board board) {
		if(stage == animationStageIdle)
			return;
		
		if(time >= flashFinishTime)
			finish(board);
		else if (time >= nextFlash) {
			nextFlash += flashInterval;
			drawEnable = !drawEnable;
			board.invalidate();
		}
	}

	public void start(Board board, int currentDropInterval) {
		bitmapRow = row.drawBitmap(squareSize);
		stage = animationStageFlash;
		startTime = System.currentTimeMillis();
		flashInterval = Math.min( // Choose base flash interval on slow levels and shorter interval on fast levels.
				rawFlashInterval,
				(int)((float)currentDropInterval / (float)flashCount)
		);
		flashFinishTime = startTime + 2*flashInterval*flashCount;
		nextFlash = startTime + flashInterval;
		drawEnable = false;
		board.invalidate();
	}
	
	public boolean finish(Board board) {
		if(animationStageIdle == stage)
			return false;
		stage = animationStageIdle;
		row.finishClear(board);
		drawEnable = true;
		return true;
	}
	
	public void draw(int x, int y, int ss, Canvas c) {
		//float scaleFactor = flashFinishTime / (flashFinishTime-flashProgress);
		//Bitmap bm = Bitmap.createBitmap(brustWidth, burstHeight, Bitmap.Config.ARGB_8888);
		//Canvas tamp = new Canvas(bm);
		//transparentPaint.setAlpha(do{(shit.here())}while(you += shit));
		this.squareSize = ss;
		if(drawEnable) {
			if(stage == animationStageIdle)
				bitmapRow = row.drawBitmap(ss);
			//bitmapRow.scale(scaleFactor, scaleFactor, px, py);
			if (bitmapRow != null)
				c.drawBitmap(bitmapRow, x, y, null);
		}
	}
	
	public void startFlash() {
		
	}
	
	public void cancelBurst() {
		
	}
	
	public void startBurst() {
		
	}
}