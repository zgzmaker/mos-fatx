package main.core;

import main.exception.DiskException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * FileDisk
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-15
 **/
public class FileDisk implements IDisk{
    private static final String FILE_NAME = "mos.disk";
    private FileInputStream fileInputStream;

    @Override
    public byte[] readSector(int sectorIdx) {
        return readSector(sectorIdx, 0, secotrSize());
    }

    @Override
    public byte[] readSector(int sectorIdx, int offset, int maxLen) {
        if (0 < sectorIdx || sectorIdx >= sectorCount()) {
            throw new DiskException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }

        int len = Math.min(secotrSize(), maxLen);
        byte[] result = new byte[len];
        offset = sectorIdx * secotrSize() + offset;
        try {
            fileInputStream = new FileInputStream(this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME);
            fileInputStream.read(result, offset, len);
            return result;
        }catch (IOException e) {
            throw new DiskException("read disk error. reason:" + e.getMessage());
        }finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new DiskException("read disk error. reason:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public void writeSector(int sectorIdx, byte[] sectorData) {
        writeSector(sectorIdx, 0, sectorData);
    }

    @Override
    public void writeSector(int sectorIdx, int offset, byte[] sectorData) {
        if (0 < sectorIdx || sectorIdx >= sectorCount()) {
            throw new DiskException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }
        if (null == sectorData || 0 == sectorData.length) {
            return;
        }
        if (offset + sectorData.length > secotrSize()) {
            throw new DiskException("data too large");
        }
        /* setting offset */
        offset = sectorIdx * secotrSize() + offset;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(
                    this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME, "rw");
            randomAccessFile.seek(offset);
            randomAccessFile.write(sectorData, 0, sectorData.length);
        }catch (IOException e) {
            throw new DiskException("read disk error. reason:" + e.getMessage());
        }finally {
            if (null != randomAccessFile) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    throw new DiskException("read disk error. reason:" + e.getMessage());
                }
            }
        }
    }

    public static void main(String arg[]) throws FileNotFoundException {
        IDisk disk = new FileDisk();
        String str = ";fda";
        byte[] data = str.getBytes();
        disk.writeSector(0, 3, data);
        byte[] bytes = disk.readSector(0, 0,7);
        System.out.println(bytes);
    }
}
