package lin.xidian.frame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lin.xidian.core.VideoCancelListener;
import lin.xidian.utils.Constant;

public class VideoPanel extends JPanel implements ActionListener,Runnable
{
	private String imgPath = Constant.ROOT_DIR+"\\image\\";
	private Component destVisual;
	private Component localVisual;
	
	private JLabel destVideo;
	private JLabel localVideo;
	
	private JLabel infoLabel = new JLabel("等待回应中...");
	private JButton button = new JButton("取消");
	
	private boolean isRunning = false;
	private List<VideoCancelListener> listeners = new ArrayList<VideoCancelListener>();
	private long destId = 0;
	
	public void init(long destid)
	{
		this.setLayout(null);
		this.destId = destid;
		ImageIcon local = new ImageIcon(imgPath+"localVideo.jpg");
		ImageIcon dest = new ImageIcon(imgPath+"destVideo.jpg");
		destVideo = new JLabel(dest);
		localVideo = new JLabel(local);
		infoLabel.setFont(new Font("幼圆",Font.BOLD,25));
		button.addActionListener(this);
		
		Dimension destMension = new Dimension(480,360);
		destVideo.setMaximumSize(destMension);
		destVideo.setMinimumSize(destMension);
		destVideo.setPreferredSize(destMension);
		destVideo.setBounds(1, 1, 480, 360);
		
		Dimension localMension = new Dimension(118,87);
		localVideo.setMaximumSize(localMension);
		localVideo.setMinimumSize(localMension);
		localVideo.setPreferredSize(localMension);
		localVideo.setBounds(1, 365, 118, 87);
		
		Dimension infoMension = new Dimension(150,40);
		infoLabel.setPreferredSize(infoMension);
		infoLabel.setMinimumSize(infoMension);
		infoLabel.setMaximumSize(infoMension);
		infoLabel.setBounds(130, 367, 150, 40);

		button.setBounds(130, 410, 100, 40);
		
		this.add(destVideo);
		this.add(localVideo);
		this.add(infoLabel);
		this.add(button);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(isRunning)
		{
			isRunning = false;
		}
		fireVideoCancelListeners();
	}

	public void run()
	{
		isRunning = true;
		long last = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		StringBuilder builder;
		while(isRunning)
		{
			builder = new StringBuilder();
			now = System.currentTimeMillis();//以毫秒为单位
			long as = (now - last)/1000;
			int hour = (int)(as/3600);
			int min = (int)((as-hour*60)/60);
			int sec = (int)(as-hour*3600-min*60);
			if(hour<10)
			{
				builder.append("0"+hour+" : ");
			}
			else
			{
				builder.append(hour+" : ");
			}
			if(min<10)
			{
				builder.append("0"+min+" : ");
			}
			else
			{
				builder.append(min+" : ");
			}
			if(sec<10)
			{
				builder.append("0"+sec);
			}
			else
			{
				builder.append(sec);
			}
			infoLabel.setText(builder.toString());
		}
	}
	
	public void stopTimer()
	{
		isRunning = false;
	}
	
	public void startTimer()
	{
		if(!isRunning)
		{
			new Thread(this).start();
		}
	}
	
	public void addDestVideo(Component view)
	{
		destVisual = view;
		Dimension destMension = new Dimension(480,360);
		destVisual.setMaximumSize(destMension);
		destVisual.setMinimumSize(destMension);
		destVisual.setPreferredSize(destMension);
		destVisual.setBounds(1, 1, 480, 360);
		this.remove(destVideo);
		this.add(destVisual);
		startTimer();
	}
	
	public void addLocalVideo(Component view)
	{
		localVisual = view;
		Dimension localMension = new Dimension(118,87);
		localVisual.setMaximumSize(localMension);
		localVisual.setMinimumSize(localMension);
		localVisual.setPreferredSize(localMension);
		localVisual.setBounds(1, 365, 118, 87);
		this.remove(localVideo);
		this.add(localVisual);
		startTimer();
	}
	
	public void fireVideoCancelListeners()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = listeners.size();
				for(int i=0;i<len;i++)
				{
					listeners.get(i).videoCancel(destId);
				}
			}
		}).start();
	}
	
	public void addVideoCancelListener(VideoCancelListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeAllAdd()
	{
		destVideo = null;
		localVideo = null;
		listeners.clear();
		listeners = null;
	}
}
