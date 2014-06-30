package lin.xidian.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//setWrapper init send
public class ManagerSender implements Sender,Runnable
{
	private List<FinishListener> finishListeners = new ArrayList<FinishListener>();
	private List<SendEventListener> sendEventListeners = new ArrayList<SendEventListener>();
	private Wrapper wrapper;
	private String destIp;
	private int destPort;
	private DatagramSocket socket;
	private boolean running = false;
	private DatagramPacket packet;
	private InetAddress address;
	
	public void addFinishListener(FinishListener listener)
	{
		finishListeners.add(listener);
	}

	public void addSendEventListener(SendEventListener listener)
	{
		sendEventListeners.add(listener);
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	public void send(String type, byte[] data)
	{
		if(running)
		try {
			byte[] buffer = new byte[1024];
			buffer = data;
			packet = new DatagramPacket(buffer,buffer.length,address,destPort);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init()
	{
		try {
			destIp = wrapper.getDestIp();
			destPort = wrapper.getDestPort();
			socket = wrapper.getDatagramSocket();
			address = InetAddress.getByName(destIp);
			running = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void stop()
	{
		running = false;
		/*
		wrapper = null;
		destIp = null;
		destPort = 0;
		socket = null;
		finishListeners = null;
		sendEventListeners = null;
		*/
	}

	public void startTick()
	{
		Thread thread = new Thread(this);
		thread.start();
	}

	public void run()
	{
		String message = "tick:tick";
		DatagramPacket mpacket = new DatagramPacket(message.getBytes(),message.getBytes().length,address,destPort);
		while(running)
		{
			try {
				socket.send(mpacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.currentThread().sleep(15000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
