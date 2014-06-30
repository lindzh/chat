package lin.xidian.audio;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.SessionEvent;

import lin.xidian.core.Wrapper;

public class RTPAudioReciver extends RTPAudioReciverBase implements ReceiveStreamListener, SessionListener,ControllerListener
{
	private RTPAudioWrapperBase audioWrapper;
	private String destSendIp;
	private int destSendPort;
	private String selfReciveIp;
	private int selfRecivePort;
	
	//----------------------------------------
	private RTPManager manager;
	private Object dataSync = new Object();
	private boolean isDataRecived = false;
	private Player player;
	
	private List<AudioEventListener> list = new ArrayList<AudioEventListener>();
	private boolean startOK = true;
	
	public void init()
	{
		this.destSendIp = audioWrapper.getDestSendIp();
		this.destSendPort = audioWrapper.getDestSendPort();
		this.selfReciveIp = audioWrapper.getSelfReciveIp();
		this.selfRecivePort = audioWrapper.getSelfRecivePort();
	}

	public void setWrapper(Wrapper wrapper)
	{
		audioWrapper = (RTPAudioWrapperBase)wrapper;
	}

	public void start()
	{
		startOK = true;
		new Thread(new Runnable()
		{

			public void run()
			{
				manager = RTPManager.newInstance();
				manager.addReceiveStreamListener(RTPAudioReciver.this);
				manager.addSessionListener(RTPAudioReciver.this);
				try {
					InetAddress inetLocal = InetAddress.getByName(selfReciveIp);
					SessionAddress localAddress = new SessionAddress(inetLocal,selfRecivePort);
					InetAddress inetDest = InetAddress.getByName(destSendIp);
					SessionAddress destAddress = new SessionAddress(inetDest,destSendPort);
					System.out.println("Reciver ip port");
					System.out.println("SelfData:"+localAddress.getDataAddress()+" "+localAddress.getDataPort());
					System.out.println("SelfControl:"+localAddress.getControlAddress()+" "+localAddress.getControlPort());
					System.out.println("DestData:"+destAddress.getDataAddress()+" "+destAddress.getDataPort());
					System.out.println("DestControl:"+destAddress.getControlAddress()+" "+destAddress.getControlPort());
					manager.initialize(localAddress);
					BufferControl bc = (BufferControl)manager.getControl("javax.media.control.BufferControl");
					bc.setBufferLength(350);
					manager.addTarget(destAddress);
					isDataRecived = false;
					synchronized(dataSync)
					{
						while(!isDataRecived)
						{
							dataSync.wait(1000);
							System.out.println("Waitting for data to arrive");
						}
					}
				} catch (Exception e) 
				{
					startOK = true;
					e.printStackTrace();
				}
				AudioStartEvent ev = new AudioStartEvent("Audio Reciver info:",startOK);
				fireAudioEventListeners(ev);
			}
		}).start();
	}

	public void stop()
	{
    	manager.removeTargets( "Closing session from RTPReceive");
		manager.dispose();
    	player.removeControllerListener(this);
    	player.close();
	}

	public void update(ReceiveStreamEvent evt)
	{
		  //RTPManager mgr = (RTPManager)evt.getSource();
		  //Participant participant = evt.getParticipant();	// 得到加入者（发送者）
		    ReceiveStream stream = evt.getReceiveStream();      // 得到接收到的数据流
		    if (evt instanceof NewReceiveStreamEvent) {           // 接收到新的数据流
		      try {
		        stream = ((NewReceiveStreamEvent)evt).getReceiveStream();   // 得到新数据流
		        DataSource ds = stream.getDataSource();                     // 得到数据源
		        player = javax.media.Manager.createPlayer(ds);       // 通过数据源构造一个媒体播放器
		        if (player == null)
		          return;
		        player.addControllerListener(this);             // 给播放器添加控制器监听
		        player.realize();                               // 实现播放器
		        synchronized (dataSync) {
		        	isDataRecived = true;
		          dataSync.notifyAll();
		        }
		      }
		      catch (Exception e) {
		        System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
		        return;
		      }
		    }
		    else if (evt instanceof ByeEvent)
		    {                  // 数据接收完毕
		    	player.close();
		    }		
	}

	public void update(SessionEvent arg0)
	{
		
	}

	public void controllerUpdate(ControllerEvent ce)
	{
	    player = (Player)ce.getSourceController();            // 得到事件源
	    if (player == null)
	      return;

	    if (ce instanceof RealizeCompleteEvent) 
	    {               // 播放器实现完成
	      player.start();                   // 开始播放
	    }

	    if (ce instanceof ControllerErrorEvent) {              // 控制器错误
	    	manager.removeTargets( "Closing session from RTPReceive");
			manager.dispose();
	    	player.removeControllerListener(this);
	    	player.close();
	    }		
	}
	
	@Override
	public void addAudioEventListener(AudioEventListener listener)
	{
		list.add(listener);
	}
	
	private void fireAudioEventListeners(AudioEvent event)
	{
		int len = list.size();
		for(int i=0;i<len;i++)
		{
			list.get(i).audioUpdate(event, "recive");
		}
	}
}
