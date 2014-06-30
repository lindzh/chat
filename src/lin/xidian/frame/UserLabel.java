package lin.xidian.frame;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import lin.xidian.core.ChatBuildListener;
import lin.xidian.core.ChatCloseListener;
import lin.xidian.core.ChatFrameCloseListener;
import lin.xidian.core.User;
import lin.xidian.utils.Constant;


//ChatBuildListener��ʹ��Manager��Reciver��ͬʱ��Reciver�м���ChatBuildListener�ӿ�
public class UserLabel extends JLabel implements Runnable,ChatFrameCloseListener,ChatBuildListener,ChatCloseListener
{
	private User managerUser;//���ڱ���Է�Ip��Ϣ,����ʹ������
	private List<String> messages = new ArrayList<String>();
	private boolean isFrameBuild = false;
	private boolean isTicking = false;
	private String imgPath = Constant.ROOT_DIR+"\\image\\";
	private String onImg = "on.png";
	private String offImg = "out.png";
	private ImageIcon icon; 
	private User messageUser;
	
	public void setOppMessageSocketAddress(String ip,int port)
	{
		messageUser.setIp(ip);
		messageUser.setPort(port);
	}
	
	public User getOppManagerUser()
	{
		return managerUser;
	}
	
	public User getOppMessageUser()
	{
		return messageUser;
	}
	
	public UserLabel(User user)
	{
		super();
		this.managerUser = user;
		messageUser = new User(this.managerUser.getId(),this.managerUser.getName(),null,null,0);
		isTicking = false;
		icon = new ImageIcon(imgPath+onImg);
		this.setIcon(icon);
		this.setText(user.getName()+"\n"+user.getId());
		this.setBorder(BorderFactory.createTitledBorder(""));
	}
	
	public boolean canbuildFrame()
	{
		return !isFrameBuild;
	}
	
	public void setFrameBuild(boolean isBuild)
	{
		isFrameBuild = isBuild;
	}
	
	public long getId()
	{
		return managerUser.getId();	
	}
	
	public void stopTick()
	{
		isTicking = false;
	}
	
	public void startTick()
	{
		if(!isTicking)
		{
			isTicking = true;
			new Thread(this).start();
		}
	}
	
	public void logout()
	{
		icon = new ImageIcon(imgPath+offImg);
		setIcon(icon);
	}
	
	public List<String> getMessages()
	{
		return messages;
	}
	
	public void clearMessages()
	{
		messages.clear();
	}
	
	public void addNewMessage(String message)
	{
		messages.add(message);
		if(!isTicking)
		{
			startTick();
		}
	}

	public void run()
	{
		//boolean visible = true;
		boolean isRight = true;
		ImageIcon yicon = new ImageIcon(imgPath+"on.png");
		ImageIcon nicon = new ImageIcon(imgPath+"tick.png");
		while(isTicking)
		{
			//setVisible(!visible);
			if(isRight)
			{
				setIcon(yicon);
			}
			else
			{
				setIcon(nicon);
			}
			try {
				Thread.currentThread().sleep(500);//0.7s��һ��
				isRight = !isRight;	
				//visible = !visible;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setIcon(icon);
		//setVisible(true);
	}

	//�Լ��ѽ����Ĵ����ѹر�
	public void chatFrameClose(long id, long myid)
	{
		System.out.println("My frame close:"+id);
		if(managerUser.getId() == id)
		{
			setFrameBuild(false);
		}
		
	}

	//�Է��������ѽ����¼�,�Է��ѽ������Լ���û�н���ʱʹ��
	public void chatBuild(long id, String ip, int port)
	{
		if(messageUser.getId() == id)
		{
			messageUser.setIp(ip);
			messageUser.setPort(port);
		}
	}

	//�Է������ڹر��¼�
	public void chatClose(long id)
	{
		System.out.println("�Է������ѹر�");
		if(messageUser.getId() == id)
		{
			messageUser.setPort(0);	
		}
		
	}
}
