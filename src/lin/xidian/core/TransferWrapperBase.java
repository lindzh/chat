package lin.xidian.core;

public abstract class TransferWrapperBase implements Wrapper
{
	public abstract void addSendFile(long id,TransFile transFile);
	public abstract void addReciveFile(long id,TransFile transFile);
	public abstract void reciveFile(long id);
	public abstract void sendFile(long id);
	public abstract void cancelSend(long id);
	public abstract void cancelRecive(long id);
	//添加相关接口
	public abstract void removeReciveFinishListeners();
	public abstract void removeReciveEventListeners();
	public abstract void addRecieveEventListener(RecieveEventListener listener);
	public abstract void addReciveFinishListener(FinishListener listener);
	public abstract void addSendEventListener(SendEventListener listener);
	public abstract void addSendFinishListener(FinishListener listener);
	public abstract void removeSendFinishListeners();
	public abstract void removeSendEventListeners();
}
