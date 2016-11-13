package io.github.zkhan93.lanmak.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.github.zkhan93.lanmak.events.HostEvents;
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
            datagramSocket.setSoTimeout(120000);
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket;
            while (true) {
                receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                Log.d(TAG, "waiting for packet from server");
                datagramSocket.receive(receivePacket);
                Log.d(TAG, "we have a packet");
                String msg = new String(receivePacket.getData()).trim();
                Log.d(TAG, "from " + receivePacket.getAddress().getHostName() + " data:" + msg);
                if (msg.startsWith("DISCOVER_RKMS_RESPONSE")) {
                    EventBus.getDefault().post(new HostEvents(new Host(receivePacket.getAddress().getHostName(), receivePacket
                            .getAddress().getHostAddress())));
                } else {
                    Log.d(TAG, "some other packet ");
                }
            }
        } catch (SocketTimeoutException ex) {
            Log.d(TAG, "time up no more servers");
            //TODO: show rescan issue
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
