package lin.xidian.video;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lin.xidian.utils.Constant;

public class SendTest implements VideoEventListener,ActionListener
{
		private VideoWrapperBase wrapper = new VideoWrapper();
		private String selfIp = "222.25.187.241";
		private String destIp = "222.25.187.61";
		private JFrame frame = new JFrame("视频");
		private JPanel mainFrame = new JPanel();
		private JPanel bodyFrame = new JPanel();
		private JPanel headFrame = new JPanel();
		private JButton closeBtn = new JButton("结束");
		private JButton startBtn = new JButton("开始");
		private JButton exitBtn = new JButton("关闭");
		
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
			
			mainFrame.setLayout(new BorderLayout());
			bodyFrame.setBounds(100, 100, 300, 600);
			closeBtn.addActionListener(this);
			startBtn.addActionListener(this);
			exitBtn.addActionListener(this);
			headFrame.setLayout(new BoxLayout(headFrame,BoxLayout.X_AXIS));
			headFrame.add(startBtn);
			headFrame.add(closeBtn);
			headFrame.add(exitBtn);
			mainFrame.add(headFrame,BorderLayout.NORTH);
			mainFrame.add(bodyFrame,BorderLayout.CENTER);
			frame.setBounds(200, 200, 300, 500);
			frame.add(mainFrame);
			frame.setVisible(true);
		}
		
		
		
		public static void main(String[] args)
		{
			SendTest test = new SendTest();
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
					JPanel sendPanel = new JPanel();
					sendPanel.setBorder(BorderFactory.createTitledBorder("发送端"));
					sendPanel.add(cp);
					bodyFrame.add(sendPanel);
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
					JPanel sendPanel = new JPanel();
					sendPanel.setBorder(BorderFactory.createTitledBorder("接收端"));
					sendPanel.add(cp);
					bodyFrame.add(sendPanel);
					System.out.println("Rec 视频接收");
				}
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			JButton btn = (JButton)e.getSource();
			if(btn == closeBtn)
			{
				wrapper.stop();
			}
			if(btn == exitBtn)
			{
				wrapper.stop();
				frame.dispose();
				System.exit(0);
			}
			if(btn == startBtn)
			{
				wrapper.start();
			}
		}
}
