package lin.xidian.frame;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import lin.xidian.core.FileCancelListener;
import lin.xidian.core.TransFile;

public class TransPanel extends JPanel
{
	private static final long serialVersionUID = -2104866126225278172L;
	private String type;
	private TransFile file;
	private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL,0,50);
	private JButton cancelButton = new JButton("取消");
	private List<FileCancelListener> fileCancelListeners = new ArrayList<FileCancelListener>();
	private long hasRecOrSend = 0;
	private String head = "";
	private String total = "";
	
	private ActionListener btnListeber = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)//主动取消
		{
			JButton btn = (JButton)e.getSource();
			if(btn == cancelButton)
			{
				int len = fileCancelListeners.size();
				for(int i=0;i<len;i++)
				{
					fileCancelListeners.get(i).cancelFile(file.getId(), type);
				}
			}
		}
	};
	
	public String getSize(long length)
	{
		String[] units = new String[]{"Kb","Mb","Gb","Tb"};
		String unit = "b";
		int i = 0;
		long point = 1024;
		while(length/point>1024)
		{
			unit = units[i];
			point*=1024;
			i++;
		}
		return length/point+unit;
	}
	
	public TransPanel(String type,TransFile file)
	{
		this.type = "self"+type;
		this.file = file;
		///progressBar.setString(file.getName());
		progressBar.setStringPainted(true);
		this.setLayout(new FlowLayout());
		this.add(progressBar);
		cancelButton.addActionListener(btnListeber);
		this.add(cancelButton);
		
		total = getSize(file.getSize());
		if(type.equals("send"))
		{
			head = "已发送   ";
			this.setBorder(BorderFactory.createTitledBorder(file.getName()));
		}
		if(type.equals("recive"))
		{
			this.setBorder(BorderFactory.createTitledBorder(file.getName()));
			head = "已 接收   ";
		}
	}
	
	public TransFile getTransFile()
	{
		return file;
	}
	
	public void addValue(int length)
	{
		int value = 0;
		hasRecOrSend+=length;
		double a = (double)hasRecOrSend;
		double b = (double)file.getSize();
		double rst = a/b;
		value = (int)(rst*100);
		progressBar.setValue(value/2);
		progressBar.setString(head+value+"% 共 "+total);
	}
	
	public void addFileCancelListeners(FileCancelListener listener)
	{
		fileCancelListeners.add(listener);
	}
}
