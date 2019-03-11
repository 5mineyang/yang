package com.mineyang.yang.utils;

/**
 * Created by huangsx on 15/10/14.
 */
public class ByteUtils {
    /**
     * 短整型与字节的转换
     *
     * @param number 短整型
     * @return 两位的字节数组
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = Integer.valueOf(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    /**
     * 字节的转换与短整型
     *
     * @param b 两位的字节数组
     * @return 短整型
     */
    public static short byteToShort(byte[] b) {
        short s;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * 整型与字节数组的转换
     *
     * @param i 整型
     * @return 四位的字节数组
     */
    public static byte[] intToByte(int i) {
        byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    /**
     * 字节数组和整型的转换
     *
     * @param bytes 字节数组
     * @return 整型
     */
    public static int bytesToInt(byte[] bytes) {
        int num = bytes[0] & 0xFF;
        num |= ((bytes[1] << 8) & 0xFF00);
        num |= ((bytes[2] << 16) & 0xFF0000);
        num |= ((bytes[3] << 24) & 0xFF000000);
        return num;
    }

    /**
     * 字节数组和长整型的转换
     *
     * @param number 字节数组
     * @return 长整型
     */
    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp & 0xff).byteValue();
            // 将最低位保存在最低位
            temp = temp >> 8;
            // 向右移8位
        }
        return b;
    }

    /**
     * 字节数组和长整型的转换
     *
     * @param b 字节数组
     * @return 长整型
     */
    public static long byteToLong(byte[] b) {
        long s;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff; // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }


    /**
     * byte转二进制数组
     *
     * @param b byte
     * @return byte[]
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * byte 转二进制字符串
     *
     * @param b byte
     * @return 01010101
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * byte数组拼接
     *
     * @param byte_1 byte[]
     * @param byte_2 byte[]
     * @return byte[]
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * byte数组 间隔提取
     *
     * @param bytes byte[]
     * @param per   提起间隔
     * @return byte[]
     */
    public static byte[] byteExtract(byte[] bytes, int per) {
        int count = bytes.length / per;
        if (count >= 1) {
            byte[] r = new byte[count];
            for (int i = 0; i < count; i++) {
                r[i] = bytes[i * per];
            }
            return r;
        }
        return bytes;
    }

    /**
     * C语言2字节 转Int(高地位问题)
     *
     * @param bytes byte[]
     * @return int
     */
    public static int bytesToIntC(byte[] bytes) {
        return ((0xff & bytes[1]) << 8) + (bytes[0] & 0xff);
    }

    /**
     * C语言4字节 转Int(高地位问题)
     *
     * @param bytes byte[]
     * @return int
     */
    public static int Bytes4ToIntC(byte[] bytes) {
        return (0xff & bytes[3]) << 24 | (0xff & bytes[2]) << 16 | (0xff & bytes[1]) << 8 | 0xff & bytes[0];
    }


    /**
     * BCD码转为10进制串(阿拉伯数据)
     *
     * @param bytes BCD码
     * @return 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            temp.append((byte) ((aByte & 0xf0) >>> 4));
            temp.append((byte) (aByte & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * 10进制串转为BCD码
     *
     * @param asc 10进制串
     * @return BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 16进制串转byte数组
     *
     * @param src 16进制串
     * @return byte数组
     */
    public static byte[] hex2Bytes(String src) {
        byte[] res = new byte[src.length() / 2];
        char[] chs = src.toCharArray();
        for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
            res[c] = (byte) (Integer.parseInt(new String(chs, i, 2), 16));
        }

        return res;
    }

    /**
     * byte数组转16进制串
     *
     * @param array     byte数组
     * @param prefix    前缀 例如:0x
     * @param separator 分隔符
     * @return 16进制串
     */
    public static String bytes2HexString(byte[] array, String prefix, String separator) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        for (byte anArray : array) {
            sb.append(prefix);
            int v = anArray & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
            sb.append(separator);
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(']');
        return sb.toString();
    }

    /**
     * byte数组转16进制串 空格符分割
     *
     * @param array byte数组
     * @return 16进制串
     */
    public static String bytes2HexString(byte[] array) {
        return bytes2HexString(array, "", " ");
    }


    /**
     * 搜索字节组
     *
     * @param data 源
     * @param key  关键字节组
     * @return index （未找到为-1）
     */
    public static int searchBytes(byte[] data, byte[] key) {
        if (data == null) {
            return -1;
        }
        if (key == null || key.length == 0) {
            return 0;
        }
        if (data.length < key.length) {
            return -1;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == key[0]) {
                for (int j = 1; j < key.length; j++) {
                    if (data[i + j] != key[j]) {
                        break;
                    }
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * 切割字节数组
     *
     * @param data       源
     * @param splitIndex 分隔点 (第一节的长度)
     * @return byte[2][]
     */
    public static byte[][] splitBytes(byte[] data, int splitIndex) {
        if (splitIndex < 0 || splitIndex > data.length) {
            throw new IndexOutOfBoundsException("data length:" + data.length + " split index:" + splitIndex);
        }

        byte[][] result = new byte[2][];
        if (splitIndex == 0) {
            result[0] = new byte[]{};
            result[1] = data;
        }

        if (splitIndex == data.length) {
            result[0] = data;
            result[1] = new byte[]{};
        }

        byte[] temp1 = new byte[splitIndex];
        byte[] temp2 = new byte[data.length - splitIndex];
        System.arraycopy(data, 0, temp1, 0, temp1.length);
        System.arraycopy(data, temp1.length, temp2, 0, temp2.length);
        result[0] = temp1;
        result[1] = temp2;

        return result;
    }

    /**
     * 校验和
     *
     * @param data 源
     * @return 和
     */
    public static int checkSum(byte[] data) {
        int count = 0;
        for (byte b : data) {
            count += b & 0xFF;
        }
        return count & 0xFFFF;
    }

    /**
     * byte数组转short数组
     *
     * @param data 字节数组
     * @return short数组
     */
    public static short[] byteArray2ShortArray(byte[] data) {
        int count = data.length / 2;
        short[] retVal = new short[count];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }
}
