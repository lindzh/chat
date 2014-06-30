package lin.xidian.core;

public abstract class MessageWrapperBase implements Wrapper,TickListener,ChatCloseListener
{
	public abstract void setManager(ManagerBase manager);
	public abstract ManagerBase getManager();
	public abstract void sendMessage(String message);
	public abstract void closeChat();
	public abstract void buildChat();
	public abstract void setDestMIp(String ip);
	public abstract void setDestMPort(int port);
	public abstract int getBestPort();
	
	//添加其他方法,添加文件发送时的控制信息
	//添加Transfer文件支持
	public abstract void sendFile(TransFile file,int port);
	public abstract void reciveFile(long id,String state,int port);//state有两种，一个是yes(同意接收)，一个是no（不接收）
	public abstract void cancelFile(long id,String state);//state两种状态,send叫对方取消发送,一个是recive，叫对方取消接收
	public abstract void startSend(long id);
	public abstract void startRecive(long id);
	
	public abstract void addSendFileListener(SendFileListener listener);
	public abstract void addReciveFileListener(ReciveFileListener listener);
	public abstract void addFileCancelListener(FileCancelListener listener);
	public abstract void addStartSendListener(StartSendListener listener);
	public abstract void addStartReciveListener(StartReciveListener listener);
	//=====================================================
	
	//语音
	public abstract void startAudio();//主动启动语音
	public abstract void stopAudio();//主动结束语音
	public abstract void replyAudio(String state);//回复同意语音还是不同意
	
	//视频
	public abstract void startVideo();
	public abstract void stopVideo();
	public abstract void replyVideo(String state);
}
