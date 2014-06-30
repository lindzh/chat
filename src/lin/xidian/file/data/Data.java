package lin.xidian.file.data;

public class Data
{
	private boolean isReceived = false;
	private int time;
	private byte [] data;
	private int sendTime;
	private int length;
	
	public boolean isReceived()
	{
		return isReceived;
	}
	public void setReceived(boolean isReceived)
	{
		this.isReceived = isReceived;
	}
	public synchronized int getTime()
	{
		return time;
	}
	public synchronized void incTime()
	{
		time++;
	}
	
	public void setTime(int _time)
	{
		time = _time;
	}
	
	public byte[] getData()
	{
		return data;
	}
	public void setData(byte[] data,int length)
	{
		this.data = data;
		this.length = length;
	}
	
	public void incrSendTime()
	{
		sendTime++;
	}
	
	public int getSendTime()
	{
		return sendTime;
	}
	public int getLength()
	{
		return length;
	}
	
	
	
	
	
}
