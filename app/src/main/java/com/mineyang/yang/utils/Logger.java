package com.mineyang.yang.utils;

import android.text.TextUtils;
import android.util.Log;

public final class Logger {

    public static boolean ENABLE = true;

    private Logger() {
    }

    public static void log(int level, Object tagObj, Object msgObj) {
        if (ENABLE) {

            String tag = obj2tag(tagObj);

            String msg = "null";
            if (msgObj != null) {
                if (msgObj instanceof Throwable) {
                    msg = Log.getStackTraceString((Throwable) msgObj);
                } else {
                    msg = msgObj.toString();
                }
            }

            String msg2 = null;
            if (msg.length() > 3000) {
                msg2 = msg.substring(3000);
                msg = msg.substring(0, 3000);
            }
            switch (level) {
                case Log.ERROR:
                    Log.e(tag, msg);
                    break;
                case Log.WARN:
                    Log.w(tag, msg);
                    break;
                case Log.INFO:
                    Log.i(tag, msg);
                    break;
                case Log.DEBUG:
                    Log.d(tag, msg);
                    break;
                case Log.VERBOSE:
                    Log.v(tag, msg);
                    break;
                default:
                    break;
            }

            if (msg2 != null) {
                log(level, tagObj, msg2);
            }
        }
    }

    private static String obj2tag(Object obj) {
        if ((obj instanceof String) || (obj instanceof Number)) {
            return obj.toString();
        } else if (obj instanceof Class) {
            String tag = ((Class) obj).getSimpleName();
            if (TextUtils.isEmpty(tag)) {
                return obj2tag(((Class) obj).getSuperclass());
            } else {
                return tag;
            }
        } else {
            return obj2tag(obj.getClass());
        }
    }

    // ERROR
    public static void e(Object tag, Object msg) {
        log(Log.ERROR, tag, msg);
    }

    public static void e(Object tag, String format, Object... args) {
        e(tag, String.format(format, args));
    }

    // WARNING
    public static void w(Object tag, Object msg) {
        log(Log.WARN, tag, msg);
    }

    public static void w(Object tag, String format, Object... args) {
        w(tag, String.format(format, args));
    }

    // INFO
    public static void i(Object tag, Object msg) {
        log(Log.INFO, tag, msg);
    }

    public static void i(Object tag, String format, Object... args) {
        i(tag, String.format(format, args));
    }

    // DEBUG
    public static void d(Object tag, Object msg) {
        log(Log.DEBUG, tag, msg);
    }

    public static void d(Object tag, String format, Object... args) {
        d(tag, String.format(format, args));
    }

    // VERBOSE
    public static void v(Object tag, Object msg) {
        log(Log.VERBOSE, tag, msg);
    }

    public static void v(Object tag, String format, Object... args) {
        v(tag, String.format(format, args));
    }

    //TRACK
    public static void track() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        Logger.i("TRACK", elements.length >= 4 ? elements[3] : null);
    }

}
