package lin.xidian.bbvideo;

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
	
	private RTPManager[] managers = new RTPManager[2];
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
		int portIndex = 0;
		for(int i=0;i<2;i++)
		{
			try
			{
				portIndex = 2*i;
				managers[i] = RTPManager.newInstance();
				managers[i].addReceiveStreamListener(this);
				managers[i].addSessionListener(this);
			//	SessionAddress localAddress = new SessionAddress(InetAddress.getByName(selfReciveIps[i]),selfRecivePorts[2*i],InetAddress.getByName(selfReciveIps[i]),selfRecivePorts[2*i+1]);
				SessionAddress localAddress = new SessionAddress(InetAddress.getByName(selfReciveIp),selfRecivePort);
				SessionAddress destAddress = new SessionAddress(InetAddress.getByName(destSendIp),destSendPort);
				
				RTPSocketAdapter adapter =new RTPSocketAdapter(InetAddress.getByName(selfReciveIp),selfRecivePort,1);
				managers[i].initialize(adapter);
				
				
				System.out.println("Reciver  controlPort  dataPort");
				//System.out.println("Local PORT:"+adapter.+"  "+adapter.getDataPort());
			//	System.out.println("Dest PORT:"+destAddress.getControlPort()+" "+destAddress.getDataPort());
				
				
			//	managers[i].initialize(localAddress);
				BufferControl bc = (BufferControl)managers[i].getControl("javax.media.control.BufferControl");
				bc.setBufferLength(350);
				managers[i].addTarget(destAddress);
				
			} catch (Exception e) {
				startOK = false;
				e.printStackTrace();
			}
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
		
        managers[0].removeTargets( "Session ended.");
        managers[0].dispose();
        managers[1].removeTargets( "Session ended.");
        managers[1].dispose();
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
			//进行相关设置
			/*if(cp)
			cp.setBounds(0, 0, playFrame.videoReqIcon.getIconWidth(),
					playFrame.videoReqIcon.getIconHeight());
					
			playFrame.viewBigPane.add(cp);
			
			if(cp!=null)
			{
				
			}
			playFrame.viewBigPane.removeAll();
			playFrame.viewBigPane.add(cp);
			playFrame.viewBigPane.validate();
			*/
			
			//获取视图
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
