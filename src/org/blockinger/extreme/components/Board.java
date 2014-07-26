package org.blockinger.extreme.components;

import org.blockinger.extreme.R;
import org.blockinger.extreme.Row;
import org.blockinger.extreme.Square;
import org.blockinger.extreme.activities.GameActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Board extends Component {
	
	private int height;
	private int width;
	private Row topRow; // index 0
	private Row currentRow;
	private int currentIndex;
	private Row tempRow;
	//private Context context;
	private boolean valid;
	private Bitmap blockMap;
	private Canvas blockVas;
	
	public Board(GameActivity ga) {
		super(ga);
		width = host.getResources().getInteger(R.integer.spalten);
		height = host.getResources().getInteger(R.integer.zeilen);
		valid = false;
		
		/* Init Board */
		topRow = new Row(width,host);
		currentIndex = 0;
		tempRow = topRow;
		currentRow = topRow;
		for(int i = 1; i < height; i++) {
			currentRow = new Row(width,host);
			currentIndex = i;
			currentRow.setAbove(tempRow);
			tempRow.setBelow(currentRow);
			tempRow = currentRow;
		}
		topRow.setAbove(currentRow);
		currentRow.setBelow(topRow);
	}
	
	public void draw(int x, int y, int squareSize, Canvas c){ // top left corner of game board
		if(topRow == null)
			throw new RuntimeException("BlockBoard was not initialized!");
		
		if(valid) {
			c.drawBitmap(blockMap, x, y, null);
			return;
		}
		
		/* This Block is responsible to prevent the
		 * java.lang.OutOfMemoryError: bitmap size exceeds VM budget
		 * Crash.
		 */
		try {
			blockMap = Bitmap.createBitmap(width*squareSize, height*squareSize, Bitmap.Config.ARGB_8888);
		} catch(Exception e) {
			valid = false;
			tempRow = topRow;
			for(int i = 0; i < height; i++) {
				if(tempRow != null) {
					c.drawBitmap(tempRow.drawBitmap(squareSize), x, y+i*squareSize, null);
					tempRow = tempRow.below();
				}
			}
			return;
		}
		
		blockVas = new Canvas(blockMap);
		valid = true;
		tempRow = topRow;
		for(int i = 0; i < height; i++) {
			if(tempRow != null) {
				tempRow.draw(0,0+i*squareSize,squareSize,blockVas);
				tempRow = tempRow.below();
			}
		}
		c.drawBitmap(blockMap, x, y, null);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Square get(int x, int y) {
		if(x < 0)
			return null;
		if(x > (width - 1))
			return null;
		if(y < 0)
			return null;
		if(y > (height - 1))
			return null;
		if(currentIndex == y){
			return currentRow.get(x);
		} else if(currentIndex < y) {
			if(currentRow.below() == null)
				return null;
			else {
				currentRow = currentRow.below();
				currentIndex++;
				return get(x, y);
			}
		} else {
			if(currentRow.above() == null)
				return null;
			else {
				currentRow = currentRow.above();
				currentIndex--;
				return get(x, y);
			}
		}
	}

	public void set(int x, int y, Square square) {
		if(x < 0)
			return;
		if(x > (width - 1))
			return;
		if(y < 0)
			return;
		if(y > (height - 1))
			return;
		if(square == null)
			return;
		if(square.isEmpty())
			return;
		
		valid = false;
		
		if(currentIndex == y)
			currentRow.set(square,x);
		else if(currentIndex < y) {
			currentRow = currentRow.below();
			currentIndex++;
			set(x, y, square);
		} else {
			currentRow = currentRow.above();
			currentIndex--;
			set(x, y, square);
		}
	}

	public void cycle(long time) {
		// begin at bottom line
		if(topRow == null)
			throw new RuntimeException("BlockBoard was not initialized!");
		
		tempRow = topRow.above();
		for(int i = 0; i < height; i++) {
			tempRow.cycle(time, this);
			tempRow = tempRow.above();
			if(tempRow == null)
				return;
		}
	}

	public int clearLines(int dim) {
		valid = false;
		Row clearPointer = currentRow;
		int clearCounter = 0;
		for(int i = 0; i < dim; i++) {
			if(clearPointer.isFull()) {
				clearCounter++;
				clearPointer.clear(this, host.game.getAutoDropInterval());
			}
			clearPointer = clearPointer.above();
		}
		currentRow = topRow;
		currentIndex = 0;
		return clearCounter;
	}

	public Row getTopRow() {
		return topRow;
	}

	public void finishClear(Row row) {
		valid = false;
		topRow = row;
		currentIndex++;
		host.display.invalidatePhantom();
	}

	public void interruptClearAnimation() {
		// begin at bottom line
		if(topRow == null)
			throw new RuntimeException("BlockBoard was not initialized!");
		
		Row interator = topRow.above();
		for(int i = 0; i < height; i++) {
			if(interator.interrupt(this)) {
				interator = topRow.above();
				i = 0;
				valid = false;
			} else
				interator = interator.above();
			if(interator == null)
				return;
		}
		host.display.invalidatePhantom();
	}

	public void invalidate() {
		valid = false;
	}

	public void popupScore(long addScore) {
		//TODO
	}

	public int getCurrentRowIndex() {
		return currentIndex;
	}

	public Row getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRowIndex(int index) {
		currentIndex = index;
	}

	public void setCurrentRow(Row row) {
		currentRow = row;
	}

}
