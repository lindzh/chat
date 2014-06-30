package lin.xidian.frame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lin.xidian.core.AudioCancelListener;
import lin.xidian.utils.Constant;

public class AudioPanel extends JPanel implements Runnable
{
	private long destId;
	private JButton button = new JButton("取消");
	private String image = "audio.png";
	private JLabel hasChatTime = new JLabel("等待回应中...");
	private List<AudioCancelListener> audioCncelListeners = new ArrayList<AudioCancelListener>();
	private boolean isRunning = false;
	
	private ActionListener btnListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			JButton btn = (JButton)e.getSource();
			if(btn == button)
			{
				stopTimer();
				int len = audioCncelListeners.size();
				for(int i=0;i<len;i++)
				{
					audioCncelListeners.get(i).audioCancel(destId);
				}
			}
		}
	};
	
	public AudioPanel(long id)
	{
		this.destId = id;
	}
	
	public void  initGUI()
	{
		JLabel imgLabel = new JLabel();
		ImageIcon icon = new ImageIcon(Constant.ROOT_DIR+"\\image\\"+image);
		imgLabel.setIcon(icon);
		hasChatTime.setFont(new Font("幼圆",Font.BOLD,20));
		button.addActionListener(btnListener);
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.add(imgLabel);
		this.add(hasChatTime);
		this.add(button);
	}

	public void startTimer()
	{
		new Thread(this).start();
	}
	
	public void addAudioCancelListener(AudioCancelListener listener)
	{
		audioCncelListeners.add(listener);
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
			hasChatTime.setText(builder.toString());
		}
	}
	
	public void stopTimer()
	{
		isRunning = false;
	}
}
