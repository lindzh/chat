package lin.xidian.video;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.Codec;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Owned;
import javax.media.Player;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.control.QualityControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.SourceCloneable;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;

import lin.xidian.core.Wrapper;
/**
 * 其他事件相应加入
 * @author lindia
 *
 */
public class VideoSender extends VideoSenderBase implements ControllerListener,Runnable
{
	private VideoWrapperBase videoWrapper;
	
	private String selfSendIp;
	private int selfSendPort;
	private String destReciveIp;
	private int destRecivePort;
	
	private Processor videoProcessor;
	
	private DataSource videoDataSource;
	private DataSource videoOutput;
	
	private DataSource mediaData = null;
	private DataSource dataLocalClone = null;
	private RTPManager manager;
	private MediaLocator videoLocator;
	
	private Player player;
	
	private List<VideoEventListener> list = new ArrayList<VideoEventListener>();
	
	private boolean startOK = true;//用于查看初始化与事件响应
	
	public void init()
	{
		selfSendIp = videoWrapper.getSelfSendIp();
		selfSendPort = videoWrapper.getSelfSendPort();
		destReciveIp = videoWrapper.getDestReciveIp();
		destRecivePort = videoWrapper.getDestRecivePort();
	}
	
	@Override
	public void start()
	{		
		new Thread(this).start();
	}
	
	public void run()
	{
		startOK = true;
		Vector videos = CaptureDeviceManager.getDeviceList(new VideoFormat(null));
		if(videos!=null&&videos.size()>0)
		{
			videoLocator = ((CaptureDeviceInfo)videos.get(0)).getLocator();
			videoProcessor = createProcessor(videoLocator);
			if(videoProcessor != null)
			{
				videoDataSource = mediaData;
				videoOutput = videoProcessor.getDataOutput();
			}	
		}
		try {
			player = Manager.createPlayer(videoDataSource);
			player.addControllerListener(this);
			player.realize();
		} catch (Exception e)
		{
			startOK = false;
			e.printStackTrace();
		}
		try {
			createTransmitter(videoOutput);
		} catch (Exception e) 
		{
			startOK = false;
			e.printStackTrace();
		}
		videoProcessor.start();
		VideoStartEvent ev = new VideoStartEvent("Capture initialize and Transmit state :",startOK);
		fireVideoEventListeners(ev);
	}

	public void stop()
	{
		player.stop();
		player.close();
		player.removeControllerListener(this);
		player = null;
		
		videoProcessor.stop();
		videoProcessor.close();
		videoProcessor = null;
		
        manager.removeTargets( "Session ended.");
        manager.dispose();
        manager.removeTargets( "Session ended.");
        manager.dispose();
	}

	public void setWrapper(Wrapper wrapper)
	{
		videoWrapper = (VideoWrapperBase)wrapper;
	}
	

	private Processor createProcessor(MediaLocator locator)
	{
		Processor processor = null;
		if (locator == null)
			return null;
		mediaData = null;
		try {
			mediaData = Manager.createDataSource(locator);
			mediaData = Manager.createCloneableDataSource(mediaData);
			dataLocalClone = ((SourceCloneable) mediaData).createClone();
		} catch (Exception e) {
			e.printStackTrace();
			try {
					mediaData = Manager.createDataSource(locator);
					mediaData = Manager.createCloneableDataSource(mediaData);
					dataLocalClone = ((SourceCloneable) mediaData).createClone();
			} catch (Exception e1) {
				startOK = false;
				e1.printStackTrace();
			}
		}
		try {
			processor = javax.media.Manager.createProcessor(dataLocalClone);
		} catch (Exception npe) {
			startOK = false;
			npe.printStackTrace();
			return null;
		}
		boolean result = waitForState(processor, Processor.Configured);
		if (result == false)
		{
			startOK = false;
			return null;
		}
		TrackControl[] tracks = processor.getTrackControls();
		if (tracks == null || tracks.length < 1)
		{
			startOK = false;
			return null;
		}
		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
		processor.setContentDescriptor(cd);
		Format supportedFormats[];
		Format chosen;
		boolean atLeastOneTrack = false;
		for (int i = 0; i < tracks.length; i++) {
			if (tracks[i].isEnabled()) {
				supportedFormats = tracks[i].getSupportedFormats();
				if (supportedFormats.length > 0) {
					if (supportedFormats[0] instanceof VideoFormat) {
						chosen = checkForVideoSizes(tracks[i].getFormat(),
								supportedFormats[0]);
					} else
						chosen = supportedFormats[0];
					tracks[i].setFormat(chosen);
					atLeastOneTrack = true;
				} else
					tracks[i].setEnabled(false);
			} else
				tracks[i].setEnabled(false);
		}

		if (!atLeastOneTrack)
		{
			startOK = false;
			return null;
		}
		result = waitForState(processor, Controller.Realized);
		if (result == false)
		{
			startOK = false;
			return null;
		}
		setJPEGQuality(processor, 0.5f);
		return processor;
	}
	
