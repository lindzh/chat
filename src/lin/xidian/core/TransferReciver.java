package lin.xidian.core;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import lin.xidian.utils.Constant;
import lin.xidian.utils.InetUtils;

public class TransferReciver extends TransferReciverBase
{
	private Wrapper wrapper;
	private DatagramSocket socket;
	private List<FinishListener> finishListeners = new ArrayList<FinishListener>();
	private List<RecieveEventListener> recieveEventListeners = new ArrayList<RecieveEventListener>();
	private boolean isReciving = false;
	private String saveFileName = "";
	private long fileLength = 0;
	private long recivedLength = 0;
	private long id = 0;
	
	public void addFinishListener(FinishListener listener)
	{
		finishListeners.add(listener);
	}

	public void addRecieveEventListener(RecieveEventListener listener)
	{
		recieveEventListeners.add(listener);	
	}

	public void fireFinishListeners()//利用ID的方式通知
	{
		int len = finishListeners.size();
		for(int i=0;i<len;i++)
		{
			finishListeners.get(i).finish("recive", String.valueOf(id));
		}	
	}
	
	public void fireRecieveEventListeners(final byte[] data)//通知接收到了数据
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = recieveEventListeners.size();
				for(int i=0;i<len;i++)
				{
					recieveEventListeners.get(i).RecieveEvent(String.valueOf(id), data);
				}				
			}
			
		}).start();
	}
	
	public void addTickListener(TickListener listener)
	{
		
	}

	public void init()
	{
		this.socket = wrapper.getDatagramSocket();
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	public void start()
	{
		
	}

	public void stop()
	{
		if(isReciving)
		{
			isReciving = false;
		}
		else
		{
			isReciving = false;
			fileLength = 0;
			recivedLength = 0;
			saveFileName = "";
			id = 0;
			//removeFinishListeners();
			//removeReciveEventListeners();
		}
	}

	public void run()
	{
		System.out.println("now ReciveTime   "+InetUtils.getLocalIp()+":"+socket.getLocalPort());
		File file = new File(saveFileName);
		
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			isReciving = true;
			byte[] buffer;
			DatagramPacket packet;
			int len = 1024*50;//长度必须一致
			while(isReciving&&recivedLength<fileLength)
			{
				buffer = new byte[1024*50];//50kb
				packet = new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);
				if(recivedLength+buffer.length>fileLength)
				{
					len = (int)(fileLength-recivedLength);
				}
				fos.write(buffer, 0,len);
				fireRecieveEventListeners(buffer);
				recivedLength+=len;
			}
			//文件已发送完毕或者强制退出
			fos.close();
			if(isReciving)//正常退出
			{
				fireFinishListeners();
			}
			else
			{
				if(recivedLength<fileLength)
				{
					file.delete();
				}
			}
			//初始化设置
			isReciving = false;
			fileLength = 0;
			recivedLength = 0;
			saveFileName = "";
			id = 0;
			//removeFinishListeners();
			//removeReciveEventListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeFinishListeners()
	{
		finishListeners.clear();
	}

	@Override
	public void removeReciveEventListeners()
	{
		recieveEventListeners.clear();
	}

	@Override
	public void reciveFile(TransFile file)
	{
		id = file.getId();
		this.fileLength = file.getSize();
		recivedLength = 0;
		saveFileName = Constant.FILE_PATH+file.getName();
		boolean isFileOk = false;
		int num = 1;
		while(!isFileOk)
		{
			File file1 = new File(saveFileName);
			if(!file1.exists())
			{
				break;
			}
			saveFileName = Constant.FILE_PATH+num+file.getName();
			num++;
		}
		System.out.println("开始接收文件:"+saveFileName);
		new Thread(this).start();
		
	}

}
 