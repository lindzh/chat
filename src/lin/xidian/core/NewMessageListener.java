package lin.xidian.core;

public interface NewMessageListener
{
	public void newMessage(String type,long num,String ip,int port,String info);
}
