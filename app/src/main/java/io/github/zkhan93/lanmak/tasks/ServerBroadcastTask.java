package io.github.zkhan93.lanmak.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * It sends a broadcast packet({@link DatagramPacket}) to every NetworkInterface's broadcast address.
 * Created by Zeeshan Khan on 11/6/2016.
 */

public class ServerBroadcastTask extends AsyncTask<Void, Void, Void> {
    public static String TAG = ServerBroadcastTask.class.getSimpleName();
    private DatagramSocket datagramSocket;
    private byte[] sendData = "DISCOVER_RKMS_REQUEST".getBytes();

    public ServerBroadcastTask(@NonNull DatagramSocket socket) {
        datagramSocket = socket;
        try {
            datagramSocket.setBroadcast(true);
        } catch (SocketException ex) {
            Log.d(TAG, "" + ex.getLocalizedMessage());
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            InetAddress broadcast;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    send(broadcast);
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "error " + ex.getLocalizedMessage());
        }
        return null;
    }

    private void send(InetAddress address) {
        if (address == null)
            return;
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 2222);//on
            if (!datagramSocket.isClosed()) {
                datagramSocket.send(sendPacket);
                Log.d(TAG, "Request packet sent to: " + sendPacket.getAddress() + ":" + sendPacket.getPort());
            }else{
                Log.d(TAG, "Request packet not sent socket is closed");
            }
        } catch (Exception ex) {
            Log.d(TAG, "unable to send packet on broadcast address" + ex.getLocalizedMessage());
        }
    }
}
