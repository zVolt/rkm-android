package io.github.zkhan93.lanmak.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import io.github.zkhan93.lanmak.events.HostFoundEvents;
import io.github.zkhan93.lanmak.events.HostSearchOverEvent;
import io.github.zkhan93.lanmak.models.Host;

/**
 * Created by Zeeshan Khan on 11/6/2016.
 */

public class ServerBroadcastReceiverTask extends AsyncTask<Void, Void, Void> {
    public static final String TAG = ServerBroadcastReceiverTask.class.getSimpleName();

    private DatagramSocket datagramSocket;

    public ServerBroadcastReceiverTask(@NonNull DatagramSocket socket) {

        datagramSocket = socket;
        try {
            datagramSocket.setBroadcast(true);
        } catch (Exception ex) {
            Log.d(TAG, "" + ex.getLocalizedMessage());
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            //receiving code
            datagramSocket.setSoTimeout(30000);
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket;
            while (true) {
                receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                Log.d(TAG, "waiting for packet from server");
                datagramSocket.receive(receivePacket);
                Log.d(TAG, "we have a packet");
                try {
                    Log.d(TAG, new String(receivePacket.getData()).trim());
                    Host host = new Gson().fromJson(new String(receivePacket.getData()).trim(), Host.class);
                    EventBus.getDefault().post(new HostFoundEvents(host));
                } catch (Exception ex) {
                    Log.d(TAG, "some other packet " + ex.getLocalizedMessage());
                }
//                Log.d(TAG, "from " + receivePacket.getAddress().getHostName() + " data:" + msg);
//                if (msg.startsWith("DISCOVER_RKMS_RESPONSE")) {
//
//                } else {
//                    Log.d(TAG, "some other packet ");
//                }
            }
        } catch (SocketTimeoutException ex) {
            Log.d(TAG, "time up no more servers");
            //TODO: show rescan issue
            EventBus.getDefault().post(new HostSearchOverEvent());
        } catch (Exception ex) {
            Log.d(TAG, "exception in receving packets" + ex.getLocalizedMessage());
        } finally {
            //assuming that ServerBroadcastReceiverTask will finish in 10 sec and all responses will be here
            try {
                if (datagramSocket != null)
                    datagramSocket.close();
            } catch (Exception ex) {
                Log.d(TAG, "error closing client" + ex.getLocalizedMessage());
            }
        }
        return null;
    }
}
