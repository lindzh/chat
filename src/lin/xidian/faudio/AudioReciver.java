package lin.xidian.faudio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import lin.xidian.core.FinishListener;
import lin.xidian.core.RecieveEventListener;
import lin.xidian.core.Reciver;
import lin.xidian.core.TickListener;
import lin.xidian.core.Wrapper;

public class AudioReciver implements Reciver
{
	private Wrapper wrapper;
	private DatagramSocket socket;
	private SourceDataLine dataLine;
	private boolean isRunning = false;
	
	public void addFinishListener(FinishListener listener)
	{
		
	}

	public void addRecieveEventListener(RecieveEventListener listener)
	{
		
	}

	public void addTickListener(TickListener listener)
	{
		
	}

	public void init()
	{
		new Thread(this).start();
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;
		this.socket = this.wrapper.getDatagramSocket();
	}

	public void start()
	{
		
	}

	public void stop()
	{
		isRunning = false;
	}

	public void run()
	{
		AudioFormat format =new AudioFormat(8000,16,2,true,true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(format, 16384);
			byte[] buffer;
			DatagramPacket packet;
			dataLine.start();
			isRunning = true;
			while(isRunning)
			{
		     buffer = new byte[1024];//此处数组的大小跟实时性关系不大，可根据情况进行调整 
		     packet = new DatagramPacket(buffer,buffer.length);
		     socket.receive(packet);
		     dataLine.write(buffer, 0,1024); 
			}
			dataLine.stop();
			dataLine.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

}
