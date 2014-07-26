package org.blockinger.extreme.pieces;

import org.blockinger.extreme.Square;
import org.blockinger.extreme.components.Board;

import android.content.Context;

public abstract class Piece3x3 extends Piece {
	
	/**
	 * Always call super(); first.
	 * @param width
	 * @param height
	 */
	protected Piece3x3(Context c) {
		super(c,3);
	}
	
	/**
	 * @return true if rotation was successfull.
	 */
	@Override
	public boolean turnLeft(Board board) {
		int maxLeftOffset = -4;
		int maxRightOffset = -4;
		int maxBottomOffset = -4;
		int leftOffset = 0;
		int rightOffset = 0;
		int bottomOffset = 0;
		Square backup[][] = pattern;
		// [0][0] ... [0][2]
		//  ....       ....
		// [2][0] ... [2][2]
		rotated[0][0] = pattern[0][2];
		rotated[0][1] = pattern[1][2];
		rotated[0][2] = pattern[2][2];
		
		rotated[1][0] = pattern[0][1];
		rotated[1][1] = pattern[1][1]; //center stays identical
		rotated[1][2] = pattern[2][1];
		
		rotated[2][0] = pattern[0][0];
		rotated[2][1] = pattern[1][0];
		rotated[2][2] = pattern[2][0];
		// check for border violations and collisions
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j++) {
				if(rotated[i][j] != null) {
					leftOffset = - (x+j);
					rightOffset = (x+j) - (board.getWidth() - 1);
					bottomOffset = (y+i) - (board.getHeight() - 1);
					if(!rotated[i][j].isEmpty()) // left border violation
						if (maxLeftOffset < leftOffset)
							maxLeftOffset = leftOffset;
					if(!rotated[i][j].isEmpty()) // right border violation
						if (maxRightOffset < rightOffset)
							maxRightOffset = rightOffset;
					if(!rotated[i][j].isEmpty()) // bottom border violation
						if (maxBottomOffset < bottomOffset)
							maxBottomOffset = bottomOffset;
					if(board.get(x+j,y+i) != null)
						if(!rotated[i][j].isEmpty() && !board.get(x+j,y+i).isEmpty()) // collision
							return false;
				}
			}
		}
		
		backup = pattern;
		pattern = rotated;
		rotated = backup;
		
		// try to correct border violations
		if(maxBottomOffset < 1) {
			if(maxLeftOffset < 1)  {
				if(maxRightOffset < 1) {
					reDraw();
					return true;
				} else {
					if(setPosition(x - maxRightOffset, y, false, board)) {
						reDraw();
						return true;
					} else {
						rotated = pattern;
						pattern = backup;
						return false;
					}
				}
			} else {
				if(setPosition(x + maxLeftOffset, y, false, board)) {
					reDraw();
					return true;
				} else {
					rotated = pattern;
					pattern = backup;
					return false;
				}
			}
		} else {
			if(setPosition(x, y - maxBottomOffset, false, board)) {
				reDraw();
				return true;
			} else {
				rotated = pattern;
				pattern = backup;
				return false;
			}
		}
	}

	/**
	 * @return true if rotation was successfull.
	 */
	@Override
	public boolean turnRight(Board board) {
		int maxLeftOffset = -4;
		int maxRightOffset = -4;
		int maxBottomOffset = -4;
		int leftOffset = 0;
		int rightOffset = 0;
		int bottomOffset = 0;
		Square backup[][] = pattern;
		// [0][0] ... [0][2]
		//  ....       ....
		// [2][0] ... [2][2]
		rotated[0][0] = pattern[2][0];
		rotated[0][1] = pattern[1][0];
		rotated[0][2] = pattern[0][0];
		
		rotated[1][0] = pattern[2][1];
		rotated[1][1] = pattern[1][1]; //center stays identical
		rotated[1][2] = pattern[0][1];
		
		rotated[2][0] = pattern[2][2];
		rotated[2][1] = pattern[1][2];
		rotated[2][2] = pattern[0][2];

		// check for border violations and collisions
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j++) {
				if(rotated[i][j] != null) {
					leftOffset = - (x+j);
					rightOffset = (x+j) - (board.getWidth() - 1);
					bottomOffset = (y+i) - (board.getHeight() - 1);
					if(!rotated[i][j].isEmpty()) // left border violation
						if (maxLeftOffset < leftOffset)
							maxLeftOffset = leftOffset;
					if(!rotated[i][j].isEmpty()) // right border violation
						if (maxRightOffset < rightOffset)
							maxRightOffset = rightOffset;
					if(!rotated[i][j].isEmpty()) // bottom border violation
						if (maxBottomOffset < bottomOffset)
							maxBottomOffset = bottomOffset;
					if(board.get(x+j,y+i) != null)
						if(!rotated[i][j].isEmpty() && !board.get(x+j,y+i).isEmpty()) // collision
							return false;
				}
			}
		}
		
		backup = pattern;
		pattern = rotated;
		rotated = backup;
		
		// try to correct border violations
		if(maxBottomOffset < 1) {
			if(maxLeftOffset < 1)  {
				if(maxRightOffset < 1) {
					reDraw();
					return true;
				} else {
					if(setPosition(x - maxRightOffset, y, false, board)) {
						reDraw();
						return true;
					} else {
						rotated = pattern;
						pattern = backup;
						return false;
					}
				}
			} else {
				if(setPosition(x + maxLeftOffset, y, false, board)) {
					reDraw();
					return true;
				}else {
					rotated = pattern;
					pattern = backup;
					return false;
				}
			}
		} else {
			if(setPosition(x, y - maxBottomOffset, false, board)) {
				reDraw();
				return true;
			} else {
				rotated = pattern;
				pattern = backup;
				return false;
			}
		}
	}
}
