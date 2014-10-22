package com.example.minesweeper;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MinesweeperActivity extends Activity {

	public static final int ROWS = 8;
	public static final int COLUMNS = 8;
	public static final int MINES = 10;

	public GameTile[][] grid;

	int column_width, cheat, noOfMines;
	float displayWidth;
	private boolean firstClickFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_minesweeper);
		cheat = 0;
		noOfMines = MINES;
		TextView mineCount = (TextView) findViewById(R.id.mineCount);
		mineCount.setText("" + MINES);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		displayWidth = metrics.widthPixels;
		column_width = (int) (displayWidth / (ROWS + 1));

		showMineField(column_width, column_width);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.minesweeper, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.restartGame:
			restartGame();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// -------------- Setup MineField and Plant Mines------------------
	/*
	 * Displays a 8 x 8 grid of Tiles. Initially has no mines providing the
	 * player a fair first chance.
	 */
	public void showMineField(int w, int h) {

		TableLayout mineField = (TableLayout) findViewById(R.id.mineField);
		grid = new GameTile[ROWS][COLUMNS];

		for (int row = 0; row < ROWS; ++row) {
			final int drow = row;
			TableRow mineRow = new TableRow(getApplicationContext());

			for (int col = 0; col < COLUMNS; ++col) {
				final int dcol = col;
				grid[row][col] = new GameTile(getApplicationContext());
				grid[row][col].initGameTile(w, h);

				// Set normal click...
				grid[row][col].setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!firstClickFlag)
							setMines(drow, dcol);
						if (!checkLose(drow, dcol)) {
							openTilesRecursive(drow, dcol);
						} else {
							loseGame();
						}
					}
				});

				// Set long click...
				grid[row][col]
						.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								setQorF(drow, dcol);
								return true;
							}
						});

				mineRow.addView(grid[row][col]);
			}
			mineField.addView(mineRow);

		}
	}

	/*
	 * Sets the mines in the Mine Field randomly. This happens after the users
	 * first click on any tile.
	 */
	public void setMines(final int rw, final int cl) {
		firstClickFlag = true;
		Random rand = new Random();

		for (int mines = 1; mines <= MINES; ++mines) {
			int rand_row = rand.nextInt(ROWS);
			int rand_col = rand.nextInt(COLUMNS);

			// prevent the current clicked tile to be set as a Mine.
			if ((rand_row == rw) && (rand_col == cl))
				continue;

			if (rand_row != ROWS && rand_col != COLUMNS) {
				if (grid[rand_row][rand_col].isMine())
					mines--;
				else {
					grid[rand_row][rand_col].plantMine();

					// go one row and col back
					int startRow = rand_row - 1;
					int startCol = rand_col - 1;
					// check 3 rows across and 3 down
					int checkRows = 3;
					int checkCols = 3;
					if (startRow < 0) // if it is on the first row
					{
						startRow = 0;
						checkRows = 2;
					} else if (startRow + 3 > ROWS) // if it is on the last row
						checkRows = 2;

					if (startCol < 0) {
						startCol = 0;
						checkCols = 2;
					} else if (startCol + 3 > COLUMNS) // if it is on the last
						checkCols = 2;
					// 3 rows across & 3 rows down
					for (int j = startRow; j < startRow + checkRows; j++) {
						for (int k = startCol; k < startCol + checkCols; k++) {
							if (!grid[j][k].isMine())
								++grid[j][k].noOfAdjMines;
						}
					}
				}
			}
		}

	}

	/*
	 * Displays a Popup on LongClick of a Tile. Player can select a Question
	 * Mark or Flag.
	 */
	private void setQorF(final int row, final int col) {

		RelativeLayout pr = (RelativeLayout) findViewById(R.id.popup);
		final TextView mineCount = (TextView) findViewById(R.id.mineCount);
		View popupView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.popup, null);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(getApplicationContext());
		popup.setContentView(popupView);
		popup.setWidth(column_width * 3);
		popup.setHeight(column_width * 2);
		popup.setFocusable(true);
		popup.showAsDropDown(grid[row][col]);

		Button popupF = (Button) popupView.findViewById(R.id.popupF);
		popupF.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (grid[row][col].isFlagged()) {
					// Remove flag
					grid[row][col].setFlagged(false);
					grid[row][col].setText("");
					grid[row][col].setTextColor(Color.BLACK);
					int mn = ++noOfMines;
					mineCount.setText("" + mn);
					grid[row][col]
							.setBackgroundResource(R.drawable.active_tile);
				} else {
					grid[row][col].setFlagged(true);
					grid[row][col].setText("F");
					grid[row][col].setTextColor(Color.BLUE);
					grid[row][col].setBackgroundColor(Color.GREEN);
					int mn = --noOfMines;
					mineCount.setText("" + mn);
				}
				popup.dismiss();
			}
		});

		Button popupQ = (Button) popupView.findViewById(R.id.popupQ);
		popupQ.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (grid[row][col].isQuestionMark()) {
					// Remove question mark
					grid[row][col].setQuestionMark(false);
					grid[row][col].setText("");
					grid[row][col].setTextColor(Color.BLACK);
					grid[row][col]
							.setBackgroundResource(R.drawable.active_tile);
				} else {
					grid[row][col].setQuestionMark(true);
					grid[row][col].setText("?");
					grid[row][col].setTextColor(Color.MAGENTA);
					grid[row][col].setBackgroundColor(Color.YELLOW);
				}
				popup.dismiss();
			}
		});

	}

	/*
	 * Start a new game session
	 */
	public void restartGame() {
		TableLayout mineField = (TableLayout) findViewById(R.id.mineField);
		mineField.removeAllViews();
		firstClickFlag = false;
		noOfMines = MINES;

		ImageButton ibutton = (ImageButton) findViewById(R.id.smiley);
		ibutton.setImageResource(R.drawable.smile_start);

		TextView mineCount = (TextView) findViewById(R.id.mineCount);
		mineCount.setText("" + MINES);

		showMineField(column_width, column_width);
	}

	// ----------------Gameplay Logic---------------------------------
	/*
	 * Opens the 8 connected neighbours of current tile if they have 0 adjacent
	 * tile count. Else, stops.
	 */
	public void openTilesRecursive(int row, int col) {
		if (grid[row][col].isMine() || grid[row][col].isFlagged())
			return;

		grid[row][col].openTile();

		if (grid[row][col].noOfAdjMines != 0)
			return;

		int startRow = row - 1;
		int startCol = col - 1;
		// check 3 rows across and 3 down
		int checkRows = 3;
		int checkCols = 3;
		if (startRow < 0) // if it is on the first row
		{
			startRow = 0;
			checkRows = 2;
		} else if (startRow + 3 > ROWS) // if it is on the last row
			checkRows = 2;

		if (startCol < 0) {
			startCol = 0;
			checkCols = 2;
		} else if (startCol + 3 > COLUMNS) // if it is on the last row
			checkCols = 2;

		for (int i = startRow; i < startRow + checkRows; i++) {
			for (int j = startCol; j < startCol + checkCols; j++) {
				if (grid[i][j].isCovered())
					openTilesRecursive(i, j);
			}
		}
	}

	/*
	 * called on Smiley click to validate.
	 */
	public void onSmileyClick(View v) {
		if (!firstClickFlag) // prevent validation when user has not started the
								// game
			return;

		if (validate())
			winGame();
		else
			loseGame();
	}

	/*
	 * Checks if all the Mines are flagged or covered on Smiley clicked.
	 */
	public boolean validate() {
		for (int r = 0; r < ROWS; ++r) {
			for (int c = 0; c < COLUMNS; ++c) {
				if (grid[r][c].isMine()) {
					if (noOfMines <= 0) {
						if (!grid[r][c].isFlagged())
							return false;
					} else {
						if (!grid[r][c].isCovered())
							return false;
					}
				}
			}
		}
		return true;
	}

	/*
	 * Checks if user clicked a Mine.
	 */
	public boolean checkLose(int row, int col) {
		return grid[row][col].isMine();
	}

	/*
	 * Displays a Dialog indicating Game Over. Option to Restart the game.
	 */
	public void loseGame() {
		ImageButton ibutton = (ImageButton) findViewById(R.id.smiley);
		ibutton.setImageResource(R.drawable.smiley_lose);
		showMines(false);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MinesweeperActivity.this);
		builder.setCancelable(true);
		builder.setTitle("Game Over :(");
		builder.setIcon(R.drawable.smiley_lose);
		builder.setNeutralButton("Restart Game",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						restartGame();
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/*
	 * Displays a Dialog indicating Game won. Option to play a New Game.
	 */
	public void winGame() {
		ImageButton ibutton = (ImageButton) findViewById(R.id.smiley);
		ibutton.setImageResource(R.drawable.smiley_win);
		showMines(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MinesweeperActivity.this);
		builder.setCancelable(true);
		builder.setTitle("Congratultions..You WIN !!!");
		builder.setIcon(R.drawable.smiley_win);
		builder.setNeutralButton("New Game",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						restartGame();
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/*
	 * Displays the mines in the minefield on win or lose to the player.
	 */

	public void showMines(boolean gameWon) {
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLUMNS; ++j) {
				if (grid[i][j].isMine()) {
					if (gameWon || grid[i][j].isFlagged()) {
						grid[i][j].setBackgroundColor(Color.GREEN);
						grid[i][j].setText("Y");
					} else {
						grid[i][j].setBackgroundColor(Color.RED);
						grid[i][j].setText("X");
					}
				}
			}
		}
	}

	// start a 
	public void setTimer(View v) {

	}
	/*
	 * public void cheat(View v) { // can be used to call different types of
	 * game cheats. gameCheat(++cheat); }
	 * 
	 * /* Allows the player 3 cheats by automatically flagging mines. If user
	 * wants more cheats, opens a dialog to buy cheats.
	 * 
	 * public void gameCheat(int cheatCount) { if (cheatCount > 3) {
	 * AlertDialog.Builder builder = new AlertDialog.Builder(
	 * MinesweeperActivity.this); builder.setCancelable(true);
	 * builder.setTitle("You are out of Cheats");
	 * builder.setIcon(R.drawable.logo); builder.setNeutralButton("Buy Cheats",
	 * new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * String url = "http://www.thumbtack.com/"; // Intent in = new
	 * Intent(Intent.ACTION_VIEW); // in.setData(Uri.parse(url)); //
	 * startActivity(in); dialog.dismiss(); } }); builder.create().show(); }
	 * else { for (int i = 0; i < ROWS; ++i) { for (int j = 0; j < COLUMNS; j++)
	 * { if (grid[i][j].isMine() && !grid[i][j].isFlagged()) {
	 * grid[i][j].setFlagged(true); grid[i][j].setText("F");
	 * grid[i][j].setTextColor(Color.BLUE);
	 * grid[i][j].setBackgroundColor(Color.GREEN); TextView mineCount =
	 * (TextView) findViewById(R.id.mineCount); mineCount.setText("" +
	 * --noOfMines); return; } } } } }
	 */
	// MineSweeperActivity ends....
}
