package com.example.minesweeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	}

	public void gameAbout(View v) {
		Toast.makeText(getApplicationContext(), "Hello !", Toast.LENGTH_SHORT)
				.show();
		// String url = "http://www.thumbtack.com/challenges/software-engineer";
		// Intent in = new Intent(Intent.ACTION_VIEW);
		// in.setData(Uri.parse(url));
		// startActivity(in);
	}

	public void startGame(View v) {
		startActivity(new Intent(getBaseContext(), MinesweeperActivity.class));
	}

	public void gameInstructions(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(true);
		builder.setTitle("Instructions");
		builder.setMessage(R.string.instructions);

		builder.setNeutralButton("Got it",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();

	}

}
