package lin.xidian.video;

import java.awt.Component;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.SessionEvent;

import lin.xidian.core.Wrapper;
import net.sf.fmj.media.rtp.RTPSocketAdapter;

public class VideoReciver extends VideoReciverBase implements ReceiveStreamListener, SessionListener,
ControllerListener 
{
	private VideoWrapperBase videoWrapper;
	
	private String selfReciveIp;
	private int selfRecivePort;
	private String destSendIp;
	private int destSendPort;
	
	private RTPManager manager;
	private Object dataSync = new Object();
	private boolean dataReceived = false;
	private Player player;

	private boolean startOK = true;
	
	private List<VideoEventListener> list = new ArrayList<VideoEventListener>();
	
	public void init()
	{
		selfReciveIp = videoWrapper.getSelfReciveIp();
		selfRecivePort = videoWrapper.getSelfRecivePort();
		destSendIp = videoWrapper.getDestSendIp();
		destSendPort = videoWrapper.getDestSendPort();
	}

	public void setWrapper(Wrapper wrapper)
	{
		videoWrapper = (VideoWrapperBase)wrapper;
	}

	public void start()
	{
		new Thread(this).start();
	}
	
	

	@Override
	public void run()
	{
		try
		{
			manager = RTPManager.newInstance();
			manager.addReceiveStreamListener(this);
			manager.addSessionListener(this);
			SessionAddress localAddress = new SessionAddress(InetAddress.getByName(selfReciveIp),selfRecivePort);
			SessionAddress destAddress = new SessionAddress(InetAddress.getByName(destSendIp),destSendPort);
			
			RTPSocketAdapter adapter =new RTPSocketAdapter(InetAddress.getByName(selfReciveIp),selfRecivePort,1);
			manager.initialize(adapter);
		//	managers[i].initialize(localAddress);
			BufferControl bc = (BufferControl)manager.getControl("javax.media.control.BufferControl");
			bc.setBufferLength(350);
		//	managers[i].addTarget(destAddress);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/*
		try
		{
			synchronized(dataSync)
			{
				while(!dataReceived)
				{
					dataSync.wait(1000);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
		VideoStartEvent ev = new VideoStartEvent("Recive state:",startOK);
		fireVideoEventListeners(ev);
	}

	public void stop()
	{
		player.stop();
		player.close();
		player.removeControllerListener(this);
		player = null;
		
        manager.removeTargets( "Session ended.");
        manager.dispose();
	}

	public void update(ReceiveStreamEvent evt)
	{
		ReceiveStream stream = evt.getReceiveStream();
		if ((stream!=null) && (evt instanceof NewReceiveStreamEvent))
		{
			try {
				stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
				final DataSource dataSource = stream.getDataSource();
				new Thread()
				{
					public void run()
					{
						try 
						{
							player = Manager.createPlayer(dataSource);
							player.addControllerListener(VideoReciver.this);
							player.realize();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				
				/*
				synchronized (dataSync)
				{
					dataReceived = true;
					dataSync.notifyAll();
				}
				*/
			} catch (Exception e)
			{
				System.err.println("NewReceiveStreamEvent exception "+ e.getMessage());
				return;
			}
		}
		else if (evt instanceof ByeEvent)
		{
			System.err.println("BYE");
		}
	}

	public void update(SessionEvent se)
	{
		if (se instanceof NewParticipantEvent) 
		{
			Participant p = ((NewParticipantEvent) se).getParticipant();
			System.err.println("  - A new participant had just joined: " + p);
		}
	}

	public void controllerUpdate(ControllerEvent ce)
	{
		if(ce instanceof RealizeCompleteEvent)
		{
			Component cp = player.getVisualComponent();
			if(cp != null)
			{
				VideoVisualEvent event = new VideoVisualEvent(cp);
				fireVideoEventListeners(event);
			}
		}
		player.start();
	}

	@Override
	public void addVideoEventListener(VideoEventListener listener)
	{
		list.add(listener);
	}
	
	private void fireVideoEventListeners(VideoEvent videoEvent)
	{
		int len = list.size();
		for(int i=0;i<len;i++)
		{
			list.get(i).videoUpdate(videoEvent, "recive");
		}
	}
}
