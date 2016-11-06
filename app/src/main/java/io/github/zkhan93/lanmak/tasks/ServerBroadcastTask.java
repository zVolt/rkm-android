package io.github.zkhan93.lanmak.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * It sends a broadcast packet({@link DatagramPacket}) to every NetworkInterface's broadcast address.
 * Created by Zeeshan Khan on 11/6/2016.
 */

public class ServerBroadcastTask extends AsyncTask<Void, Void, Void> {
    public static String TAG = ServerBroadcastTask.class.getSimpleName();
    private DatagramSocket datagramSocket;

    public ServerBroadcastTask(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            this.datagramSocket.setBroadcast(true);
            byte[] sendData = "DISCOVER_RKMS_REQUEST".getBytes();

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6835);
                        datagramSocket.send(sendPacket);
                    } catch (Exception ex) {
                        Log.d(TAG, "unable to send packet on broadcast address" + ex.getLocalizedMessage());
                    }
                    Log.d(TAG, "Request packet sent to: " + broadcast.getHostAddress() + "; " +
                            "Interface: " + networkInterface.getDisplayName());
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "error " + ex.getLocalizedMessage());
        }
        return null;
    }
}
