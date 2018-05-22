package cn.bubi.access.utils.console;

/**
 * 命令处理器；
 *
 * @author haiq
 */
public interface CommondProcessor{

    public void onEnter(String command, String[] args, CommandConsole console);

}
