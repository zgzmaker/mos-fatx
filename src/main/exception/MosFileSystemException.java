package main.exception;

/**
 * DiskException
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-15
 **/
public class MosFileSystemException extends RuntimeException{

    /**
     * 错误信息
     */
    private String message;

    public MosFileSystemException(String message) {
        super();
        this.message = message;
    }


}
