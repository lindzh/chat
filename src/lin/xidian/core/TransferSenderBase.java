package lin.xidian.core;

public abstract class TransferSenderBase implements Sender
{
	public abstract void setDestPort(int port);
	public abstract void sendFile(TransFile file);
	public abstract void removeFinishListeners();
	public abstract void removeSendEventListeners();
}
