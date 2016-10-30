package io.github.zkhan93.lanmak;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import io.github.zkhan93.lanmak.utility.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public class SocketConnectionService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener,
        SocketConnectionClbk {
    public static final String TAG = SocketConnectionService.class.getSimpleName();
    private Socket socket;
    private PrintWriter out;
    private final IBinder iBinder = new LocalBinder();
    private boolean connected;

    public class LocalBinder extends Binder {
        public SocketConnectionService getService() {
            return SocketConnectionService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!connected)
            reconnect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
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
        connected = true;
        Log.d(TAG, "connected to server");
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

    public void send(String command) {
        try {
            if (out != null) {
                out.println(command);
                System.out.println(command);
            } else {
                System.out.println("out is null");
            }
        } catch (Exception e) {
            // reconnect server
        }
    }

    public void sendSpecialKey(int scode) {
        if (out != null) {
            out.println(Constants.ZERO + Constants.COLON + Constants.ONE
                    + Constants.COLON + scode);
        }
    }

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("server_ip") || key.equals("port")) {
            Log.d(TAG, "reestablishing connection");
            reconnect();
        }
    }

    private void reconnect() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ip = spf.getString("server_ip", Constants.SERVER_IP);
        String port = spf.getString("port", String.valueOf(Constants.PORT));
        new SetNetwork(this, ip, port).execute();
    }
}
