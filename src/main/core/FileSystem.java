package main.core;

import java.util.ArrayList;
import java.util.List;

/**
 * FileSystem
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class FileSystem implements IFileSystem{

    private static final int DIR_ENTRY_DATA_OFFSET = 512;
    private static final int DIR_ENTRY_DATA_LENGTH = 512 * 63;

    private IDisk disk;
    private FileAllocationTable fileAllocationTable;
    private Cluster bootCluster;

    /**
     * 当前路径
     */
    private String curPath = "/";


    public FileSystem(IDisk disk) {
        this.disk = disk;
        /* init file system */
        /* init root */
        bootCluster = new Cluster(disk,0, true);

        /* init Root Directory */
        byte[] rootDirData = bootCluster.getData(DIR_ENTRY_DATA_OFFSET, DIR_ENTRY_DATA_LENGTH);



        /* init file Allocation Table */
        List<Cluster> fatClusterList = new ArrayList<>();
        for(int i = 1; i <= 4; i++) {
            Cluster fatCluster = new Cluster(disk, i, true);
            fatClusterList.add(fatCluster);
        }
        fileAllocationTable = new FileAllocationTable(fatClusterList);
    }

    @Override
    public void newDirectory(String dirPath, String dirName) {

    }

    @Override
    public void newFile(String filePath, String fileName) {

    }

    @Override
    public void write2File(String fileName, Byte[] data) {

    }

    @Override
    public Byte[] readFromFile(String fileName) {
        return new Byte[0];
    }

    @Override
    public String currentDirPath() {
        return null;
    }
}
