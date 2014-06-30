package lin.xidian.core;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import lin.xidian.utils.DatagramSocketFactory;

/**
 * setip setport setReciver sender 
 * @author lindia
 * 由主窗口发起事件
 */
public class TransferWrapper extends TransferWrapperBase
{
	private DatagramSocket socket;
	private long destId = 0;
	private String destName = "";
	private String destIp = "";
	private int destPort = 0;
	private Map<Long,TransFile> sendFiles = new HashMap<Long,TransFile>();
	private Map<Long,TransFile> reciveFiles = new HashMap<Long,TransFile>();
	private long isSendingFile = 0;
	private long isRecivingFile = 0;
	
	private TransferReciverBase reciver;
	private TransferSenderBase sender;
	
	public TransferWrapper()
	{
		socket = DatagramSocketFactory.getInstance().createDatagramSocket("transfer");
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
		destName = name;		
	}

	public void setDestPort(int port)
	{
		destPort = port;
		sender.setDestPort(destPort);
	}

	public void setReciver(Reciver reciver)
	{
		this.reciver = (TransferReciverBase)reciver;		
	}

	public void setSender(Sender sender)
	{
		this.sender = (TransferSenderBase)sender;
	}

	public void start()
	{
		
	}

	public void startTick()
	{
		//在没有发送的时刻可以发送心跳包,在发送文件是不发送心跳包
	}

	public void stop()
	{
		if(sender!=null)
		{
			isSendingFile = 0;
			sendFiles.clear();
			sender.stop();
		}
		if(reciver!=null)
		{
			isRecivingFile = 0;
			reciveFiles.clear();
			reciver.stop();
		}
	}

	@Override
	public void addReciveFile(long id, TransFile transFile)
	{
		reciveFiles.put(id, transFile);
	}

	@Override
	public void addSendFile(long id, TransFile transFile)
	{
		sendFiles.put(id, transFile);
	}

	@Override
	public void reciveFile(long id)
	{
		isRecivingFile = id;
		System.out.println("进入接收");
		reciver.reciveFile(reciveFiles.get(id));
	}

	@Override
	public void sendFile(long id)
	{
		isSendingFile = id;
		System.out.println("进入发送");
		sender.sendFile(sendFiles.get(isSendingFile));
	}

	@Override
	public void cancelRecive(long id)
	{
		if(isRecivingFile == id)
		{
			isRecivingFile = 0;
			reciver.stop();//强制结束,应该加一个cancel方法
		}
		reciveFiles.remove(id);
	}

	@Override
	public void cancelSend(long id)
	{
		if(isSendingFile == id)
		{
			isRecivingFile = 0;
			sender.stop();//查看一下，对不
		}
		sendFiles.remove(id);
	}

	@Override
	public void addRecieveEventListener(RecieveEventListener listener)
	{
		reciver.addRecieveEventListener(listener);
	}

	@Override
	public void removeReciveEventListeners()
	{
		reciver.removeReciveEventListeners();
	}

	@Override
	public void addReciveFinishListener(FinishListener listener)
	{
		reciver.addFinishListener(listener);
	}

	@Override
	public void removeReciveFinishListeners()
	{
		reciver.removeFinishListeners();
	}

	
	@Override
	public void addSendEventListener(SendEventListener listener)
	{
		sender.addSendEventListener(listener);
	}

	@Override
	public void addSendFinishListener(FinishListener listener)
	{
		sender.addFinishListener(listener);
	}

	@Override
	public void removeSendEventListeners()
	{
		sender.removeSendEventListeners();	
	}

	@Override
	public void removeSendFinishListeners()
	{
		sender.removeFinishListeners();		
	}
}
