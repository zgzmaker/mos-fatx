package main.core;

import main.exception.DiskException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private FileOutputStream fileOutputStream;
    public FileDisk() throws FileNotFoundException {

        fileOutputStream = new FileOutputStream(this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME, true);
    }

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
        if (0 < sectorIdx || sectorIdx >= sectorCount()) {
            throw new DiskException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }
        if (null == sectorData || 0 == sectorData.length) {
            return;
        }
        if (sectorData.length > secotrSize()) {
            throw new DiskException("data too large");
        }

        int offset = sectorIdx * secotrSize();
        try {
            fileOutputStream = new FileOutputStream(this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME, true);
            fileOutputStream.write(result, offset, len);
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
    public void writeSector(int sectorIdx, int offset, byte[] sectorData) {

    }

    public static void main(String arg[]) throws FileNotFoundException {
        IDisk disk = new FileDisk();
        byte[] bytes = disk.readSector(0, 2);
        System.out.println(bytes);
    }
}
