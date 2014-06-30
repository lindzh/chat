package lin.xidian.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * addListeners setWrapper,init,start
 * @author lindia
 *
 */
public class ManagerReciver extends ManagerReciverBase
{
	private List<LoginListener> loginListeners = new ArrayList<LoginListener>();
	private List<LogfailListener> logfailListeners = new ArrayList<LogfailListener>();
	private List<LogoutListener> logoutListeners = new ArrayList<LogoutListener>();
	private List<NewMessageListener> messageListeners = new ArrayList<NewMessageListener>();
	private List<FinishListener> finishListeners = new ArrayList<FinishListener>();
	private List<RecieveEventListener> recieveEventListeners = new ArrayList<RecieveEventListener>();
	private List<TickListener> tickListeners = new ArrayList<TickListener>();
	
	private Wrapper wrapper;
	private DatagramSocket socket;
	private boolean running = false; 
	private byte[] buffer = new byte[1024];
	private DatagramPacket packet;
	@Override
	public void addLoginListener(LoginListener listener)
	{
		loginListeners.add(listener);
	}

	@Override
	public void addLogoutListener(LogoutListener listener)
	{
		logoutListeners.add(listener);
	}

	@Override
	public void addNewMessageListener(NewMessageListener listener)
	{
		messageListeners.add(listener);
	}

	public void addFinishListener(FinishListener listener)
	{
		finishListeners.add(listener);
	}

	public void addRecieveEventListener(RecieveEventListener listener)
	{
		recieveEventListeners.add(listener);
	}

	public void addTickListener(TickListener listener)
	{
		tickListeners.add(listener);
	}
	
	public void init()
	{
		socket = wrapper.getDatagramSocket();
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;		
	}

	public void start()
	{
		running = true;
		new Thread(this).start();
		System.out.println("¿ªÆô½ÓÊÕ");
		System.out.println("Login¡ª¡ªNum£º"+loginListeners.size());
	}

	public void stop()
	{
		running = false;
		
		/*
		wrapper = null;
		socket = null;
		packet = null;
		loginListeners = null;
		logoutListeners = null;
		messageListeners = null;
		finishListeners = null;
		recieveEventListeners = null;
		*/
	}
	
	public void fireLogoutListeners(long id)
	{
		int len = logoutListeners.size();
		for(int i=0;i<len;i++)
		{
			logoutListeners.get(i).logout(id);
		}
	}
	
	public void fireLoginListeners(long id,String name,String ip,int port)
	{
		int len = loginListeners.size();
		for(int i=0;i<len;i++)
		{
			loginListeners.get(i).login(id, name, ip, port);
		}
	}
	
	public void fireMessageListeners(String type,long id,String ip,int port,String info)
	{
		int len = messageListeners.size();
		for(int i=0;i<len;i++)
		{
			messageListeners.get(i).newMessage(type, id, ip, port,info);
		}
	}
	
	public void fireTickListeners(String tickinfo)
	{
		int len = tickListeners.size();
		for(int i=0;i<len;i++)
		{
			tickListeners.get(i).tick(tickinfo);
		}
	}
	
	public void fireReciveEventListeners(String type,byte[] data)
	{
		int len = tickListeners.size();
		for(int i=0;i<len;i++)
		{
			recieveEventListeners.get(i).RecieveEvent(type, data);
		}
	}
	
	public void run()
	{
		while(running)
		{
			try {
				buffer = new byte[1024];
				packet = new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);
				String recStr = new String(buffer).trim();
				
				if(recStr.startsWith("login"))
				{
					String info = recStr.split(":")[1];
					String[] loginInfo = info.split("\\,");
					long id = Long.parseLong(loginInfo[0]);
					String name = loginInfo[1];
					String ip = loginInfo[2];
					int port = Integer.parseInt(loginInfo[3].trim());
					fireLoginListeners(id,name,ip,port);
				}
				if(recStr.startsWith("logfail"))
				{
					long id = Long.parseLong(recStr.split(":")[1].trim());
					fireLogFailListeners(id);
				}
				if(recStr.startsWith("logout"))
				{
					long id = Long.parseLong(recStr.split(":")[1]);
					fireLogoutListeners(id);
				}
				if(recStr.startsWith("tick"))
				{
					String tickInfo = recStr.split(":")[1].trim();
					fireTickListeners(tickInfo);
				}
				if(recStr.startsWith("newMessage"))//newMessage:type,id,ip,port,info
				{
					int index = recStr.indexOf(":");
					String recc = recStr.substring(index+1).trim();
					String[] str = recc.split("\\,");
					String type = str[0];
					long id = Long.parseLong(str[1]);
					String ip = str[2];
					int port = Integer.parseInt(str[3]);
					int len = str.length;
					String info;
					if(len>5)
					{
						StringBuilder builder = new StringBuilder();
						for(int i=4;i<len-1;i++)
						{
							builder.append(str[i]+",");
						}
						builder.append(str[len-1]);
						info = builder.toString();
					}
					else
					{
						info = str[4];
					}
					fireMessageListeners(type,id,ip,port,info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addLogfailListener(LogfailListener listener)
	{
		logfailListeners.add(listener);
	}
	
	public void fireLogFailListeners(final long id)
	{
		new Thread(new Runnable()
		{

			public void run()
			{
				int len = logfailListeners.size();
				for(int i=0;i<len;i++)
				{
					logfailListeners.get(i).logFail(id);
				}				
			}
			
		}).start();
	}

}
