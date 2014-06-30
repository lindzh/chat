package lin.xidian.newcore.file;

import java.util.ArrayList;
import java.util.List;

import com.lindia.net.core.cache.Data;
import com.lindia.net.core.listener.NetReceivedListener;
import com.lindia.net.core.listener.NetStatusListener;
import com.lindia.net.core.sender.PacketSizeHolder;
import com.lindia.net.core.socket.NarqSocketBase;

public class StableFileSocket extends NarqSocketBase
{

	private List<NetReceivedListener> neteceivedListeners = new ArrayList<NetReceivedListener>();
	private  List<NetStatusListener> netStatusListeners = new ArrayList<NetStatusListener>();
	private PacketSizeHolder sizeHolder;
	
	public void setPacketSizeHolder(PacketSizeHolder sizeHolder)
	{
		this.sizeHolder = sizeHolder;
	}
	
	public void addNetReceivedListener(NetReceivedListener listener)
	{
		neteceivedListeners.add(listener);
	}
	
	public void addNetStatusListener(NetStatusListener listener)
	{
		netStatusListeners.add(listener);
	}
	
	public void onReceived(Data arg0)
	{
		for(NetReceivedListener listener:neteceivedListeners)
		{
			listener.onReceived(arg0);
		}
	}

	
	public void adjustPacketSize(int arg0)
	{
		sizeHolder.adjustPacketSize(arg0);
	}

	public int getPacketSize()
	{
		return sizeHolder.getPacketSize();
	}

	public void onStatus(boolean arg0, long arg1, boolean arg2)
	{
		for(NetStatusListener listener:netStatusListeners)
		{
			listener.onStatus(arg0, arg1, arg2);
		}
	}
}
