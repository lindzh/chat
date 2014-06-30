package lin.xidian.file.deliver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lin.xidian.cmd.CMDConst;
import lin.xidian.file.data.FileRecieveProtocolWrapper;
import lin.xidian.file.listener.FileListener;
import lin.xidian.utils.ChatByteUtils;

public class FileDeliver implements Deliver
{
	private long cmd;
	private Map map;
	
	private List<FileListener> tickAckListeners = new ArrayList<FileListener>();
	private List<FileListener> tickListeners = new ArrayList<FileListener>();
	private List<FileListener> fileRequestListeners = new ArrayList<FileListener>();
	private List<FileListener> fileAckListeners = new ArrayList<FileListener>();
	private List<FileListener> fileDataListeners = new ArrayList<FileListener>();
	private List<FileListener> fileDataAckListeners = new ArrayList<FileListener>();
	private List<FileListener> fileCancelListeners = new ArrayList<FileListener>();
	
	public void delive(byte[] data, int len)
	{
		byte[] cmdb = new byte[8];
		byte[] datas = new byte[len]; 
		System.arraycopy(data, 0, cmdb, 0, 8);
		System.arraycopy(data, 0, datas, 0, len);
		cmd = ChatByteUtils.bytesToLong(cmdb);
		if(cmd == CMDConst.TICK_ACK)
		{
			map = FileRecieveProtocolWrapper.recieveFileTickAck(datas);
		}else if(cmd == CMDConst.TICK_REQUEST)
		{
			map = FileRecieveProtocolWrapper.recieveFileTick(datas);
		}else if(cmd == CMDConst.SEND_FILE_REQUEST)
		{
			map = FileRecieveProtocolWrapper.recieveSendFileRequest(datas);
		}
		else if(cmd == CMDConst.SEND_FILE_ACK_YES)
		{
			map = FileRecieveProtocolWrapper.recieveSendFileRequestAckYes(datas);
		}
		else if(cmd == CMDConst.SEND_FILE_ACK_NO)
		{
			map = FileRecieveProtocolWrapper.recieveSendFileRequestAckNo(datas);
		}
		else if(cmd == CMDConst.SEND_FILE_DATA)
		{
			map = FileRecieveProtocolWrapper.recieveFileData(datas);
		}
		else if(cmd == CMDConst.SEND_FILE_DATA_ACK)
		{
			map = FileRecieveProtocolWrapper.recieveFileDataAck(datas);
		}else if(cmd == CMDConst.SEND_FILE_DATA_RESEND)
		{
			map = FileRecieveProtocolWrapper.recieveFileDataReSend(datas);
		}
		else if(cmd == CMDConst.SEND_FILE_CANCEL)
		{
			map = FileRecieveProtocolWrapper.recieveSendFileCancel(datas);
		}
		invokeListeners();
	}

	public void invokeListeners()
	{
		if(map == null)
		{
			return;
		}
		if(cmd == CMDConst.TICK_ACK)
		{
			fireListeners(tickAckListeners,cmd,map);
		}else if(cmd == CMDConst.TICK_REQUEST)
		{
			fireListeners(tickListeners,cmd,map);
		}else if(cmd == CMDConst.SEND_FILE_REQUEST)
		{
			fireListeners(fileRequestListeners,cmd,map);
		}
		else if(cmd == CMDConst.SEND_FILE_ACK_YES)
		{
			fireListeners(fileAckListeners,cmd,map);	
		}
		else if(cmd == CMDConst.SEND_FILE_ACK_NO)
		{
			fireListeners(fileAckListeners,cmd,map);
		}
		else if(cmd == CMDConst.SEND_FILE_DATA)
		{
			fireListeners(fileDataListeners,cmd,map);
		}
		else if(cmd == CMDConst.SEND_FILE_DATA_ACK)
		{
			fireListeners(fileDataAckListeners,cmd,map);
		}else if(cmd == CMDConst.SEND_FILE_DATA_RESEND)
		{
			fireListeners(fileDataAckListeners,cmd,map);
		}
		else if(cmd == CMDConst.SEND_FILE_CANCEL)
		{
			fireListeners(fileCancelListeners,cmd,map);
		}
	}

	public void addListener(FileListener listener,long type)
	{
		if(type == CMDConst.TICK_ACK)
		{
			tickAckListeners.add(listener);
		}else if(type == CMDConst.TICK_REQUEST)
		{
			tickListeners.add(listener);
		}else if(type == CMDConst.SEND_FILE_REQUEST)
		{
			fileRequestListeners.add(listener);
		}
		else if(type == CMDConst.SEND_FILE_ACK_YES)
		{
			fileAckListeners.add(listener);
		}
		else if(type == CMDConst.SEND_FILE_ACK_NO)
		{
			fileAckListeners.add(listener);
		}
		else if(type == CMDConst.SEND_FILE_DATA)
		{
			fileDataListeners.add(listener);
		}
		else if(type == CMDConst.SEND_FILE_DATA_ACK)
		{
			fileDataAckListeners.add(listener);
		}else if(type == CMDConst.SEND_FILE_DATA_RESEND)
		{
			fileDataAckListeners.add(listener);
		}
		else if(type == CMDConst.SEND_FILE_CANCEL)
		{
			fileCancelListeners.add(listener);
		}
		
	}
	
	private void fireListeners(List<FileListener> listeners,long type,Map map)
	{
		for(FileListener listener:listeners)
		{
			listener.onEvent(type, map);
		}
	}
}
