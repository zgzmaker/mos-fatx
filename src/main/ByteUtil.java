package main;

import java.nio.ByteBuffer;

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
}
