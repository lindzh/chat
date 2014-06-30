package lin.xidian.core;

import java.net.DatagramSocket;

import lin.xidian.utils.DatagramSocketFactory;
import lin.xidian.utils.InetUtils;


//setProperty setSender setReciver init start
/**
 * ����ʵ��MainFrameCloseListener�ӿ� ϵͳ�����ڹر��¼��ӿ�
 * ����ʹ������IP��Port��һ��ΪManager��һ��ΪMessage
 */
public class MessageWrapper extends MessageWrapperBase implements ChatBuildListener
{
	private static final String type = "message";
	private DatagramSocket socket;
	private MessageSenderBase sender;
	private MessageReciverBase reciver;
	private long destId;
	private String destName;
	private String destIp;
	private int destPort;
	private String destMIp;
	private int destMPort;
	private ManagerBase manager;
	
	public MessageWrapper()
	{
		socket = DatagramSocketFactory.getInstance().createDatagramSocket(type);
	}
	
	@Override
	public void sendMessage(String message)
	{
		if(destMPort != 0)//�ͻ����ѽ���
		{
			sender.sendMessage("message:"+message);
		}
		else//newMessage:type,id,ip,port,info   ���͵�Manager��ʹ��NewMessageListener��ʽ����
		{
			String ms = "newMessage:message,"+manager.getUserId()+","+InetUtils.getLocalIp()+","+socket.getLocalPort()+","+message;
			sender.send("message", ms.getBytes());
		}
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
		return getBestPort();
	}

	public void init()
	{
		reciver.init();
		sender.init();
	}

	public void setDestId(long id)
	{
		this.destId = id;
	}

	public void setDestIp(String ip)
	{
		this.destIp = ip;
	}

	public void setDestName(String name)
	{
		this.destName = name;
	}

	public void setDestPort(int port)
	{
		destPort = port;
	}

	public void setReciver(Reciver reciver)
	{
		this.reciver = (MessageReciverBase)reciver;
		this.reciver.addTickListener(this);
		this.reciver.addChatCloseListener(this);
		this.reciver.addChatBuildListener(this);
		this.reciver.setWrapper(this);
		//�������������
	}

	public void setSender(Sender sender)
	{
		this.sender = (MessageSenderBase)sender;
		this.sender.setWrapper(this);
	}

	public void start()
	{
		reciver.start();
	}

	public void startTick()
	{
		sender.startTick();
	}

	public void stop()
	{
		sender.stop();
		reciver.stop();
	}

	public void tick(String tickinfo)
	{
		System.out.println("tick:"+tickinfo);
		//�����Դ˲鿴�û��Ƿ�����,�������ڷ���������
	}

	//�Է����ڽ����¼�
	public void chatBuild(long id, String ip, int port)//һ��ֻ��port�ı�
	{
		//�����ҵķ���Ŀ�ĵ�
		if(this.getDestId() == id)
		{
			destMIp = ip;
			destMPort = port;
			sender.setDestPort(getBestPort());
		}
	}
	
	//�Է����ڹر��¼�
	public void chatClose(long id)
	{
		if(id == destId)
		{
			destMPort = 0;
			sender.setDestPort(getBestPort());
		}
	}
	
	@Override
	public void setDestMIp(String ip)
	{
		destMIp = ip;
	}

	@Override
	public void setDestMPort(int port)
	{
		destMPort = port;
	}

	@Override
	public int getBestPort()
	{
		if(destMPort!=0)
		{
			return destMPort;
		}
		return destPort;
	}

	@Override
	public void buildChat()
	{
		if(destMPort != 0)//�ͻ����ѽ���
		{
			String message = "chatBuild:"+manager.getUserId()+","+InetUtils.getLocalIp()+","+socket.getLocalPort();
			sender.send("chatBuild", message.getBytes());
		}
		else
		{
			String message = "newMessage:chatBuild,"+manager.getUserId()+","+InetUtils.getLocalIp()+","+socket.getLocalPort()+",**";
			sender.send("chatBuild", message.getBytes());
		}
	}

	@Override
	public void closeChat()
	{
		if(destMPort != 0)//�ͻ����ѽ���
		{
			String message = "chatClose:"+manager.getUserId();
			sender.send("chatBuild", message.getBytes());
		}
		else
		{
			String message = "newMessage:chatClose,"+manager.getUserId()+","+InetUtils.getLocalIp()+",0,**";
			sender.send("chatBuild", message.getBytes());
		}
	}

	@Override
	public void setManager(ManagerBase manager)
	{
		this.manager = manager;		
	}

	@Override
	public ManagerBase getManager()
	{
		return manager;
	}

	@Override
	public void cancelFile(long id, String state)
	{
		String message = "cancelFile:"+id+","+state;
		sender.send("cancelFile", message.getBytes());
	}

	@Override
	public void reciveFile(long id, String state,int port)
	{
		String message = "reciveFile:"+id+","+state+","+port;
		sender.send("reciveFile", message.getBytes());
	}

	@Override
	public void sendFile(TransFile file,int port)
	{
		String message = "sendFile:"+file.getId()+","+file.getName()+","+file.getSize()+","+port;
		sender.send("sendFile", message.getBytes());
	}

	@Override
	public void startRecive(long id)
	{
		String message = "startRecive:"+id;
		sender.send("startRecive", message.getBytes());
	}

	@Override
	public void startSend(long id)
	{
		String message = "startSend:"+id;
		sender.send("startRecive", message.getBytes());
	}

	@Override
	public void addFileCancelListener(FileCancelListener listener)
	{
		reciver.addFileCancelListener(listener);
	}

	@Override
	public void addReciveFileListener(ReciveFileListener listener)
	{
		reciver.addReciveFileListener(listener);
	}

	@Override
	public void addSendFileListener(SendFileListener listener)
	{
		reciver.addSendFileListener(listener);
		System.out.println("�����");
	}

	@Override
	public void addStartReciveListener(StartReciveListener listener)
	{
		reciver.addStartReciveListener(listener);
	}

	@Override
	public void addStartSendListener(StartSendListener listener)
	{
		reciver.addStartSendListener(listener);
	}

	@Override
	public void startAudio()//startAudio:id,ip,port //audio:id,type,port type/yes no
	{
		String message = "startAudio:"+manager.getUserId();
		sender.send("startAudio", message.getBytes());
	}

	@Override
	public void stopAudio()//stopAudio
	{
		String message = "stopAudio:"+manager.getUserId();
		sender.send("stopAudio", message.getBytes());
	}

	@Override
	public void replyAudio(String state)
	{
		String message = "replyAudio:"+manager.getUserId()+","+state;
		sender.send("replyAudio", message.getBytes());
	}

	@Override
	public void replyVideo(String state)
	{
		String message = "replyVideo:"+manager.getUserId()+","+state;
		sender.send("replyVideo", message.getBytes());
	}

	@Override
	public void startVideo()
	{
		String message = "startVideo:"+manager.getUserId();
		sender.send("startVideo", message.getBytes());
	}

	@Override
	public void stopVideo()
	{
		String message = "stopVideo:"+manager.getUserId();
		sender.send("stopVideo", message.getBytes());
	}
}
