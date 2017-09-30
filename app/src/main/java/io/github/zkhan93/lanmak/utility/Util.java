package io.github.zkhan93.lanmak.utility;

import android.content.Context;
import android.os.Build;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public class Util {
    public static String TAG = Util.class.getSimpleName();

    public static Boolean isIPv4Address(String address) {
        if (address.isEmpty()) {
            return false;
        }
        try {
            Object res = InetAddress.getByName(address);
            return res instanceof Inet4Address || res instanceof Inet6Address;
        } catch (final UnknownHostException ex) {
            return false;
        }
    }

    public static boolean isValidPort(String port) {
        if (port == null || port.isEmpty())
            return false;
        try {
            int intPort = Integer.parseInt(port);
            if (intPort < 65536)
                return true;
            else
                return false;
        } catch (NumberFormatException ex) {
            return false;
        }
    }


    public static int getCameraId(Context context) {
        int cameraId = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            CameraManager cameraManager = (CameraManager) context.getSystemService(Context
// .CAMERA_SERVICE);
//            String[] cameraIds = cameraManager.getCameraIdList();
//
//            for (String id : cameraIds) {
//
//                if (info.facing == camera2.CameraInfo.CAMERA_FACING_BACK) {
//                    cameraId = i;
//                    break;
//                }
//            }
            return cameraId;

        } else {
            return cameraId;
        }
    }

}
