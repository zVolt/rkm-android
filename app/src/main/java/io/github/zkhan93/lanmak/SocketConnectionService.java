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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import io.github.zkhan93.lanmak.callbacks.SocketConnectionClbk;
import io.github.zkhan93.lanmak.events.ActionDisconnectEvent;
import io.github.zkhan93.lanmak.events.SocketEvents;
import io.github.zkhan93.lanmak.tasks.SocketConnectionTask;
import io.github.zkhan93.lanmak.utility.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public class SocketConnectionService extends Service implements SharedPreferences
        .OnSharedPreferenceChangeListener,
        SocketConnectionClbk {
    public static final String TAG = SocketConnectionService.class.getSimpleName();
    private final IBinder iBinder;
    private Socket socket;
    private PrintWriter out;
    private int serviceState;
    private Thread thread;

    {
        serviceState = Constants.SERVICE_STATE.DISCONNECTED;
        iBinder = new LocalBinder();
        thread = new Thread();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (serviceState == Constants.SERVICE_STATE.DISCONNECTED)
            reconnect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .unregisterOnSharedPreferenceChangeListener(this);
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
            Log.d(TAG, "exception occured while closing previous socket: " + ex
                    .getLocalizedMessage());
        }
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        postUpdateState(Constants.SERVICE_STATE.CONNECTED);
        Log.d(TAG, "connected to server");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionDisconnectEvent event) {
        close();
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
    void sendMove(final int x, final int y, final float vx, final float vy) {
        performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    void sendScroll(final boolean up) {
        performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    void sendClick(final int button) {
        performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void send(final String command) {
        performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void sendSpecialKey(final int scode) {
        performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void close() {
        try {
            if (this.socket != null && this.socket.isConnected())
                this.socket.close();
        } catch (IOException ex) {
            Log.d(TAG, "exception occured while closing previous socket: " + ex
                    .getLocalizedMessage());
        }
        if (out != null)
            out.close();
        postUpdateState(Constants.SERVICE_STATE.DISCONNECTED);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "shared preferences changed");
        if (key.equals("server_ip") || key.equals("port")) {
            Log.d(TAG, "re-establishing connection");
            reconnect();
        }
    }

    public void reconnect() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        String ip = spf.getString("server_ip", Constants.SERVER_IP);
        String port = spf.getString("port", String.valueOf(Constants.PORT));
        new SocketConnectionTask(this, ip, port).execute();
    }

    public int getServiceState() {
        return serviceState;
    }

    private void postUpdateState(int state) {
        if (state != Constants.SERVICE_STATE.CONNECTED && state != Constants.SERVICE_STATE
                .DISCONNECTED && state != Constants
                .SERVICE_STATE.CONNECTING) {
            Log.d(TAG, "invalid state update");
            return;
        }
        serviceState = state;
        EventBus.getDefault().post(new SocketEvents(state));
    }

    private Thread performOnBackgroundThread(final Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
        return t;
    }

    class LocalBinder extends Binder {
        SocketConnectionService getService() {
            return SocketConnectionService.this;
        }
    }
}
