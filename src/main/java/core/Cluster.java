package core;

import main.java.exception.MosFileSystemException;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Cluster
 * File system storage unit
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class Cluster implements ICluster{

    private IDisk disk;

    private int clusterId;

    private byte[] data;

    private int dataLen;

    public Cluster(IDisk disk, int clusterId, boolean loadData) {
        this.disk = disk;
        this.clusterId = clusterId;
        this.data = new byte[sectorNum() * disk.secotrSize()];
        if (loadData) {
            readCluster();
        }
        this.dataLen = this.data.length;
    }

    public int getDataLen() {
        return this.dataLen;
    }

    @Override
    public byte[] readCluster() {
        if (0 < clusterId || clusterId >= clusterCount()) {
            throw new MosFileSystemException("illegal sectorIdx. sectorIdx must in [0, " + clusterCount() + ")");
        }

        for (int i = 0; i < sectorNum(); i++) {
            int sectorIdx = clusterId * sectorNum() + i;
            byte[] data = disk.readSector(sectorIdx);
            for(int j = 0 ; j < data.length; j++) {
                this.data[i * disk.secotrSize() + j] = data[j];
            }
        }

        return this.data;
    }

    @Override
    public void writeCluster(byte[] clusterData) {
        if (null == clusterData) {
            throw new IllegalArgumentException("clusterData should not be empty");
        }
        this.data = clusterData;
        updateData(clusterData, 0, clusterData.length);
    }

    @Override
    public void updateData(byte[] newData, int offset, int len) {
        if (null == newData || 0 == newData.length || 0 >= len) {
            return;
        }

        if (offset + len > disk.secotrSize() * sectorNum()) {
            throw new IllegalArgumentException("data too large");
        }

        /* update data in memory*/
        for(int i = offset; i < offset + len; i++) {
            this.data[i] = newData[i - offset];
        }

        /* flush to disk. start to write data to sectors */
        /* 簇内扇区下标 */
        int sectorIndexInCluster = offset / disk.secotrSize();
        int dataOffset = disk.secotrSize() * sectorIndexInCluster;
        while (dataOffset < offset + dataLen && sectorIndexInCluster < sectorNum()) {
            int sectorIdx = clusterId * sectorNum() + sectorIndexInCluster;
            disk.writeSector(sectorIdx, Arrays.copyOfRange(this.data, dataOffset, dataOffset + disk.secotrSize()));

            dataOffset += disk.secotrSize();
            sectorIndexInCluster++;
        }
    }

    public byte[] getData(int offset, int len) {
        if (null == this.data) {
            readCluster();
        }
        if (0 > offset || offset + len > this.data.length) {
            throw new IllegalArgumentException("illegal parameter.");
        }
        return Arrays.copyOfRange(this.data, offset, offset + len);
    }

    public static void main(String arg[]) throws FileNotFoundException {
        IDisk disk = new FileDisk();
        ICluster cluster = new Cluster(disk, 0, false);
        byte[] data = new byte[612];
        for (int i = 0; i < 612; i++) {
            data[i] = 'c';
        }
        cluster.updateData(data, 3, 600);
        System.out.println(data);
    }
}
