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


//ChatBuildListener需使用Manager的Reciver，同时在Reciver中加入ChatBuildListener接口
public class UserLabel extends JLabel implements Runnable,ChatFrameCloseListener,ChatBuildListener,ChatCloseListener
{
	private User managerUser;//用于保存对方Ip信息,建议使用两个
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
				Thread.currentThread().sleep(500);//0.7s闪一次
				isRight = !isRight;	
				//visible = !visible;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setIcon(icon);
		//setVisible(true);
	}

	//自己已建立的窗口已关闭
	public void chatFrameClose(long id, long myid)
	{
		System.out.println("My frame close:"+id);
		if(managerUser.getId() == id)
		{
			setFrameBuild(false);
		}
		
	}

	//对方主窗口已建立事件,对方已建立但自己还没有建立时使用
	public void chatBuild(long id, String ip, int port)
	{
		if(messageUser.getId() == id)
		{
			messageUser.setIp(ip);
			messageUser.setPort(port);
		}
	}

	//对方主窗口关闭事件
	public void chatClose(long id)
	{
		System.out.println("对方窗口已关闭");
		if(messageUser.getId() == id)
		{
			messageUser.setPort(0);	
		}
		
	}
}
