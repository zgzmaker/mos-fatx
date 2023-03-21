package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Directory
 *
 * 目录
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class Directory extends MosFile {

    public static final int DIRECTORY_ENTRY_BYTE_SIZE = 32;
    private static final int ENTRY_NUM_PER_CLUSTER = FileSystem.disk.secotrSize() * FileSystem.disk.sectorCount() / DIRECTORY_ENTRY_BYTE_SIZE;

    /**
     * 目录下的条目
     */
    private List<DirectoryEntry> entries;

    private List<Integer> parentStartingClusters;

    private int startingCluster;

    public Directory(byte[] data, int clusterId, List<Integer> parentStartingClusters) {
        /* data to entries */
        if (null == data || 0 == data.length) {
            entries = new ArrayList<>();
            return;
        }

        this.startingCluster = clusterId;
        this.parentStartingClusters = parentStartingClusters;

        for(int i = 0; i < data.length; i += DIRECTORY_ENTRY_BYTE_SIZE) {
            DirectoryEntry entry = new DirectoryEntry(
                    Arrays.copyOfRange(data, i, i + DIRECTORY_ENTRY_BYTE_SIZE),
                    i);
            entries.add(entry);
        }
    }

    public Directory(int startingCluster, List<Integer> parentStartingClusters) {
        this.startingCluster = startingCluster;
        this.parentStartingClusters = parentStartingClusters;
        int index = 0;
        while(FileSystem.fileAllocationTable.using(startingCluster)) {
            Cluster cluster = new Cluster(FileSystem.disk, startingCluster, true);
            byte[] data = cluster.getData(0, cluster.getDataLen());
            for (int i = 0; i < cluster.getDataLen(); i += cluster.getDataLen()) {
                DirectoryEntry entry = new DirectoryEntry(
                        Arrays.copyOfRange(data, i, i + DIRECTORY_ENTRY_BYTE_SIZE),
                        index);
                entries.add(entry);
                index++;
            }
            startingCluster = FileSystem.fileAllocationTable.getFatEntryValue(startingCluster);
        }
    }

    public String output() {
        if (null == this.entries || 0 == this.entries.size()) {
            return "";
        }

        StringBuilder sb = new StringBuilder(this.entries.get(0).getFormatDisplay());
        for(int i = 1; i < this.entries.size(); i++) {
            sb.append("\n")
                    .append(this.entries.get(i).getFormatDisplay());
        }
        return sb.toString();
    }

    public DirectoryEntry findEntryByFileName(String fileName) {
        for (DirectoryEntry entry : entries) {
            if (entry.getFileName().equals(fileName)) {
                return entry;
            }
        }
        return null;
    }

    public void incrEntryFileSize(DirectoryEntry entry, int length) {
        entry.incrFileSize(length);

        /* 存盘 */
        Cluster cluster = new Cluster(FileSystem.disk, getClusterIdByEntryId(entry.getId()), false);
        cluster.updateDataNoCache(entry.toBytes(),
                DIRECTORY_ENTRY_BYTE_SIZE * (entry.getId() % ENTRY_NUM_PER_CLUSTER), DIRECTORY_ENTRY_BYTE_SIZE);
    }

    public void addEntry(String filename, boolean directory) {
        int index = entries.size();

        DirectoryEntry entry = new DirectoryEntry(filename, directory, -1, 0, index);
        entries.add(entry);
        /* 写入文件 */
        append(entry.toBytes());
    }

    private int getClusterIdByEntryId(int entryId) {
        int clusterNum = entryId / ENTRY_NUM_PER_CLUSTER;
        int clusterId = startingCluster;
        while (clusterNum > 0) {
            clusterId = FileSystem.fileAllocationTable.getFatEntryValue(clusterId);
            clusterNum--;
        }
        return clusterId;
    }

    public int getParent() {
        if (null == parentStartingClusters || 0 == parentStartingClusters.size()) {
            return -1;
        }
        return parentStartingClusters.get(parentStartingClusters.size()-1);
    }

    public List<Integer> getParents() {
        return parentStartingClusters;
    }
}
