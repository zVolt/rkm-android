package io.github.zkhan93.lanmak.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zeeshan Khan on 11/8/2016.
 */

public class Host implements Parcelable {
    private String name;
    private String ip;
    private int port;

    public Host() {
    }

    public Host(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public Host(Parcel parcel) {
        name = parcel.readString();
        ip = parcel.readString();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ip);
    }

    public static final Creator<Host> CREATOR = new Creator<Host>() {
        @Override
        public Host createFromParcel(Parcel source) {
            return new Host(source);
        }

        @Override
        public Host[] newArray(int size) {
            return new Host[0];
        }
    };
}
