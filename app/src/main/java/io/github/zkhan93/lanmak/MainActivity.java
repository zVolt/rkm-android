package io.github.zkhan93.lanmak;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

import io.github.zkhan93.lanmak.utility.Constants;


public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener, OutputStreamHandler {
    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * static because we cannot recreate it on every onCrete onResume cycle of activity
     */
    private static Socket socket;
    private static PrintWriter out;
    static boolean CONNECTED;
    static int x1, y1, x2, y2;

    TableLayout specialButtons;
    Fragment fragment;

    int dx, dy;
    long stime, dtime;
    boolean click, scroll, click_hold, move, moved, clicked, prevent_jump, hold_active;
    int drag_threshold = 1, hold_threshold = 120;

    VelocityTracker vtracker;
    boolean sbutton_visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fragment = getFragmentManager().findFragmentByTag(MainFragment.TAG);
            if (fragment == null)
                fragment = new MainFragment();
            getFragmentManager().beginTransaction().replace(R.id.container, fragment, MainFragment.TAG)
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("server_ip")) {
            //TODO: close the connection and reconnect it
        } else if (key.equals("port")) {
            //TODO: close the connection and restart it
        }
        Log.d(TAG, "reestablishing connection");
        new SetNetwork(getApplicationContext(), this, PreferenceManager
                .getDefaultSharedPreferences
                        (getApplicationContext())
                .getString("server_ip", Constants.SERVER_IP), PreferenceManager
                .getDefaultSharedPreferences
                        (getApplicationContext())
                .getString("port", String.valueOf(Constants.PORT))).execute();

    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case (MotionEvent.ACTION_POINTER_DOWN):
                scroll = true;
                click_hold = false;
                click = false;
                move = false;

                // Log.d(TAG, "scrolling start");
                return true;
            case (MotionEvent.ACTION_POINTER_UP):
                scroll = false;
                move = prevent_jump = true;
                click_hold = false;
                return true;
            case (MotionEvent.ACTION_DOWN):
                x1 = Math.round(event.getRawX());
                y1 = Math.round(event.getRawY());
                stime = System.currentTimeMillis();
                prevent_jump = false;
                click_hold = false;
                click = false;
                move = false;
                scroll = false;
                moved = false;
                if (vtracker == null)
                    vtracker = VelocityTracker.obtain();
                else
                    vtracker.clear();
                // Log.d(TAG, "click action");
                // Log.d(TAG, "Action was DOWN: " + x1 + "," + y1);
                return true;
            case (MotionEvent.ACTION_MOVE):
                vtracker.addMovement(event);
                vtracker.computeCurrentVelocity(100);
                x2 = Math.round(event.getRawX());
                y2 = Math.round(event.getRawY());
                dx = x2 - x1;
                dy = y2 - y1;
                dtime = System.currentTimeMillis() - stime;
                moved = Math.abs(dx) > drag_threshold
                        || Math.abs(dy) > drag_threshold;

                if (!scroll && !click_hold && !move) {
                    // scroll
                    /**
                     * pointer count=2 movement
                     */
                    if (event.getPointerCount() == 2 && moved) {
                        Log.d(TAG, "scrolling selected ");
                        scroll = true;
                    }
                    // move
                    /**
                     * pointer count =1 movement
                     */
                    if (event.getPointerCount() == 1 && moved) {
                        Log.d(TAG, "moving selected");
                        move = true;

                    }
                    // click hold
                    /**
                     * p[ointer count=1 no movement hold for some time
                     */
                    if (event.getPointerCount() == 1 && !moved
                            && dtime > hold_threshold) {
                        click_hold = true;
                        move = true;
                        Log.d(TAG, "holding selected");
                        // click down
                    }
                }
                if (scroll) {
                    if (Math.abs(dy) > 3)
                        sendScroll(dy > 0);
                } else if (click_hold) {
                    if (!clicked) {
                        sendClick(6);
                        clicked = true;
                        Log.d(TAG, "holding");
                    }

                }
                if (move) {
                    if (!prevent_jump)
                        sendMove(dx, dy, vtracker.getXVelocity(),
                                vtracker.getYVelocity());
                    if (prevent_jump)
                        prevent_jump = false;
                }

                x1 = x2;
                y1 = y2;
                return true;
            case (MotionEvent.ACTION_UP):
                // Log.d(TAG, "Action was UP: " + x2 + "," + y2);
                dtime = System.currentTimeMillis() - stime;
                // click
                /**
                 * pointer=1 no movement
                 */
                if (event.getPointerCount() == 1 && !moved) {
                    if (dtime < hold_threshold) {
                        // Log.d(TAG, "Action was click");
                        sendClick(1);
                        Log.d(TAG, "click");
                    } else {
                        if (clicked) {
                            sendClick(7);
                            Log.d(TAG, "hold done");
                            clicked = false;
                        }
                    }
                }
                move = scroll = click_hold = false;
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(TAG, "Action was CANCEL");
                vtracker.recycle();
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                // Log.d(TAG, "Movement occurred outside bounds "
                // + "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    /**
     * @param x  - pixels to move in X-Direction
     * @param y  - pixels to move in Y-Direction
     * @param vx - velocity in X-direction
     * @param vy - velocity in Y-direction
     */
    void sendMove(int x, int y, float vx, float vy) {
        try {
            if (out != null) {
                out.println(Constants.ONE + Constants.COLON + Constants.ZERO
                        + Constants.COLON + x + Constants.COLON + y
                        + Constants.COLON + vx + Constants.COLON + vy);
                System.out.println(Constants.ONE + Constants.COLON + Constants.ZERO
                        + Constants.COLON + x + Constants.COLON + y
                        + Constants.COLON + vx + Constants.COLON + vy);
            } else {
                System.out.println("out is null");
            }
        } catch (Exception e) {
            // reconnect server
        }
    }

    void sendScroll(boolean up) {
        try {
            if (out != null) {
                out.println(Constants.ONE + Constants.COLON + Constants.ONE
                        + Constants.COLON + String.valueOf(up ? 4 : 5));
                System.out.println(Constants.ONE + Constants.COLON + Constants.ONE
                        + Constants.COLON + String.valueOf(up ? 4 : 5));
            } else {
                System.out.println("out is null");
            }
        } catch (Exception e) {
            // reconnect server
        }
    }

    void sendClick(int button) {
        try {
            if (out != null) {
                out.println(Constants.ONE + Constants.COLON + Constants.ONE
                        + Constants.COLON + String.valueOf(button));
                System.out.println(Constants.ONE + Constants.COLON + Constants.ONE
                        + Constants.COLON + String.valueOf(button));
            } else {
                System.out.println("out is null");
            }
        } catch (Exception e) {
            // reconnect server
        }
    }

    public void toggleSpecialButtons() {
        if (specialButtons != null) {
            if (sbutton_visible)
                specialButtons.setVisibility(View.GONE);
            else
                specialButtons.setVisibility(View.VISIBLE);
            sbutton_visible = !sbutton_visible;
        }
    }

    public void showSpecialButtons(View view) {
        toggleSpecialButtons();
    }

    public void leftClick(View view) {
        sendClick(1);
    }

    public void rightClick(View view) {
        sendClick(3);
    }

    public void specialKey(View view) {
        int scode = 0;
        switch (view.getId()) {
            case R.id.buttonspecial11:
                scode = 1;
                break;
            case R.id.buttonspecial12:
                scode = 2;
                break;
            case R.id.buttonspecial13:
                scode = 3;
                break;
            case R.id.buttonspecial14:
                scode = 4;
                break;
            case R.id.buttonspecial21:
                scode = 5;
                break;
            case R.id.buttonspecial22:
                scode = 6;
                break;
            case R.id.buttonspecial23:
                scode = 7;
                break;
            case R.id.buttonspecial24:
                scode = 8;
                break;
            case R.id.buttonspecial15:
                scode = 9;
                break;
            case R.id.buttonspecial16:
                scode = 10;
                break;
            case R.id.buttonspecial25:
                scode = 11;
                break;
            case R.id.buttonspecial26:
                scode = 12;
                break;

        }
        if (out != null) {
            out.println(Constants.ZERO + Constants.COLON + Constants.ONE
                    + Constants.COLON + scode);
        }
        toggleSpecialButtons();
    }

    @Override
    public void setSocket(Socket socket) throws IOException {
        try {
            if (this.socket != null && this.socket.isConnected())
                this.socket.close();
        } catch (IOException ex) {
            Log.d(TAG, "exception occured while closing previous socket: " + ex.getLocalizedMessage());
        }
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void send(String command) {
        try {
            if (out != null) {
                out.println(command);
                System.out.println(command);
            } else {
                System.out.println("out is null");
                MainActivity.CONNECTED = false;
            }
        } catch (Exception e) {
            // reconnect server
        }
    }

    @Override
    public void close() {
        try {
            if (this.socket != null && this.socket.isConnected())
                this.socket.close();
        } catch (IOException ex) {
            Log.d(TAG, "exception occured while closing previous socket: " + ex.getLocalizedMessage());
        }
        if (out != null)
            out.close();
    }

    public static class SetNetwork extends AsyncTask<Void, Void, Boolean> {
        WeakReference<Context> contextRef;
        WeakReference<OutputStreamHandler> outputStreamHandlerRef;
        private String ip;
        private int port;

        public SetNetwork(Context context, OutputStreamHandler outputStreamHandler, String ip, String port) {
            contextRef = new WeakReference<>(context);
            outputStreamHandlerRef = new WeakReference<>(outputStreamHandler);
            this.ip = ip;
            this.port = Integer.parseInt(port);
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            try {

                if (ip != null && !ip.isEmpty()) {
                    outputStreamHandlerRef.get().setSocket(new Socket(ip, port));
                    CONNECTED = true;
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
                Toast.makeText(contextRef.get(), contextRef.get().getString(R.string.sever_connected),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(contextRef.get(),
                        contextRef.get().getString(R.string.sever_not_connected),
                        Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }


}
