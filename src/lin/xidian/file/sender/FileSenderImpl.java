package lin.xidian.file.sender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import lin.xidian.file.Buffer;
import lin.xidian.file.KeyGenerater;
import lin.xidian.file.data.Data;
import lin.xidian.file.data.FileSendProtocolWrapper;
import lin.xidian.file.listener.EventListener;
import lin.xidian.timer.FileTimer;
import lin.xidian.utils.IDGenerator;


public class FileSenderImpl extends FileSender implements Runnable{	

	private List<EventListener> startListeners;
	private List<EventListener> sendingListeners;
	private List<EventListener> endListeners;
	private InputStream ins = null;
	private boolean fileEnd = false;
	private boolean canceled = false;
	private FileTimer timer = new FileTimer();
	private int pieceLength = 1024*200;
	private int MaxPieceLength = 1024*1024;
	private long hasReaded = 0;
	private int windowSize = 20;
	private int MaxWindowSize = 20;
	private long speed = 1;
	
	@Override
	public void cancel() {
		
		canceled = true;
	}

	@Override
	public void start() throws FileNotFoundException{
		
		canceled = false;
		fileEnd = false;
		timer.setSender(this);
		timer.setFrequency(10);
		timer.start();
		String fileName = getConfig().getFile();
		File file = new File(fileName);
		ins = new FileInputStream(file);
		new Thread(this).start();
	}

	@Override
	public void close() {
		if(!fileEnd)
			canceled = true;
			timer.cancel();
	}

	@Override
	public void addSendStartListener(EventListener listener) {
		startListeners.add(listener);
	}

	@Override
	public void addSendEndListener(EventListener listener) {
		endListeners.add(listener);
	}

	@Override
	public void addSendingListener(EventListener listener) {
		sendingListeners.add(listener);
	}
	
	public void fireEvent(long id,String type,Object things,int sendCode)
	{
		List<EventListener> listeners = null;
		if(sendCode == SendCode.STAR)
		{
			listeners = startListeners;
		}else if(sendCode == SendCode.SENDING){
			listeners = sendingListeners;
		}else if(sendCode == SendCode.END)
		{
			listeners = endListeners;
		}
		if(listeners!=null)
		for(EventListener listener:listeners)
		{
			listener.onEvent(id, type, things);
		}
	}

	private void read() throws IOException
	{
		if(getSize()>windowSize)
		{
			//fa song bao cun
		}
		int len = pieceLength;
		byte[] buffer = new byte[len];
		int length = ins.read(buffer);
		if(length>0)
		{
			long time = System.currentTimeMillis();
			long mark = IDGenerator.generateId();
			long fileId = getConfig().getFileId();
			byte[] buf = new byte[length];
			System.arraycopy(buffer, 0, buf, 0, length);
			Buffer buff = new Buffer(buf,length);
			byte[] toSend = FileSendProtocolWrapper.wrapeSendingData(time, fileId, hasReaded, mark, buff);
			String key = KeyGenerater.generateKey(toSend);
			Data data = new Data();
			data.setData(buf, length);
			data.setTime(1);
			data.setReceived(false);
			super.put(key, data);
			timer.add(buf, length);
			hasReaded += len;
		}else{
			fileEnd = true;
		}

	}
	
	interface SendCode{
		int STAR = 0;
		int SENDING = 1;
		int END = 2;
	}

	public void run()
	{
		while(!fileEnd&&canceled)
		{	
			try {
				read();                                                                                           
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		try {
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onEvent(long type, Map data)//接收反馈事件
	{
		//调整发送大小和窗口大小
		String key = KeyGenerater.generateKey(type,data);
		Data sdata = super.get(key);
		long dataLength = sdata.getLength();
		long transTime = System.currentTimeMillis()-sdata.getSendTime();
		long nspeed = dataLength/transTime;
		if(nspeed>speed)
		{
			int time = (int)(nspeed/speed);
			if(MaxWindowSize>windowSize)
			{
				windowSize = windowSize*time>MaxWindowSize? MaxWindowSize:windowSize*time;
			}
			if(MaxPieceLength>pieceLength)
			{
				pieceLength = pieceLength*time>MaxPieceLength? MaxPieceLength:pieceLength*time;
				
			}
			speed = nspeed;
		}
		super.remove(key);
	}
}
