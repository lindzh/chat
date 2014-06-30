package lin.xidian.file.data;

import java.util.HashMap;
import java.util.Map;

import lin.xidian.file.message.MessageType.ProtocolKey;
import lin.xidian.utils.ChatByteUtils;

public class FileRecieveProtocolWrapper
{
	public static Map recieveFileTick(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] port = new byte[4];
		int echoLength = data.length - 20;
		byte[] echo = new byte[echoLength];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, time, 0, 8);
		System.arraycopy(data, 16, port, 0, 4);
		System.arraycopy(data, 20, echo, 0, echo.length);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.PORT, ChatByteUtils.bytesToInt(port));
		map.put(ProtocolKey.ECHO_ID, ChatByteUtils.bytesToLong(echo));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveFileTickAck(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] port = new byte[4];
		int echoLength = data.length - 20;
		byte[] echo = new byte[echoLength];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, time, 0, 8);
		System.arraycopy(data, 16, port, 0, 4);
		System.arraycopy(data, 20, echo, 0, echo.length);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.PORT, ChatByteUtils.bytesToInt(port));
		map.put(ProtocolKey.ECHO_ACK_ID, ChatByteUtils.bytesToLong(echo));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveSendFileRequest(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] port = new byte[4];
		byte[] fileId = new byte[8];
		byte[] size = new byte[8];
		int fileNameLength = data.length - 36;
		byte[] fileName = new byte[fileNameLength];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, cmd, 0, 8);
		System.arraycopy(data, 16, port, 0, 4);
		System.arraycopy(data, 20, fileId, 0, 8);
		System.arraycopy(data, 28, size, 0, 8);
		System.arraycopy(data, 36, fileName, 0, fileNameLength);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.PORT, ChatByteUtils.bytesToInt(port));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.FILE_SIZE, ChatByteUtils.bytesToLong(size));
		map.put(ProtocolKey.FILE_NAME, ChatByteUtils.bytesToSting(fileName));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	
	public static Map recieveSendFileRequestAckYes(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] port = new byte[4];
		byte[] fileId = new byte[8];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, cmd, 0, 8);
		System.arraycopy(data, 16, port, 0, 4);
		System.arraycopy(data, 20, fileId, 0, 8);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.PORT, ChatByteUtils.bytesToInt(port));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveSendFileRequestAckNo(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] fileId = new byte[8];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, cmd, 0, 8);
		System.arraycopy(data, 16, fileId, 0, 8);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveFileData(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] fileId = new byte[8];
		byte[] id = new byte[8];
		byte[] mark = new byte[8];
		int len = data.length-40;
		byte[] datas = new byte[len];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, time, 0, 8);
		System.arraycopy(data, 16, fileId, 0, 8);
		System.arraycopy(data, 24, id, 0, 8);
		System.arraycopy(data, 32, mark, 0, 8);
		System.arraycopy(data, 40, datas, 0, len);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.ID, ChatByteUtils.bytesToLong(id));
		map.put(ProtocolKey.MARK, ChatByteUtils.bytesToLong(mark));
		map.put(ProtocolKey.DATA, ChatByteUtils.bytesToLong(datas));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveFileDataAck(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] fileId = new byte[8];
		byte[] id = new byte[8];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, time, 0, 8);
		System.arraycopy(data, 16, fileId, 0, 8);
		System.arraycopy(data, 24, id, 0, 8);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.ID, ChatByteUtils.bytesToLong(id));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveFileDataReSend(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] fileId = new byte[8];
		byte[] id = new byte[8];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, time, 0, 8);
		System.arraycopy(data, 16, fileId, 0, 8);
		System.arraycopy(data, 24, id, 0, 8);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.ID, ChatByteUtils.bytesToLong(id));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
	
	public static Map recieveSendFileCancel(byte [] data)
	{
		Map map = new HashMap();
		byte[] cmd = new byte[8];
		byte[] time = new byte[8];
		byte[] fileId = new byte[8];
		System.arraycopy(data, 0, cmd, 0, 8);
		System.arraycopy(data, 8, time, 0, 8);
		System.arraycopy(data, 16, fileId, 0, 8);
		map.put(ProtocolKey.CMD, ChatByteUtils.bytesToLong(cmd));
		map.put(ProtocolKey.FILE_ID, ChatByteUtils.bytesToLong(fileId));
		map.put(ProtocolKey.VERSION, ChatByteUtils.bytesToLong(time));
		return map;
	}
}
