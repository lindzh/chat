package lin.xidian.core;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * setw addlisteners init setport sendFile,����stopǿ�ƽ��� 
 * @author lindia
 *
 */
public class TransferSender extends TransferSenderBase implements Runnable
{
	private DatagramSocket socket;
	private String ip = "";
	private int port = 0;
	private InetAddress address;
	private Wrapper wrapper;
	private List<FinishListener> finishListeners = new ArrayList<FinishListener>();
	private List<SendEventListener> sendEventListeners = new ArrayList<SendEventListener>();
	private boolean isSending = false;
	private String filePathName = "";
	private long fileLength = 0;
	private long hasSended = 0;
	private long id = 0;
	
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
		this.socket = wrapper.getDatagramSocket();
		ip = wrapper.getDestIp();
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void send(String type, byte[] data)
	{
		
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	public void startTick()
	{
		
	}

	public void stop()//ǿ�ƽ���
	{
		if(isSending)
		{
			isSending = false;
		}
		else
		{
			isSending = false;
			filePathName = "";
			fileLength = 0;
			hasSended = 0;
			//removeFinishListeners();//ÿ����������Ҫ��ӣ�������������
			//removeSendEventListeners();
		}
		//��������
	}

	@Override
	public void removeFinishListeners()
	{
		finishListeners.clear();		
	}

	@Override
	public void removeSendEventListeners()
	{
		sendEventListeners.clear();
	}

	public void fireSendEventListeners(byte[] data)
	{
		int len = sendEventListeners.size();
		for(int i=0;i<len;i++)
		{
			sendEventListeners.get(i).sendEvent(String.valueOf(id), data);
		}
	}
	
	public void fireFinishListeners()
	{
		int len = finishListeners.size();
		for(int i=0;i<len;i++)
		{
			finishListeners.get(i).finish("send", String.valueOf(id));
		}
	}
	
	public void run()
	{
		File file = new File(filePathName);
	
		System.out.println("now:sendTime   "+ip+":"+port);
		
		if(file.exists())
		{
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] buffer;
				DatagramPacket packet;
				int rst = 0;
				isSending = true;
				while(isSending&&hasSended<fileLength)
				{
					 buffer = new byte[1024*50];//200k
					 rst = fis.read(buffer, 0, buffer.length);
					 if(rst!=-1)
					 {
						 packet = new DatagramPacket(buffer,rst,address,port);
						 socket.send(packet);
						 hasSended+=rst;
						 Thread.currentThread().sleep(10);
						fireSendEventListeners(buffer);
					 }
				}
				fis.close();
				if(isSending)//��������
				{
					isSending = false;
					fireFinishListeners();//����Ҳ��Ҫ֪ͨ
				}
				else//����������,�ر�
				{
					if(hasSended<fileLength)
					{
						///
					}
				}
				isSending = false;
				filePathName = "";
				fileLength = 0;
				hasSended = 0;
				//removeFinishListeners();
				//removeSendEventListeners();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendFile(TransFile file)
	{
		id = file.getId();
		this.filePathName = file.getPath()+file.getName();
		System.out.println("�����ļ�·��:"+file.getPath());
		System.out.println("�����ļ�����:"+file.getName());
		fileLength = file.getSize();
		System.out.println("�����ļ�ȫ��:"+filePathName);
		System.out.println("�����ļ���С:"+fileLength);
		hasSended = 0;
		//��������
		new Thread(this).start();
	}

	@Override
	public void setDestPort(int port)
	{
		this.port = port;
		System.out.println("���ö˿�");
	}
}
