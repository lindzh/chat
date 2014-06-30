package lin.xidian.core;

public interface Sender
{
	public void setWrapper(Wrapper wrapper);
	public void addSendEventListener(SendEventListener listener);
	public void addFinishListener(FinishListener listener);
	public void send(String type,byte[] data);
	public void init();
	public void startTick();
	public void stop();
	
}
