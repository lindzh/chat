package lin.xidian.file.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import lin.xidian.file.deliver.Deliver;

public abstract class FileReciverBase implements Receive,Runnable
{
	private DatagramSocket socket;
	private Deliver deliver;
	private boolean isReceived = false;
	private boolean isCanceled = false;
	private int MaxPieceLength = 1024*1024;
	
	public void init()
	{
		
	}

	public void start()
	{
		
		new Thread(this).start();
	}

	public void close()
	{
		
	}

	public void setDeliver(Deliver deliver)
	{
		this.deliver = deliver;
	}

	public void run()
	{
		while(!isReceived&&!isCanceled)
		{
			byte[] buffer = new byte[MaxPieceLength];
			DatagramPacket packet = new DatagramPacket(buffer,MaxPieceLength);
			try {
				socket.receive(packet);
				int len = 0;
				deliver.delive(buffer, len);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	

}
