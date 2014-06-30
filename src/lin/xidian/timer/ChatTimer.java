package lin.xidian.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatTimer
{
	private static final ChatTimer chatTimer = new ChatTimer();
	
	private ChatTimer()
	{
		
	}
	
	private Timer timer= new Timer("seconds");
	
	public ChatTimer getInstance()
	{
		return chatTimer;
	}
	
	public void addTimerListener(TimerListener listener)
	{
		listeners.add(listener);
	}

	private List<TimerListener> listeners = new ArrayList<TimerListener>();
	
	public void start()
	{
		timer.schedule(new TimerTask(){

			@Override
			public void run()
			{
				for(TimerListener listener:listeners)
				{
					listener.onTime(1);
				}
			}
		}, 1000,1000);
	}
	
	public void stop()
	{
		timer.cancel();
	}
}
