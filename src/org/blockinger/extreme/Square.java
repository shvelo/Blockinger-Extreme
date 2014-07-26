package org.blockinger.extreme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Square {

	public static final int type_empty = 0;
	public static final int type_blue = 1;
	public static final int type_orange = 2;
	public static final int type_yellow = 3;
	public static final int type_red = 4;
	public static final int type_green = 5;
	public static final int type_magenta = 6;
	public static final int type_cyan = 7;
	
	private int type;
	private Paint paint;
	private Bitmap bm;
	private Bitmap phantomBM;
	private Canvas canv;
	private Canvas phantomCanv;
	//private Context context;
	private int squaresize;
	private int phantomAlpha;
	
	public Square(int type, Context c) {
		this.type = type;
		paint = new Paint();
		phantomAlpha = c.getResources().getInteger(R.integer.phantom_alpha);
		squaresize = 0;
		switch(type){
			case type_blue:
				paint.setColor(c.getResources().getColor(R.color.square_blue));
				break;
			case type_orange:
				paint.setColor(c.getResources().getColor(R.color.square_orange));
				break;
			case type_yellow:
				paint.setColor(c.getResources().getColor(R.color.square_yellow));
				break;
			case type_red:
				paint.setColor(c.getResources().getColor(R.color.square_red));
				break;
			case type_green:
				paint.setColor(c.getResources().getColor(R.color.square_green));
				break;
			case type_magenta:
				paint.setColor(c.getResources().getColor(R.color.square_magenta));
				break;
			case type_cyan:
				paint.setColor(c.getResources().getColor(R.color.square_cyan));
				break;
			case type_empty:
				return;
			default: // error: white
				paint.setColor(c.getResources().getColor(R.color.square_error));
				break;
		}
	}
	
	public void reDraw(int ss) {
		if(type == type_empty)
			return;
		
		squaresize = ss;
		bm = Bitmap.createBitmap(ss, ss, Bitmap.Config.ARGB_8888);
		phantomBM = Bitmap.createBitmap(ss, ss, Bitmap.Config.ARGB_8888);
		canv = new Canvas(bm);
		phantomCanv = new Canvas(phantomBM);

		paint.setAlpha(255);
		canv.drawRect(0, 0, squaresize, squaresize, paint);
		paint.setAlpha(phantomAlpha);
		phantomCanv.drawRect(0, 0, squaresize, squaresize, paint);
		//canv.draw
	}
	
	public Square clone(Context c) {
		return new Square(type, c);
	}
	
	public boolean isEmpty() {
		if(type == type_empty)
			return true;
		else 
			return false;
	}

	public void draw(int x, int y, int squareSize, Canvas c, boolean isPhantom) { // top left corner of square
		if(type == type_empty)
			return;
		
		if(squareSize != squaresize)
			reDraw(squareSize);
		
		if(isPhantom) {
			c.drawBitmap(phantomBM, x, y, null);
		} else {
			c.drawBitmap(bm, x, y, null);
		}
	}
}
