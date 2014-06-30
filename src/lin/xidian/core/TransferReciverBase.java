package lin.xidian.core;

public abstract class TransferReciverBase implements Reciver
{
	public abstract void reciveFile(TransFile file);
	public abstract void removeFinishListeners();
	public abstract void removeReciveEventListeners();
}
