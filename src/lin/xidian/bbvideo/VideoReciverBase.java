package lin.xidian.bbvideo;

import lin.xidian.core.FinishListener;
import lin.xidian.core.RecieveEventListener;
import lin.xidian.core.Reciver;
import lin.xidian.core.TickListener;

public abstract class VideoReciverBase implements Reciver
{

	public abstract void addVideoEventListener(VideoEventListener listener);
	
	public void addFinishListener(FinishListener listener)
	{
		
	}

	public void addRecieveEventListener(RecieveEventListener listener)
	{
		
	}

	public void addTickListener(TickListener listener)
	{
		
	}

	public void run()
	{
		
	}

	
	
}
