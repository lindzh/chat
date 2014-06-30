package lin.xidian.core;

public interface SendFileListener//收到sendFile:时事件，表明对方要给自己发送文件
{
	public void sendFileEvent(long id,String name,long size,int port);
}
