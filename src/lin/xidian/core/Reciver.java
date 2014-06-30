package lin.xidian.core;

public interface Reciver extends Runnable
{
	public void setWrapper(Wrapper wrapper);
	public void addRecieveEventListener(RecieveEventListener listener);
	public void addFinishListener(FinishListener listener);
	public void addTickListener(TickListener listener);
	public void init();
	public void start();
	public void stop();
	
	
}