	private String createTransmitter(DataSource dataOpt)
	{
		PushBufferDataSource pbds = (PushBufferDataSource) dataOpt;
		PushBufferStream pbs[] = pbds.getStreams();
		try
		{
			manager = RTPManager.newInstance();
			SessionAddress localAddress = new SessionAddress(InetAddress.getByName(selfSendIp),selfSendPort);
			SessionAddress destAddress = new SessionAddress(InetAddress.getByName(destReciveIp),destRecivePort);
			
			System.out.println("Sender  controlPort  dataPort");
			System.out.println("Local PORT:"+localAddress.getControlPort()+"  "+localAddress.getDataPort());
			System.out.println("Dest PORT:"+destAddress.getControlPort()+" "+destAddress.getDataPort());
			
			manager.initialize(localAddress);
			manager.addTarget(destAddress);
			SendStream sendStream = manager.createSendStream(dataOpt, 0);
			sendStream.start();
		}catch(Exception e)
		{
			startOK = false;
			e.printStackTrace();
			return "create Transmitter Failed";
		}
		return null;
	}
	
	
	private Format checkForVideoSizes(Format original, Format supported)
	{

		int width, height;
		Dimension size = ((VideoFormat) original).getSize();
		Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
		Format h263Fmt = new Format(VideoFormat.H263_RTP);

		if (supported.matches(jpegFmt)) 
		{
			width = (size.width % 8 == 0 ? size.width
					: (int) (size.width / 8) * 8);
			height = (size.height % 8 == 0 ? size.height
					: (int) (size.height / 8) * 8);
		} else if (supported.matches(h263Fmt))
		{
			if (size.width < 128)
			{
				width = 128;
				height = 96;
			} else if (size.width < 176)
			{
				width = 176;
				height = 144;
			} else
			{
				width = 352;
				height = 288;
			}
		} else 
		{
			return supported;
		}

		return (new VideoFormat(null, new Dimension(width, height),
				Format.NOT_SPECIFIED, null, Format.NOT_SPECIFIED))
				.intersects(supported);
	}
	
	private void setJPEGQuality(Player p, float val)
	{
		Control cs[] = p.getControls();
		QualityControl qc = null;
		VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);
		for (int i = 0; i < cs.length; i++) {

			if (cs[i] instanceof QualityControl && cs[i] instanceof Owned) {
				Object owner = ((Owned) cs[i]).getOwner();
				if (owner instanceof Codec) {
					Format fmts[] = ((Codec) owner)
							.getSupportedOutputFormats(null);
					for (int j = 0; j < fmts.length; j++) {
						if (fmts[j].matches(jpegFmt)) {
							qc = (QualityControl) cs[i];
							qc.setQuality(val);
							System.err.println("- Setting quality to " + val
									+ " on " + qc);
							break;
						}
					}
				}
				if (qc != null)
					break;
			}
		}
	}
	private Integer stateLock = new Integer(0);
	private boolean failed = false;

	Integer getStateLock() {
		return stateLock;
	}

	void setFailed() {
		failed = true;
	}

	private synchronized boolean waitForState(Processor p, int state) {
		p.addControllerListener(new StateListener());
		failed = false;
		if (state == Processor.Configured) {
			p.configure();
		} else if (state == Processor.Realized) {
			p.realize();
		}
		while (p.getState() < state && !failed) {
			synchronized (getStateLock()) {
				try {
					getStateLock().wait();
				} catch (InterruptedException ie) {
					return false;
				}
			}
		}

		if (failed)
			return false;
		else
			return true;
	}
	class StateListener implements ControllerListener {

		public void controllerUpdate(ControllerEvent ce) {

			if (ce instanceof ControllerClosedEvent)
				setFailed();

			if (ce instanceof ControllerEvent) {
				synchronized (getStateLock()) {
					getStateLock().notifyAll();
				}
			}
		}
	}
	
	public void controllerUpdate(ControllerEvent event)
	{
		if(event instanceof RealizeCompleteEvent)
		{
			//获取视图
			Component cp = player.getVisualComponent();
			if(cp != null)
			{
				VideoVisualEvent ev = new VideoVisualEvent(cp);
				fireVideoEventListeners(ev);
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
			list.get(i).videoUpdate(videoEvent, "send");
		}
	}
	
}
