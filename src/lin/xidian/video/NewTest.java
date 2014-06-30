package lin.xidian.video;

import java.awt.Component;
import javax.swing.JFrame;

import lin.xidian.utils.Constant;

public class NewTest implements VideoEventListener
{
		private VideoWrapperBase wrapper = new VideoWrapper();
		private String selfIp = "222.25.187.241";
		private String destIp = "222.25.187.241";
		private JFrame frame = new JFrame("视频");
		private Component local;
		private Component dest;
		
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
			wrapper.start();
			frame.setLayout(null);
			frame.setBounds(200, 200, 500, 400);
			frame.setVisible(true);
		}
		
		
		
		public static void main(String[] args)
		{
			NewTest test = new NewTest();
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
					local = ev.getVisualComponent();
					local.setBounds(1, 1, 480, 360);
					frame.add(local);
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
					dest = ev.getVisualComponent();
					dest.setBounds(1, 365, 118, 87);
					frame.add(dest);
					System.out.println("Rec 视频接收");
				}
			}
		}
}
