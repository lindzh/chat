package lin.xidian.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
/**
 * set add init startTick/send 
 * @author lindia
 *
 */
public class MessageSender extends MessageSenderBase
{
	private Wrapper wrapper;
	private DatagramSocket socket;
	private String destIp;
	private int destPort;
	
	private InetAddress address;
	private DatagramPacket packet;
	
	private boolean running = false;
	
	private List<FinishListener> finishListeners = new ArrayList<FinishListener>();
	private List<SendEventListener> sendEventListeners = new ArrayList<SendEventListener>();
	private boolean isPortChanged = false;
	
	@Override
	public void sendMessage(String message)
	{
		packet = new DatagramPacket(message.getBytes(),message.getBytes().length,address,destPort);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addFinishListener(FinishListener listener)
	{
		finishListeners.add(listener);
	}

	public void addSendEventListener(SendEventListener listener)
	{
		sendEventListeners.add(listener);
	}

	public void init()
	{
		try {
			destIp = wrapper.getDestIp();
			destPort = wrapper.getDestPort();
			address = InetAddress.getByName(destIp);
			socket = wrapper.getDatagramSocket();
			running = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void send(String type, byte[] data)
	{
		//根据发送类型来区分///////////////////////////////////////
		//发送监听器
		packet = new DatagramPacket(data,data.length,address,destPort);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	public void startTick()
	{
		new Thread(this).start();
	}

	public void stop()
	{
		running = false;
		wrapper = null;
		socket = null;
		address = null;
		packet = null;
		finishListeners = null;
		sendEventListeners = null;
	}

	//对方IP和Port重定向可能会影响到Tick
	public void run()
	{
		String tickmsg = "tick:tick";
		DatagramPacket tpacket = new DatagramPacket(tickmsg.getBytes(),tickmsg.getBytes().length,address,destPort);
		while(running)
		{
			try {
				socket.send(tpacket);
				Thread.currentThread().sleep(12000);
				if(isPortChanged)
				{
					tpacket = new DatagramPacket(tickmsg.getBytes(),tickmsg.getBytes().length,address,destPort);
					isPortChanged = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void setDestPort(int port)
	{
		destPort = port;
		isPortChanged = true;
	}
}
