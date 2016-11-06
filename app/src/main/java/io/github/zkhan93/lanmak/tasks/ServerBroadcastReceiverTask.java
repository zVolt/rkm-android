package io.github.zkhan93.lanmak.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Zeeshan Khan on 11/6/2016.
 */

public class ServerBroadcastReceiverTask extends AsyncTask<Void, Void, Void> {
    public static final String TAG = ServerBroadcastReceiverTask.class.getSimpleName();

    private DatagramSocket datagramSocket;

    public ServerBroadcastReceiverTask(DatagramSocket datagramSocket) {
        //TODO:check for null
        this.datagramSocket = datagramSocket;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            //receiving code
            datagramSocket.setSoTimeout(10000);
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket;
            while (true) {
                receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                Log.d(TAG, "waiting for response from server");
                datagramSocket.receive(receivePacket);
                Log.d(TAG, "we have a response");
                String msg = new String(receivePacket.getData()).trim();
                if (msg.startsWith("DISCOVER_RKMS_RESPONSE")) {
                    Log.d(TAG, "from " + receivePacket.getAddress().getHostName());
                } else {
                    Log.d(TAG, "some other packet ");
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "exception in receving packets" + ex.getLocalizedMessage());
        } finally {
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
