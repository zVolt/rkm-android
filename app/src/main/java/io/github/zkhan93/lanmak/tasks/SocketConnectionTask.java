package io.github.zkhan93.lanmak.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.github.zkhan93.lanmak.callbacks.SocketConnectionClbk;

/**
 * Created by Zeeshan Khan on 10/31/2016.
 */

public class SocketConnectionTask extends AsyncTask<Void, Void, Boolean> {
    public static String TAG = SocketConnectionTask.class.getSimpleName();

    WeakReference<SocketConnectionClbk> socketConnectionClbkRef;
    private String ip;
    private int port;

    public SocketConnectionTask(SocketConnectionClbk socketConnectionClbk, String ip, String port) {
        socketConnectionClbkRef = new WeakReference<>(socketConnectionClbk);
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    @Override
    protected Boolean doInBackground(Void... args) {
        try {
            if (ip != null && !ip.isEmpty()) {
                Log.d(TAG, "trying to connect to server");
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), 4000);
                socketConnectionClbkRef.get().setSocket(socket);
                return true;
            } else {
                Log.d(TAG, "invalid ip:port");
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "error connecting " + e.getLocalizedMessage());
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result)
            Log.d(TAG, "connected to ->" + ip + ":" + port);
        else {
            socketConnectionClbkRef.get().failedToConnect();
            Log.d(TAG, "not connected to -> " + ip + ":" + port);
        }
    }

}
