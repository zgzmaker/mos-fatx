package core;

import main.java.ByteUtil;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * DirectoryEntry
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class DirectoryEntry {

    private byte[] data;

    private String filename;

    private String filenameExtension;

    private char attribute;

    private String createTime;

    /**
     * 上次访问日期
     */
    private String LastAccessDate;

    /**
     * 最后修改时间
     */
    private String modifyTimeStamp;

    /**
     *
     */
    private int startingCluster;

    /**
     * 文件大小
     */
    private int fileSize;


    public DirectoryEntry(byte[] data) {
        this.data = data;

        byte[] tmp = Arrays.copyOfRange(data, 0, 8);
        this.filename = new String(tmp, StandardCharsets.UTF_8);

        this.filenameExtension = new String(Arrays.copyOfRange(data, 8, 11));
        this.attribute = (char) data[11];
    }

    public int getStartingCluster() {
        return ByteUtil.byte2Short(Arrays.copyOfRange(data, 26, 28));
    }

    public String getFormatDisplay() {

        long fileSize = ByteUtil.byte2Int(Arrays.copyOfRange(this.data, 28, 32));
        Short lastAccessTime = ByteUtil.byte2Short(Arrays.copyOfRange(this.data, 16, 18));

        long lastAccessTimestamp = lastAccessTime * 24 * 60 * 60L * 1000;
        String lastAccessDay = new SimpleDateFormat("yyyy/MM/dd").format(new Date(lastAccessTimestamp));

        this.filenameExtension = new String(Arrays.copyOfRange(data, 8, 11));
        String filename = new String(Arrays.copyOfRange(data, 0, 8), StandardCharsets.UTF_8);
        if (null != filenameExtension && 0 < filenameExtension.length()) {
            filename = filename + "." + filenameExtension;
        }
        return String.format("%d\t%s\t%s", fileSize, lastAccessDay, filename);
    }

    public static void main(String arg[]) throws FileNotFoundException {
        byte[] tmp = new byte[32];
        tmp[0] = 0x61;
        tmp[1] = 0x62;
        tmp[2] = 0x62;
        tmp[3] = 0x62;
        tmp[4] = 0x61;
        tmp[5] = 0x62;
        tmp[6] = 0x62;
        tmp[7] = 0x62;
        tmp[8] = 0x61;
        tmp[9] = 0x62;
        tmp[10] = 0x62;
        tmp[11] = 0x62;
        tmp[12] = 0x61;
        tmp[13] = 0x62;
        tmp[14] = 0x62;
        tmp[15] = 0x62;

        tmp[16] = 0x61;
        tmp[17] = 0x62;
        tmp[18] = 0x62;
        tmp[19] = 0x62;
        tmp[20] = 0x61;
        tmp[21] = 0x62;
        tmp[22] = 0x62;
        tmp[23] = 0x62;
        tmp[24] = 0x61;
        tmp[25] = 0x62;
        tmp[26] = 0x62;
        tmp[27] = 0x62;
        tmp[28] = 0x61;
        tmp[29] = 0x62;
        tmp[30] = 0x62;
        tmp[31] = 0x62;

        ByteBuffer buffer = ByteBuffer.wrap(tmp);
        int i = buffer.getInt();


        DirectoryEntry entry = new DirectoryEntry(tmp);
        String formatDisplay = entry.getFormatDisplay();


        System.out.println(formatDisplay);
    }
}
