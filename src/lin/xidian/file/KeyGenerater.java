package lin.xidian.file;

import java.util.Map;

import lin.xidian.cmd.CMDConst;
import lin.xidian.file.data.FileRecieveProtocolWrapper;
import lin.xidian.file.message.MessageType.ProtocolKey;
import lin.xidian.utils.ChatByteUtils;

public class KeyGenerater
{
	public static String generateKey(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		byte[] cmdbytes = new byte[8];
		System.arraycopy(bytes, 0, cmdbytes, 0, 8);
		long cmd = ChatByteUtils.bytesToLong(cmdbytes);
		if(cmd == CMDConst.TICK_REQUEST)
		{
			Map map = FileRecieveProtocolWrapper.recieveFileTick(bytes);
			sb.append(map.get(ProtocolKey.CMD)+"_");
			sb.append(map.get(ProtocolKey.ECHO_ID)+"_");
			sb.append(map.get(ProtocolKey.VERSION));
		}
		if(cmd == CMDConst.SEND_FILE_REQUEST)
		{
			Map map = FileRecieveProtocolWrapper.recieveSendFileRequest(cmdbytes);
			sb.append(map.get(ProtocolKey.CMD)+"_");
			sb.append(map.get(ProtocolKey.PORT)+"_");
			sb.append(map.get(ProtocolKey.FILE_ID)+"_");
			sb.append(map.get(ProtocolKey.VERSION));
		}
		
		if(cmd == CMDConst.SEND_FILE_CANCEL)
		{
			Map map = FileRecieveProtocolWrapper.recieveSendFileCancel(cmdbytes);
			sb.append(map.get(ProtocolKey.CMD)+"_");
			sb.append(map.get(ProtocolKey.FILE_ID)+"_");
			sb.append(map.get(ProtocolKey.VERSION));
		}
		
		if(cmd == CMDConst.SEND_FILE_DATA)
		{
			Map map = FileRecieveProtocolWrapper.recieveFileData(bytes);
			sb.append(map.get(ProtocolKey.CMD)+"_");
			sb.append(map.get(ProtocolKey.FILE_ID)+"_");
			sb.append(map.get(ProtocolKey.ID)+"_");
			sb.append(map.get(ProtocolKey.VERSION));
		}
		return sb.toString();
	}
	
	public static String generateKey(long type,Map map)
	{
		if(type == CMDConst.SEND_FILE_DATA_ACK)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(map.get(ProtocolKey.CMD)+"_");
			sb.append(map.get(ProtocolKey.FILE_ID)+"_");
			sb.append(map.get(ProtocolKey.ID)+"_");
			sb.append(map.get(ProtocolKey.VERSION));
			return sb.toString();
		}
		
		return "";
	}
}
