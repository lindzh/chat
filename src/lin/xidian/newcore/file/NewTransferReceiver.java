package lin.xidian.newcore.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lin.xidian.core.FinishListener;
import lin.xidian.core.RecieveEventListener;
import lin.xidian.core.TickListener;
import lin.xidian.core.TransFile;
import lin.xidian.core.TransferReciverBase;
import lin.xidian.core.Wrapper;
import lin.xidian.utils.Constant;
import lin.xidian.utils.InetUtils;

import com.lindia.net.core.cache.Data;
import com.lindia.net.core.listener.NetReceivedListener;
import com.lindia.net.core.listener.NetStatusListener;

public class NewTransferReceiver extends TransferReciverBase implements NetReceivedListener,NetStatusListener
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
	
	private RandomAccessFile accessFile;
	private StableFileSocket mysocket;
	
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
		for(FinishListener listener:finishListeners)
		{
			listener.finish("recive",String.valueOf(id));
		}
	}
	
	public void fireRecieveEventListeners(final byte[] data)//通知接收到了数据
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				for(RecieveEventListener listener:recieveEventListeners)
				{
					listener.RecieveEvent(String.valueOf(id), data);
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
		mysocket = SocketFactory.get(socket);
		mysocket.addNetReceivedListener(this);
		mysocket.addNetStatusListener(this);
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
		}
		mysocket.close();
	}

	public void run()
	{
		
	}
	
	private void writeFile(byte[] data,int limit,int offset,long position)
	{
		try
		{
			accessFile.seek(position);
			accessFile.write(data, limit, offset);
		}catch(IOException e)
		{
			try
			{
				accessFile.seek(position);
				accessFile.write(data, limit, offset);
			}catch(IOException e2)
			{
				
			}
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
				try {
					accessFile = new RandomAccessFile(file1,"rw");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			}
			saveFileName = Constant.FILE_PATH+num+file.getName();
			num++;
		}
		System.out.println("开始接收文件:"+saveFileName);
		
		//new Thread(this).start();
		//socket打开
		//TODO
	}

	public void onReceived(Data data)
	{
		byte[] bytes = data.getData();
		Map map = NetFileUtils.getObject(bytes);
	//	map.get
		
		
		byte[] fileData = (byte[])map.get("data");
		writeFile(fileData,fileData.length,0,(Long)map.get("position"));
		fireRecieveEventListeners(fileData);
		recivedLength += bytes.length;
		if(recivedLength>=fileLength)
		{
			fireFinishListeners();
			isReciving = false;
			fileLength = 0;
			recivedLength = 0;
			saveFileName = "";
			id = 0;
			try {
				accessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onStatus(boolean arg0, long arg1, boolean arg2)
	{
		System.out.println("网络中断");
		
	}

}
