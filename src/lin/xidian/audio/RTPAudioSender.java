package lin.xidian.audio;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;

import lin.xidian.core.Wrapper;

/**
 * init start stop ����������
 * @author lindia
 *
 */
public class RTPAudioSender extends RTPAudioSenderBase
{
	private RTPAudioWrapperBase audioWrapper;
	private String selfSendIp;
	private int selfSendPort;
	private String destReciveIp;
	private int destRecivePort;
	
	//----------------------------------------
	private MediaLocator audioLocator;
	private Processor processor;
	private DataSource dataOutput;
	private RTPManager manager;
	
	private boolean startOK = true;
	
	//-----------------------------------------
	private List<AudioEventListener> list = new ArrayList<AudioEventListener>();
	
	public void init()
	{
		this.selfSendIp = audioWrapper.getSelfSendIp();
		this.selfSendPort = audioWrapper.getSelfSendPort();
		this.destReciveIp = audioWrapper.getDestReciveIp();
		this.destRecivePort = audioWrapper.getDestRecivePort();
		//------------------------------------------
		Vector audios = CaptureDeviceManager.getDeviceList(new AudioFormat(null));
		if(audios.size()>0)
		{
			audioLocator = ((CaptureDeviceInfo)audios.elementAt(0)).getLocator();
		}
	}

	public void setWrapper(Wrapper wrapper)
	{
		audioWrapper = (RTPAudioWrapperBase)wrapper;
	}

	public void stop()
	{
	    synchronized (this) {
	        if (processor != null) {
	          processor.stop();
	          processor.close();                          // ֹͣ������
	          processor = null;                           // �رմ�����
	          // ɾ������RTP������
	          manager.removeTargets( "Session ended.");
	          manager.dispose();
	        }
	      }
	}

	@Override
	public void start()
	{
		startOK = true;
		new Thread(new Runnable()
		{
			public void run()
			{
				String rst = createProcessor();
				if(rst==null)
				{
					String rst2 = createTransmitter();
					if(rst2 == null)
					{
						processor.start();
					}
					else
					{
						startOK = false;
						System.out.println(rst2);
						processor.close();
					}
				}
				else
				{
					startOK = false;
					System.out.println(rst);
					processor.close();
				}
				
				AudioStartEvent ev = new AudioStartEvent("Audio Capture start info:",startOK);
				fireAudioEventListeners(ev);
			}
		}).start();
	}
	
	  private String createProcessor() 
	  {
		    if (audioLocator == null)
		      return "Locator is null";
		    DataSource ds = null;
		    try {
		    	try
		    	{
		    		ds = Manager.createDataSource(audioLocator);
		    		processor = Manager.createProcessor(ds); 
		    	}
		    	catch(Exception e)
		    	{
		    		e.printStackTrace();
		    		if(ds == null)
		    		{
		    			ds = Manager.createDataSource(audioLocator);
		    		}
		    		processor = Manager.createProcessor(ds); 
		    	}
		    	boolean result = waitForState(processor, Processor.Configured);         // �ȴ����������ú�
			    if (result == false)
			      return "Couldn't configure processor";
			    TrackControl[] tracks = processor.getTrackControls();
			    if(tracks!=null&&tracks.length>0)
			    {
			    	ContentDescriptor descriptor = new ContentDescriptor(ContentDescriptor.RAW_RTP);
			    	processor.setContentDescriptor(descriptor);
			    	boolean isThereTrack = false;
			    	for(int i=0;i<tracks.length;i++)
			    	{
			    		if(tracks[i].isEnabled())
			    		{
			    			Format[] formats = tracks[i].getSupportedFormats();
			    			if(formats.length>0)
			    			{
			    				tracks[i].setFormat(formats[0]);
			    				isThereTrack = true;
			    			}
				    		else
				    		{
				    			tracks[i].setEnabled(false);
				    		}
			    		}
			    		else
			    		{
			    			tracks[i].setEnabled(false);
			    		}
			    	}
			    	if(isThereTrack)
			    	{
			    		boolean rst = waitForState(processor, Controller.Realized); 
			    		if(rst)
			    		{
			    			dataOutput = processor.getDataOutput();
			    		}
			    	}
			    }
		    }
		    catch (Exception e) 
		    {
		      return "Create Processor Failed";
		    }
		    return null;
	  }
	
	  private String createTransmitter()
	  {
		  PushBufferDataSource pushDataSource = (PushBufferDataSource)dataOutput;  
		  PushBufferStream[] pushstreams = pushDataSource.getStreams();
		  if(pushstreams.length>0)
		  {
			  manager = RTPManager.newInstance();
			  InetAddress inetLocal;
			try {
				  inetLocal = InetAddress.getByName(selfSendIp);
				  SessionAddress localAddress = new SessionAddress(inetLocal,selfSendPort);
				  
				  InetAddress inetDest = InetAddress.getByName(destReciveIp);
				  SessionAddress destAddress = new SessionAddress(inetDest,destRecivePort);
				 
				  System.out.println("Sender ip port");
					System.out.println("SelfData:"+localAddress.getDataAddress()+" "+localAddress.getDataPort());
					System.out.println("SelfControl:"+localAddress.getControlAddress()+" "+localAddress.getControlPort());
					System.out.println("DestData:"+destAddress.getDataAddress()+" "+destAddress.getDataPort());
					System.out.println("DestControl:"+destAddress.getControlAddress()+" "+destAddress.getControlPort());
				  
				  manager.initialize(localAddress);
				  manager.addTarget(destAddress);
				  SendStream sendStream = manager.createSendStream(dataOutput,0);
				  sendStream.start();
			} catch (Exception e) {
				e.printStackTrace();
				return "Create Transmitter Failed";
			}
		  }
		  else
		  {
			  return "Create Transmitter Failed";
		  }
		  return null;
	  }
	  
	  
	  // ������������Ϊ�Դ�����״̬�ı�Ĵ������
	  private Integer stateLock = new Integer(0);        // ״̬������
	  private boolean failed = false;                    // �Ƿ�ʧ�ܵ�״̬��־

	  // �õ�״̬��
	  Integer getStateLock() {
	    return stateLock;
	  }

	  // ����ʧ�ܱ�־
	  void setFailed() {
	    failed = true;
	  }
	
	  private synchronized boolean waitForState(Processor p, int state)
	  {
	    p.addControllerListener(new StateListener());          // Ϊ����������״̬����
	    failed = false;
	    if (state == Processor.Configured)
	    {         // ���ô�����
	      p.configure();
	    }
	    else if (state == Processor.Realized)
	    {    // ʵ�ִ�����
	      p.realize();
	    }
	    // һֱ�ȴ���ֱ���ɹ��ﵽ����״̬����ʧ��
	    while (p.getState() < state && !failed)
	    {
	      synchronized (getStateLock())
	      {
	        try
	        {
	          getStateLock().wait();
	        }
	        catch (InterruptedException ie)
	        {
	          return false;
	        }
	      }
	    }
	    if (failed)
	      return false;
	    else
	      return true;
	  }
	  
		  // �ڲ��ࣺ��������״̬������
	  class StateListener implements ControllerListener 
	  {
		    public void controllerUpdate(ControllerEvent ce) 
		    {
		      if (ce instanceof ControllerClosedEvent)   // �������ر�
		        setFailed();
		      // �������еĿ������¼���֪ͨ��waitForState�����еȴ����߳�
		      if (ce instanceof ControllerEvent)
		      {
		        synchronized (getStateLock()) 
		        {
		          getStateLock().notifyAll();
		        }
		      }
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
			list.get(i).audioUpdate(event, "send");
		}
	}

}
