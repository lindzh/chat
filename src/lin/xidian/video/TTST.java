package lin.xidian.video;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lin.xidian.core.VideoCancelListener;
import lin.xidian.frame.VideoPanel;
import lin.xidian.utils.Constant;

public class TTST implements VideoEventListener,VideoCancelListener
{
		private VideoWrapperBase wrapper = new VideoWrapper();
		private String selfIp = "222.25.187.241";
		private String destIp = "222.25.187.241";
		private JFrame frame = new JFrame("视频");
		
		private VideoPanel videoPanel = new VideoPanel();
		private JPanel btnPanel = new JPanel();
		private JButton mybtn = new JButton("TEST");
		private JPanel main = new JPanel();
		
		public void init()
		{
			wrapper.setDestId(1234567);
			wrapper.setDestName("zywin7");
			wrapper.setSelfReciveIp(selfIp);
			wrapper.setSelfSendIp(selfIp);
			wrapper.setDestReciveIp(destIp);
			wrapper.setDestSendIp(destIp);
			wrapper.setSelfSendPort(Constant.videoSendPort);
			wrapper.setSelfRecivePort(Constant.videoRecivePort);
			wrapper.setDestSendPort(Constant.videoSendPort);
			wrapper.setDestRecivePort(Constant.videoRecivePort);
			wrapper.init();
			
			wrapper.addVideoEventListener(this, "send");
			wrapper.addVideoEventListener(this, "recive");
			
			videoPanel.init(123456);
			main.setLayout(new BoxLayout(main,BoxLayout.X_AXIS));
			main.add(videoPanel);
			btnPanel.add(mybtn);
			main.add(btnPanel);
			frame.add(main);
			frame.setBounds(200, 200, 800, 500);
			frame.setVisible(true);
			wrapper.start();
		}
		
		
		
		public static void main(String[] args)
		{
			TTST test = new TTST();
			test.init();
		}

		public void videoUpdate(VideoEvent event, String type)
		{
			if(type.equals("send"))
			{
				if(event instanceof VideoStartEvent)
				{
					VideoStartEvent ev = (VideoStartEvent)event;
					boolean state = ev.getState();
					String info = ev.getInfo();
					System.out.println(info+state);
				}
				if(event instanceof VideoVisualEvent)
				{
					VideoVisualEvent ev = (VideoVisualEvent)event;
					Component cp = ev.getVisualComponent();
					videoPanel.addLocalVideo(cp);
				}
			}
			if(type.equals("recive"))
			{
				if(event instanceof VideoStartEvent)
				{
					VideoStartEvent ev = (VideoStartEvent)event;
					boolean state = ev.getState();
					String info = ev.getInfo();
					System.out.println(info+state);
				}
				if(event instanceof VideoVisualEvent)
				{
					VideoVisualEvent ev = (VideoVisualEvent)event;
					Component cp = ev.getVisualComponent();
					videoPanel.addDestVideo(cp);
					System.out.println("Rec 视频接收");
				}
			}
		}



		public void videoCancel(long id)
		{
			
			
		}
}
