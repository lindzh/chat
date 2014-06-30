package lin.xidian.audio;

import lin.xidian.core.FinishListener;
import lin.xidian.core.RecieveEventListener;
import lin.xidian.core.Reciver;
import lin.xidian.core.TickListener;

public abstract class RTPAudioReciverBase implements Reciver
{

	public abstract void addAudioEventListener(AudioEventListener listener);
	
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
