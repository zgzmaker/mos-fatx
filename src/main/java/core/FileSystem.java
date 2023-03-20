package core;

import main.java.exception.MosFileSystemException;

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

    public static IDisk disk;
    public static FileAllocationTable fileAllocationTable;
    private Cluster bootCluster;

    private Directory rootDirectory;

    private Directory currentDirectory;

    private Cluster currentPathCluster;

    /**
     * 当前路径
     */
    private String curPath = "/";


    public FileSystem(IDisk iDisk) {
        disk = iDisk;
        /* init file system */
        /* init root */
        bootCluster = new Cluster(disk,0, true);
        currentPathCluster = new Cluster(disk, 0, true);
        currentDirectory = new Directory(bootCluster.getData(512, 512 * disk.sectorCount() - 512));

        /* init Root Directory */
        byte[] rootDirData = bootCluster.getData(DIR_ENTRY_DATA_OFFSET, DIR_ENTRY_DATA_LENGTH);
        rootDirectory = new Directory(rootDirData);

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
        DirectoryEntry entry = new DirectoryEntry(dirName, true, -1, 0);
        /* write to file */
        currentPathCluster.
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
