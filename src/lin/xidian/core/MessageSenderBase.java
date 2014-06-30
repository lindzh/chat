package lin.xidian.core;

public abstract class MessageSenderBase implements Sender,Runnable
{
	public abstract void sendMessage(String message);
	public abstract void  setDestPort(int port);//对方接收重定向，只设置端口
	//添加发送控制信息
}
