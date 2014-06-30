package lin.xidian.file.sender;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lin.xidian.file.KeyGenerater;
import lin.xidian.file.config.FileConfig;
import lin.xidian.file.data.Data;
import lin.xidian.file.listener.EventListener;
import lin.xidian.file.listener.FileListener;
import lin.xidian.timer.FileTimer;
import lin.xidian.timer.TimerListener;
import lin.xidian.utils.ChatByteUtils;
import lin.xidian.utils.Constant;

public abstract class FileSender implements Sender,FileListener,TimerListener{
	
	private DatagramSocket sender;
	private FileConfig config;
	private Map<String,Data> dataMap = new HashMap<String,Data>();//已发出的消息保存
	
	public void put(String key,Data value)
	{
		dataMap.put(key, value);
	}
	
	public void remove(String key)
	{
		dataMap.remove(key);
	}
	
	public int getSize()
	{
		return dataMap.size();
	}
	
	public Data get(String key)
	{
		return dataMap.get(key);
	}
	
	public void send(byte[] buffer,int length) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer, length, InetAddress.getByName(config.getDestIp()), config.getDestPort());
		sender.send(packet);
	}
	
	public abstract void cancel();
	
	public abstract void start() throws FileNotFoundException;
	
	public void config(FileConfig config)
	{
		this.config = config;
	}
	
	public FileConfig getConfig() {
		return config;
	}

	public void setSender(DatagramSocket sender)
	{
		this.sender = sender;
	}
	
	public DatagramSocket getSender()
	{
		return sender;
	}
	
	public void onTime(int second)//定时器 1s
	{
		Set<String> keys = dataMap.keySet();
		for(String key:keys)
		{
			Data data = dataMap.get(key);
			if(!data.isReceived())
			{
				data.incTime();
				if(data.getSendTime() == 1)
				{
					if(data.getTime() == Constant.FIRST_WAIT_TIME)
					{
						long version = System.currentTimeMillis();
						byte[] time = ChatByteUtils.longToBytes(version);
						System.arraycopy(time, 0, data.getData(), 8, 8);
						try {
							send(data.getData(),data.getLength());
						} catch (IOException e) {
							e.printStackTrace();
						}
						String nkey = KeyGenerater.generateKey(data.getData());
						data.incrSendTime();
						dataMap.remove(key);
						dataMap.put(nkey, data);
					}
				}
				
				if(data.getSendTime() == 2)
				{
					if(data.getTime() == Constant.SECOND_WAIT_TIME)
					{
						long version = System.currentTimeMillis();
						byte[] time = ChatByteUtils.longToBytes(version);
						System.arraycopy(time, 0, data.getData(), 8, 8);
						try {
							send(data.getData(),data.getLength());
						} catch (IOException e) {
							e.printStackTrace();
						}
						String nkey = KeyGenerater.generateKey(data.getData());
						data.incrSendTime();
						dataMap.remove(key);
						dataMap.put(nkey, data);
					}
				}
				
				if(data.getSendTime() == 3)
				{
					if(data.getTime() == Constant.THIRD_WAIT_TIME)
					{
						long version = System.currentTimeMillis();
						byte[] time = ChatByteUtils.longToBytes(version);
						System.arraycopy(time, 0, data.getData(), 8, 8);
						try {
							send(data.getData(),data.getLength());
						} catch (IOException e) {
							e.printStackTrace();
						}
						String nkey = KeyGenerater.generateKey(data.getData());
						data.incrSendTime();
						dataMap.remove(key);
						dataMap.put(nkey, data);
					}
				}else
				{
					//TODO
					//通知高层，发送失败
				}
			}
		}
	}

	public abstract void close();
	
	public abstract void addSendStartListener(EventListener listener);
	
	public abstract void addSendEndListener(EventListener listener);
	
	public abstract void addSendingListener(EventListener listener);
}
