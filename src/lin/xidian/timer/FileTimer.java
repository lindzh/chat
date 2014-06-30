package lin.xidian.timer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import lin.xidian.file.Buffer;
import lin.xidian.file.sender.Sender;

public class FileTimer//¶¨Ê±·¢ËÍ
{
	public FileTimer()
	{
		
	}
	private long frequency;
	private Timer timer = new Timer("file data");
	private Queue queue;
	private Sender sender;
	
	public void setSender(Sender sender)
	{
		this.sender = sender;
		queue = new LinkedList();
	}
	
	public boolean add(byte[] data,int length) 
	{
		if(queue.size()>20)
		{
			return false;
		}
		Buffer buf = new Buffer(data,length);
		return queue.offer(buf);
	}
	
	public Buffer get()
	{
		return (Buffer)queue.poll();
	}
	
	public void start()
	{
		timer.schedule(new TimerTask(){
			public void run()
			{
				Buffer buf = get();
				if(buf!=null&&buf.getLength()>0)
				{
					try {
						sender.send(buf.getBuffer(),buf.getLength());
					} catch (IOException e) {
						try {
							sender.send(buf.getBuffer(),buf.getLength());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
		},System.currentTimeMillis(), frequency);
	}
	
	public void setFrequency(long frequency)
	{
		this.frequency = frequency;
	}
	
	public void adjust(long time)
	{
		frequency = time;
		timer.cancel();
		timer.schedule(new TimerTask(){
			public void run()
			{
				Buffer buf = get();
				if(buf!=null&&buf.getLength()>0)
				{
					try {
						sender.send(buf.getBuffer(),buf.getLength());
					} catch (IOException e) {
						try {
							sender.send(buf.getBuffer(),buf.getLength());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
		},System.currentTimeMillis(), frequency);
	}
	
	public void cancel()
	{
		timer.cancel();
	}
}
