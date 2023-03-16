package main.core;

import main.exception.MosFileSystemException;

import java.io.FileInputStream;
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
        if (0 > sectorIdx || sectorIdx >= sectorCount()) {
            throw new MosFileSystemException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }


        byte[] result = new byte[secotrSize()];
        int offset = sectorIdx * secotrSize();
        try {
            fileInputStream = new FileInputStream(this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME);
            fileInputStream.read(result, offset, secotrSize());
            return result;
        }catch (IOException e) {
            throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
        }finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public byte[] readSector(int sectorIdx, int offset, int maxLen) {
        if (0 > sectorIdx || sectorIdx >= sectorCount()) {
            throw new MosFileSystemException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }

        int len = Math.min(secotrSize(), maxLen);
        byte[] result = new byte[len];
        offset = sectorIdx * secotrSize() + offset;
        try {
            fileInputStream = new FileInputStream(this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME);
            fileInputStream.read(result, offset, len);
            return result;
        }catch (IOException e) {
            throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
        }finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public void writeSector(int sectorIdx, byte[] sectorData) {
        if (0 > sectorIdx || sectorIdx >= sectorCount()) {
            throw new MosFileSystemException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }
        if (null == sectorData || 0 == sectorData.length) {
            return;
        }
        if (sectorData.length > secotrSize()) {
            throw new MosFileSystemException("data too large");
        }
        /* setting offset */
        int offset = sectorIdx * secotrSize();
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(
                    this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME, "rw");
            randomAccessFile.seek(offset);
            randomAccessFile.write(sectorData, 0, sectorData.length);
        }catch (IOException e) {
            throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
        }finally {
            if (null != randomAccessFile) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public void writeSector(int sectorIdx, int sectorOffset, byte[] sectorData, int dataOffset, int len) {
        if (0 > sectorIdx || sectorIdx >= sectorCount()) {
            throw new MosFileSystemException("illegal sectorIdx. sectorIdx must in [0, " + sectorCount() + ")");
        }
        if (null == sectorData || 0 == sectorData.length) {
            return;
        }
        if (len < 0) {
            len = sectorData.length - dataOffset;
        }
        if (sectorOffset + len > secotrSize()) {
            throw new MosFileSystemException("data too large");
        }
        /* setting offset */
        sectorOffset = sectorIdx * secotrSize() + sectorOffset;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(
                    this.getClass().getClassLoader().getResource("").getPath() + FILE_NAME, "rw");
            randomAccessFile.seek(sectorOffset);
            randomAccessFile.write(sectorData, dataOffset, len);
        }catch (IOException e) {
            throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
        }finally {
            if (null != randomAccessFile) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    throw new MosFileSystemException("read disk error. reason:" + e.getMessage());
                }
            }
        }
    }
}
