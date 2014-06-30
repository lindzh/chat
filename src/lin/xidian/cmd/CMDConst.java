package lin.xidian.cmd;

public interface CMDConst
{
	static final long TICK_REQUEST = 0x99997777;   //心跳包发送
	static final long TICK_ACK = 0x99990000;       //心跳包确认
	static final long SEND_FILE_REQUEST = 0x11110000; //发送文件命令
	static final long SEND_FILE_ACK_YES = 0x11111111; //同意接收文件
	static final long SEND_FILE_ACK_NO = 0x11112222;  //拒绝接收文件
	static final long SEND_FILE_DATA = 0x11113333;
	static final long SEND_FILE_DATA_ACK = 0x11114444;
	static final long SEND_FILE_DATA_RESEND = 0x11115555;
	static final long SEND_FILE_CANCEL = 0x11116666;
}
