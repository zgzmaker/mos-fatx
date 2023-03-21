package core;

import main.java.ByteUtil;
import main.java.exception.MosFileSystemException;

import java.util.Arrays;

/**
 * MosFile
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-20
 **/
public class MosFile {

    private int startintCluster;

    private int fileSize;

    public MosFile() {

    }

    public MosFile(int startintCluster, int fileSize) {
        this.startintCluster = startintCluster;
        this.fileSize = fileSize;
    }

    public byte[] read() {
        int clusterId = startintCluster;

        // TODO 文件的大小为 2的32次方，这里用int其实只有 2的31次方。  用ArrayList，最大也是最有Integer.MAX_VALUE.
        byte[] data = new byte[fileSize];
        int curSize = 0;
        while(curSize < fileSize && FileSystem.fileAllocationTable.using(clusterId)) {
            Cluster cluster = new Cluster(FileSystem.disk, clusterId, false);
            byte[] bytes = cluster.readClusterNoCache(fileSize);
            ByteUtil.copy(bytes, data, curSize);

            curSize += bytes.length;
            clusterId = FileSystem.fileAllocationTable.getFatEntryValue(clusterId);
        }
        return data;
    }

    public void append(byte[] data) {
        if (null == data || data.length == 0) {
            return;
        }

        if (this.startintCluster == -1) {
            this.startintCluster = FileSystem.fileAllocationTable.getNextEmptyCluster();
            if (this.startintCluster == -1) {
                throw new MosFileSystemException("disk if full");
            }
            FileSystem.fileAllocationTable.updateClusterValue(startintCluster, FileAllocationTable.EOF);
        }

        int clusterNum = FileSystem.fileAllocationTable.curClusterNum(startintCluster);
        int lastClusterId = FileSystem.fileAllocationTable.lastClusterId(startintCluster);

        Cluster cluster = new Cluster(FileSystem.disk, lastClusterId, false);

        int len = data.length + fileSize;
        int clusterSize = FileSystem.disk.secotrSize() * cluster.sectorNum();
        int needClusterNum = len / clusterSize + len % clusterSize > 0 ? 1 : 0;

        int emptyClusterNum = FileSystem.fileAllocationTable.emptyClusterNum();
        if (emptyClusterNum + clusterNum < needClusterNum) {
            throw new MosFileSystemException("disk is full.");
        }

        /* 写文件 */
        /* 待写入的数据的开始下标 */
        int dataOffset = 0;
        int offset = this.fileSize - (clusterNum - 1) * clusterSize;

        while(dataOffset < data.length) {
            int maxLen = clusterSize - offset > data.length - dataOffset ? data.length - dataOffset : clusterSize - offset;
            cluster.updateDataNoCache(Arrays.copyOfRange(data, dataOffset, maxLen), offset, maxLen);

            dataOffset += maxLen;
            offset = 0;

            /* update fat */
            int nextEmptyCluster = FileSystem.fileAllocationTable.getNextEmptyCluster();
            FileSystem.fileAllocationTable.updateClusterValue(lastClusterId, nextEmptyCluster);
            FileSystem.fileAllocationTable.updateClusterValue(nextEmptyCluster, FileAllocationTable.EOF);

            lastClusterId = nextEmptyCluster;
            cluster = new Cluster(FileSystem.disk, lastClusterId, false);
        }

        this.fileSize += data.length;
    }
}
