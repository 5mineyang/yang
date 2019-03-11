package com.mineyang.yang.utils;

import android.os.SystemClock;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangsx on 16/1/18.
 */
public class NetTimeManager {
    public static final long DayOf2016;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 1, 1);
        DayOf2016 = calendar.getTimeInMillis();
    }

    private static NetTimeManager instance = new NetTimeManager();

    private long netTime = -1;
    private long syncBootTime = -1;

    public static NetTimeManager getInstance() {
        return instance;
    }

    public void syncTime() {
        new Thread() {
            @Override
            public void run() {
                try {
                    SystemClock.elapsedRealtime();
                    URLConnection conn = new URL("http://www.baidu.com").openConnection();
                    conn.setConnectTimeout(3000);
                    conn.connect();
                    if (conn.getDate() >= DayOf2016) {
                        netTime = conn.getDate();
                        syncBootTime = SystemClock.elapsedRealtime();
                        Logger.e(this, "net time:" + netTime + " boot time:" + syncBootTime);
                        Logger.e(this, "local time:" + new Date().getTime());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public NetTimeManager setNetTime(long netTime) {
        this.netTime = netTime;
        return this;
    }

    public NetTimeManager setSyncBootTime(long syncBootTime) {
        this.syncBootTime = syncBootTime;
        return this;
    }

    public long getCurNetTime() {
        if (netTime == -1) {
            syncTime();
            return -1;
        } else {
            return netTime + SystemClock.elapsedRealtime() - syncBootTime;
        }
    }
}
