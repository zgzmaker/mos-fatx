package core;

import main.java.ByteUtil;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * DirectoryEntry
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class DirectoryEntry {

    private int id;

    private Pattern fileExtPattern = Pattern.compile("[0-9, A-Z, #, $, %, &, ', (, ), -, @]+");

    private byte[] data;

    private String filename;

    private String filenameExtension;

    private boolean readOnly = false;

    private boolean hidden = false;

    private boolean forSystem = false;

    private boolean volumeName = false;

    private boolean directory = false;

    private boolean achieveFlag = false;

    private byte reservedForWindows = 0x00;

    private byte creation = 0x00;

    private String createTime;

    /**
     * 上次访问日期
     */
    private String lastAccessDate;

    private byte[] revervedForFat32 = new byte[2];

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
    private long fileSize;


    public DirectoryEntry(byte[] data, int id) {
        this.id = id;
        this.data = data;

        byte[] tmp = Arrays.copyOfRange(data, 0, 8);
        this.filename = new String(tmp, StandardCharsets.UTF_8);

        this.filenameExtension = new String(Arrays.copyOfRange(data, 8, 11));

    }

    public DirectoryEntry(String filename, boolean directory, int startingCluster, long fileSize, int id) {
        this.id = id;
        int index = filename.lastIndexOf(".");
        if (index < 0) {
            this.filenameExtension = "";
            this.filename = filename;
        } else {
            this.filenameExtension = filename.substring(index + 1);
            this.filename = filename.substring(0, index);
        }
        if (this.filename.length() > 8 || this.filenameExtension.length() > 3
                || !fileExtPattern.matcher(this.filename).matches()
                || !fileExtPattern.matcher(this.filenameExtension).matches()) {
            throw new IllegalArgumentException("filename is illegal. source:" + filename);
        }

        this.directory = directory;
        Date date = new Date();
        this.createTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        this.lastAccessDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
        this.modifyTimeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        this.startingCluster = startingCluster;
        this.fileSize = fileSize;

        // TODO  对于direction entry 是用byte[] 存储，还是用各个变量来存储？
        this.data = toBytes();
    }

    public String getFileName() {
        String filenameExtension = new String(Arrays.copyOfRange(data, 8, 11));
        String filename = new String(Arrays.copyOfRange(data, 0, 8), StandardCharsets.UTF_8);
        if (null != filenameExtension && 0 < filenameExtension.length()) {
            filename = filename + "." + filenameExtension;
        }
        return filename;
    }

    public int getStartingCluster() {
        return ByteUtil.byte2Short(Arrays.copyOfRange(data, 26, 28));
    }

    public String getFormatDisplay() {

        long fileSize = ByteUtil.byte2Int(Arrays.copyOfRange(this.data, 28, 32));
        Short lastAccessTime = ByteUtil.byte2Short(Arrays.copyOfRange(this.data, 18, 20));

        long lastAccessTimestamp = lastAccessTime * 24 * 60 * 60L * 1000;
        String lastAccessDay = new SimpleDateFormat("yyyy/MM/dd").format(new Date(lastAccessTimestamp));

        this.filenameExtension = new String(Arrays.copyOfRange(data, 8, 11));
        String filename = new String(Arrays.copyOfRange(data, 0, 8), StandardCharsets.UTF_8);
        if (null != filenameExtension && 0 < filenameExtension.length()) {
            filename = filename + "." + filenameExtension;
        }
        return String.format("%d\t%s\t%s", fileSize, lastAccessDay, filename);
    }

    public int getFileSize() {
        return ByteUtil.byte2Int(Arrays.copyOfRange(this.data, 28, 32));
    }

    public byte[] toBytes() {
        byte[] result = new byte[32];
        ByteUtil.copy(this.filename.getBytes(), result, 0);
        ByteUtil.copy(this.filenameExtension.getBytes(), result, 8);
        ByteUtil.copy(buildAttribute(), result, 11);
        ByteUtil.copy(this.reservedForWindows, result, 12);
        ByteUtil.copy(this.creation, result, 13);
        ByteUtil.copy(ByteUtil.dateTimeToBytes(this.createTime, "yyyy/MM/dd HH:mm:ss"), 0, 4, result, 14);
        ByteUtil.copy(ByteUtil.dateToBytes(this.lastAccessDate, "yyyy/MM/dd"), 0, 2, result, 18);
        ByteUtil.copy(this.revervedForFat32, result, 20);
        ByteUtil.copy(ByteUtil.dateTimeToBytes(this.modifyTimeStamp, "yyyy/MM/dd HH:mm:ss"), result, 22);
        ByteUtil.copy(ByteUtil.intToBytes(this.startingCluster), result, 26);
        ByteUtil.copy(ByteUtil.longToBytes(this.fileSize), 4, 4, result, 28);

        return result;
    }

    private byte[] buildAttribute() {
        int attribute = 0x00;
        attribute = this.readOnly ? (attribute | 0x01) : (attribute & 0xfe);
        attribute = this.hidden ? (attribute | 0x02) : (attribute & 0xfd);
        attribute = this.forSystem ? (attribute | 0x04) : (attribute & 0xfb);
        attribute = this.volumeName ? (attribute | 0x08) : (attribute & 0xf7);
        attribute = this.directory ? (attribute | 0x10) : (attribute & 0xef);
        attribute = this.achieveFlag ? (attribute | 0x20) : (attribute & 0xdf);

        byte[] result = new byte[1];
        result[0] = (byte) attribute;

        return result;
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

//        ByteBuffer buffer = ByteBuffer.wrap(tmp);
//        int i = buffer.getInt();



    }

    public void incrFileSize(int length) {
        this.fileSize = getFileSize() + length;
        ByteUtil.copy(ByteUtil.longToBytes(this.fileSize), 4, 4, this.data, 28);
    }

    public int getId() {
        return id;
    }
}
