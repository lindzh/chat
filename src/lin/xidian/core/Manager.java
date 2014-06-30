package lin.xidian.core;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import lin.xidian.utils.DatagramSocketFactory;
import lin.xidian.utils.InetUtils;

/**
 * set init start,login logout
 * @author lindia
 *
 */
public class Manager extends ManagerBase
{
	private long destId = 0000;
	private String destIp = "222.25.187.241";
	private int destPort = 9999;
	private String destName = "server";
	private DatagramSocket socket;
	private long userId = 0;
	private String userName;
	private String password;
	private List<LogoutListener> listeners = new ArrayList<LogoutListener>();
	private List<User> users = new ArrayList<User>();
	private Sender sender;
	private ManagerReciverBase reciver;
	private boolean hasLoged = false;
	private boolean isStarted = false;
	
	public Manager()
	{
		socket = DatagramSocketFactory.getInstance().createDatagramSocket("manager");
	}
	
	public void setDestId(long id)
	{
		destId = id;
	}

	public void setDestIp(String ip)
	{
		destIp = ip;
	}

	public void setDestName(String name)
	{
		this.destName = name;
	}

	public void setDestPort(int port)
	{
		this.destPort = port;
		
	}

	public void setReciver(Reciver reciver)
	{
		this.reciver = (ManagerReciverBase)reciver;
		this.reciver.setWrapper(this);
		this.reciver.addLoginListener(this);
		this.reciver.addLogoutListener(this);
		this.reciver.addNewMessageListener(this);
		this.reciver.addTickListener(this);
	}

	public void setSender(Sender sender)
	{
		this.sender = sender;
		sender.setWrapper(this);
	}

	public void start()
	{
		if(!isStarted)
		{
			reciver.start();
			isStarted = true;
		}
	}

	public void stop()
	{
		sender.stop();
		reciver.stop();
	}

	public String getServerIp()
	{
		return destIp;
	}

	public int getServerPort()
	{
		return destPort;
	}

	public long getUserId()
	{
		return userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setServerIp(String serverip)
	{
		destIp = serverip;		
	}

	public void setServerPort(int port)
	{
		destPort = port;
	}

	public void setUserId(long id)
	{
		userId = id;
	}

	public void setUserName(String name)
	{
		userName = name;		
	}

	public void setUserPassword(String psw)
	{
		password = psw;
	}
	
	//有人登陆时提示
	public void login(long id, String name, String ip, int port)
	{
		System.out.println("收到login："+id);
		
		User user = new User(id,name,"",ip,port);
		if(userId == id)
		{
			
			userName = name;
			hasLoged = true;
		}
		else
		{
			users.add(user);
		}
	}

	//有人退出时提示
	public void logout(long id)
	{
		if(id == destId)
		{
			int len = listeners.size();
			for(int i=0;i<len;i++)
			{
				LogoutListener listener = listeners.get(i);
				listener.logout(userId);
			}
		}
		for(int i=0;i<users.size();i++)
		{
			if(users.get(i).getId() == id)
			{
				users.remove(i);
				return ;
			}
		}
	}

	//服务器发送来的心跳包消息
	public void tick(String tickinfo)
	{
		System.out.println("心跳包："+tickinfo);
	}

	//自己登陆
	@Override
	public void login()
	{
		User user = new User(destId,destName,"",destIp,destPort);
		users.add(user);
		String message = "login:"+userId+","+password+","+InetUtils.getLocalIp();
		int num = 0;
		while(!hasLoged&&num<4)
		{
			try {
				sender.send("login", message.getBytes());
				num++;
				Thread.currentThread().sleep(10000*num);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(num == 5)
		{
			System.out.println("服务器没有打开");
			//添加
		}
	}

	//自己退出
	@Override
	public void logout()
	{
		String message = "logout:"+userId;
		sender.send("logout", message.getBytes());
		int len = listeners.size();
		for(int i=0;i<len;i++)
		{
			LogoutListener listener = listeners.get(i);
			listener.logout(userId);
		}
	}

	@Override
	public void addLogoutListener(LogoutListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeLogoutLstener(LogoutListener listener)
	{
		listeners.remove(listener);
	}

	public DatagramSocket getDatagramSocket()
	{
		return socket;
	}

	public long getDestId()
	{
		return destId;
	}

	public String getDestIp()
	{
		return destIp;
	}

	public String getDestName()
	{
		return destName;
	}

	public int getDestPort()
	{
		return destPort;
	}

	public void init()
	{
		sender.init();
		reciver.init();
	}

	public void startTick()
	{
		sender.startTick();
	}

	public void newMessage(String type, long num, String ip, int port,
			String info)
	{
		
	}

	@Override
	public void requestFriendsAndGroups()
	{
		String message = "requestFriendsAndGroups:"+this.getUserId();
		sender.send("", message.getBytes());
	}

}
