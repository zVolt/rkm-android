package io.github.zkhan93.remotekeyboardandmouse;

import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends Activity {
	static MainFragment mf;
	static float x1, y1, x2, y2;
	static Socket s;
	static PrintWriter put;
	static boolean connected;
	static String TAG = "io.github.zkhan93.remotekeyboardandmouse.MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (mf == null) {
			mf = new MainFragment();
			getFragmentManager().beginTransaction().replace(R.id.container, mf)
					.commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			x1 = event.getX();
			y1 = event.getY();
			Log.d(TAG, "Action was DOWN: " + x1 + "," + y1);
			return true;
		case (MotionEvent.ACTION_MOVE):
			x2 = event.getX();
			y2 = event.getY();
			// Log.d(TAG, "Action was MOVE: " + (int) (x2 - x1) + ","
			// + (int) (y2 - y1));
			sendMove((int) (x2 - x1), (int) (y2 - y1));
			x1 = x2;
			y1 = y2;
			return true;
		case (MotionEvent.ACTION_UP):
			Log.d(TAG, "Action was UP");
			return true;
		case (MotionEvent.ACTION_CANCEL):
			Log.d(TAG, "Action was CANCEL");
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			Log.d(TAG, "Movement occurred outside bounds "
					+ "of current screen element");
			return true;
		default:
			return super.onTouchEvent(event);
		}
	}

	void sendMove(int x, int y) {
		try {
			if (put != null)
				put.println("1:0:" + x + ":" + y);
		} catch (Exception e) {
			// reconnect server
		}
	}

	public static class SetNw extends AsyncTask<String, Void, Boolean> {
		Context cont;

		public SetNw(Context con) {
			cont = con;
		}

		@Override
		protected Boolean doInBackground(String... ip) {
			try {

				if (ip != null && ip[0] != null) {
					MainActivity.s = new Socket(ip[0], Constants.PORT);
					MainActivity.put = new PrintWriter(
							MainActivity.s.getOutputStream(), true);
					connected = true;
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(cont, "connected to server", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(cont,
						"uanble to connect! check server IP in setting",
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
}
