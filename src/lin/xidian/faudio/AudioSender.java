package lin.xidian.faudio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import lin.xidian.core.FinishListener;
import lin.xidian.core.SendEventListener;
import lin.xidian.core.Sender;
import lin.xidian.core.Wrapper;

/**
 * set init stop  其中init表示启动
 * @author lindia
 *
 */
public class AudioSender implements Sender,Runnable
{
	private Wrapper wrapper;
	private DatagramSocket socket;
	private String destIp;
	private int destPort;
	private InetAddress address;
	
	private TargetDataLine dataLine;
	private boolean isRunning = false;
	
	public void init()
	{
		new Thread(this).start();
	}

	public void send(String type, byte[] data)
	{
		
	}

	public void setWrapper(Wrapper wrapper)
	{
		try {
			this.wrapper = wrapper;
			this.socket = this.wrapper.getDatagramSocket();
			this.destIp = this.wrapper.getDestIp();
			this.destPort = this.wrapper.getDestPort();
			this.address = InetAddress.getByName(destIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void startTick()
	{
		
	}

	public void stop()
	{
		isRunning = false;
	}
	
	public void addFinishListener(FinishListener listener)
	{
		
	}

	public void addSendEventListener(SendEventListener listener)
	{
		
	}

	public void run()
	{
		AudioFormat format = new AudioFormat(8000,16,2,true,true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			dataLine = (TargetDataLine)AudioSystem.getLine(info);
			dataLine.open(format, dataLine.getBufferSize());
			byte[] buffer = new byte[1024];
			int num = 0;
			DatagramPacket packet;
			dataLine.start();
			isRunning = true;
			while(isRunning)
			{
				num = dataLine.read(buffer, 0, 128);
				packet = new DatagramPacket(buffer,num,address,destPort);
				socket.send(packet);
				buffer = new byte[1024];
			}
			dataLine.stop();
			dataLine.close();
			dataLine = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
