package lin.xidian.core;

public interface ReciveFileListener//表明我发送此文件对方是否同意
{
	public void reciveEvent(long id,String state,int port);
}
