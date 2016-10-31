package io.github.zkhan93.lanmak;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import io.github.zkhan93.lanmak.callbacks.SocketConnectionClbk;
import io.github.zkhan93.lanmak.events.SocketEvents;
import io.github.zkhan93.lanmak.tasks.SocketConnectionTask;
import io.github.zkhan93.lanmak.utility.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public class SocketConnectionService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener,
        SocketConnectionClbk {
    public static final String TAG = SocketConnectionService.class.getSimpleName();

    private Socket socket;
    private PrintWriter out;
    private final IBinder iBinder;
    private int serviceState;

    {
        serviceState = Constants.SERVICE_STATE.DISCONNECTED;
        iBinder = new LocalBinder();
    }

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
        if (serviceState == Constants.SERVICE_STATE.DISCONNECTED)
            reconnect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void startAttemptToConnect() {
        postUpdateState(Constants.SERVICE_STATE.CONNECTING);
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
        postUpdateState(Constants.SERVICE_STATE.CONNECTED);
        Log.d(TAG, "connected to server");
    }

    public void failedToConnect() {
        postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        Log.d(TAG, "not connected to server");
    }

    /**
     * @param x  - pixels to move in X-Direction
     * @param y  - pixels to move in Y-Direction
     * @param vx - velocity in X-direction
     * @param vy - velocity in Y-direction
     */
    void sendMove(int x, int y, float vx, float vy) {
        if (serviceState != Constants.SERVICE_STATE.CONNECTED)
            return;
        if (out != null) {
            out.println(Constants.ONE + Constants.COLON + Constants.ZERO
                    + Constants.COLON + x + Constants.COLON + y
                    + Constants.COLON + vx + Constants.COLON + vy);
            if (out.checkError())
                postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        } else {
            Log.d(TAG, "out is null");
            postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        }

    }

    void sendScroll(boolean up) {
        if (serviceState != Constants.SERVICE_STATE.CONNECTED)
            return;
        if (out != null) {
            out.println(Constants.ONE + Constants.COLON + Constants.ONE
                    + Constants.COLON + String.valueOf(up ? 4 : 5));
            if (out.checkError())
                postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        } else {
            Log.d(TAG, "out is null");
            postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        }
    }

    void sendClick(int button) {
        if (serviceState != Constants.SERVICE_STATE.CONNECTED)
            return;
        if (out != null) {
            out.println(Constants.ONE + Constants.COLON + Constants.ONE
                    + Constants.COLON + String.valueOf(button));
            if (out.checkError())
                postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        } else {
            Log.d(TAG, "out is null");

            postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        }

    }

    public void send(String command) {
        if (serviceState != Constants.SERVICE_STATE.CONNECTED)
            return;
        if (out != null) {
            out.println(command);
            if (out.checkError())
                postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        } else {
            Log.d(TAG, "out is null");
            postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
        }

    }

    public void sendSpecialKey(int scode) {
        if (serviceState != Constants.SERVICE_STATE.CONNECTED)
            return;
        if (out != null) {
            out.println(Constants.ZERO + Constants.COLON + Constants.ONE
                    + Constants.COLON + scode);
            Log.d(TAG, "out is null");

        } else {
            Log.d(TAG, "out is null");
            postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
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
        postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("server_ip") || key.equals("port")) {
            Log.d(TAG, "re-establishing connection");
            reconnect();
        }
    }

    public void reconnect() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ip = spf.getString("server_ip", Constants.SERVER_IP);
        String port = spf.getString("port", String.valueOf(Constants.PORT));
        new SocketConnectionTask(this, ip, port).execute();
    }

    public int getServiceState() {
        return serviceState;
    }

    private void postUpdateState(int state) {
        if (state != Constants.SERVICE_STATE.CONNECTED && state != Constants.SERVICE_STATE.DISCONNECTED && state != Constants
                .SERVICE_STATE.CONNECTING) {
            Log.d(TAG, "invalid state update");
            return;
        }
        serviceState = state;
        EventBus.getDefault().post(new SocketEvents(state));
    }
}
