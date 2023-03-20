package main.java;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ByteUtil
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class ByteUtil {

    /**
     * byte 数组转 int
     * @param data
     * @return
     */
    public static int byte2Int(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt();
    }

    public static Short byte2Short(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getShort();
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort(x);
        return buffer.array();
    }

    public static byte[] dateTimeToBytes(String datetime, String format) {
        Date parse = null;
        try {
            parse = new SimpleDateFormat(format).parse(datetime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("pare timestamp error. source:" + datetime);
        }
        int timestamp = (int) (parse.getTime() / 1000);
        return ByteUtil.intToBytes(timestamp);
    }

    public static byte[] dateToBytes(String date, String format) {
        Date parse = null;
        try {
            parse = new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("pare timestamp error. source:" + date);
        }
        short timestamp = (short) (parse.getTime() / 1000 / 24 / 60 / 60);
        return ByteUtil.shortToBytes(timestamp);
    }


    public static void copy(byte[] source, int sourceOffset, int sourceLen, byte[] destination, int destinationOffset) {
        if (sourceOffset < 0 || sourceLen < 0 || sourceOffset + sourceLen > source.length) {
            throw new IllegalArgumentException("source parameter illegal");
        }
        for(int i = sourceOffset; i < sourceOffset + sourceLen; i++) {
            destination[destinationOffset + i - sourceOffset] = source[i];
        }
    }

    public static void copy(byte[] source, byte[] destination, int destinationOffset) {
        if (null == source) {
            return;
        }
        copy(source, 0, source.length, destination, destinationOffset);
    }

    public static void copy(byte source, byte[] destination, int destinationOffset) {
        destination[destinationOffset] = source;
    }
}
