package com.example.minesweeper;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/*
 * GameTile extends Button class to include the private members for the game.
 * */

public class GameTile extends Button {

	private Context mContext;
	private boolean isCovered;
	private boolean isMine;
	private boolean isFlagged;
	private boolean isQuestionMark;
	public int noOfAdjMines;

	public GameTile(Context ctxt) {
		super(ctxt);
		mContext = ctxt;
	}

	public void initGameTile(int width, int height) {
		isCovered = true;
		isMine = false;
		isFlagged = false;
		isQuestionMark = false;
		noOfAdjMines = 0;
		Log.d("Minew", "GameTile: " + width);
		this.setWidth(width);
		this.setHeight(height);
		this.setBackgroundResource(R.drawable.active_tile);
	}

	public void openTile() {
		isCovered = false;
		this.setEnabled(false);
		this.setText("" + noOfAdjMines);
		this.setBackgroundResource(R.drawable.inactive_tile);
		this.setClickable(false);
	}

	// ----Getter/Setter---------
	public boolean isCovered() {
		return isCovered;
	}

	public boolean isMine() {
		return isMine;
	}

	public void plantMine() {
		this.isMine = true;
	}

	public int getNoOfAdjMines() {
		return this.noOfAdjMines;
	}

	public void setNoOfAdjMines(int noOfMines) {
		this.noOfAdjMines = noOfMines;
	}

	public boolean isFlagged() {
		return isFlagged;
	}

	public void setFlagged(boolean isFlagged) {
		this.isFlagged = isFlagged;
	}

	public boolean isQuestionMark() {
		return isQuestionMark;
	}

	public void setQuestionMark(boolean isQuestionMark) {
		this.isQuestionMark = isQuestionMark;
	}

}
