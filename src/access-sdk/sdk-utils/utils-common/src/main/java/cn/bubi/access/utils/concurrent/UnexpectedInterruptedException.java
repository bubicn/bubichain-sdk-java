package cn.bubi.access.utils.concurrent;

/**
 * 未预期的中断异常；
 *
 * @author haiq
 */
public class UnexpectedInterruptedException extends RuntimeException{

    private static final long serialVersionUID = -4404758941888371638L;

    public UnexpectedInterruptedException(){
    }

    public UnexpectedInterruptedException(String message){
        super(message);
    }

    public UnexpectedInterruptedException(String message, Throwable cause){
        super(message, cause);
    }

}
