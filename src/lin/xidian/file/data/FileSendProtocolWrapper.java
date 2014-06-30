package lin.xidian.file.data;

import lin.xidian.cmd.CMDConst;
import lin.xidian.file.Buffer;
import lin.xidian.utils.ChatByteUtils;

public class FileSendProtocolWrapper
{
	public static byte[] wrapeTick(long time,int port,long echoId)
	{
		return ChatByteUtils.getBytes(CMDConst.TICK_REQUEST,time,port,echoId);
	}
	
	public static byte[] wrapeTickAck(long time,int port,long ackEchoId)
	{
		return ChatByteUtils.getBytes(CMDConst.TICK_ACK,time,port,ackEchoId);
	}
	
	public static byte[] wrapeSendFileRequest(long time,int port,long fileId,long size,String fileName)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_REQUEST,time,port,fileId,size,fileName);
	}
	
	public static byte[] wrapSendFileAckYes(long time,int port,long fileId)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_ACK_YES,time,port,fileId);
	}
	
	public static byte[] wrapSendFileAckNo(long time,long fileId)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_ACK_NO,time,fileId);
	}
	
	public static byte[] wrapeSendingData(long time,long fileId,long pieceId,long mark,Buffer data)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_DATA,time,fileId,pieceId,mark,fileId);
	}
	
	public static byte[] wrapeSendingAck(long time,long fileId,long pieceId)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_DATA_ACK,time,fileId,pieceId);
	}
	
	public static byte[] wrapeSendingResend(long time,long fileId,long pieceId)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_DATA_RESEND,time,fileId,pieceId);
	}
	
	public static byte[] wrapeSendingCancel(long time,long fileId)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_CANCEL,time,fileId);
	}
	
	public static byte[] wrapeReceivingCancel(long time,long fileId)
	{
		return ChatByteUtils.getBytes(CMDConst.SEND_FILE_CANCEL,time,fileId);
	}
}
