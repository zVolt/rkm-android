package io.github.zkhan93.lanmak;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.Socket;

import static io.github.zkhan93.lanmak.SettingsActivity.GeneralPreferenceFragment.TAG;

/**
 * Created by Zeeshan Khan on 10/31/2016.
 */

public class SetNetwork extends AsyncTask<Void, Void, Boolean> {
    WeakReference<SocketConnectionClbk> outputStreamHandlerRef;
    private String ip;
    private int port;

    public SetNetwork(SocketConnectionClbk socketConnectionClbk, String ip, String port) {
        outputStreamHandlerRef = new WeakReference<>(socketConnectionClbk);
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    @Override
    protected Boolean doInBackground(Void... args) {
        try {

            if (ip != null && !ip.isEmpty()) {
                outputStreamHandlerRef.get().setSocket(new Socket(ip, port));
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
//            Toast.makeText(contextRef.get(), contextRef.get().getString(R.string.sever_connected),
//                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "connected to ->" + ip + ":" + port);
        } else {
//            Toast.makeText(contextRef.get(),
//                    contextRef.get().getString(R.string.sever_not_connected),
//                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "not connected to -> " + ip + ":" + port);
        }
        super.onPostExecute(result);
    }

}
