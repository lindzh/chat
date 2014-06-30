package lin.xidian.core;

public abstract class MessageReciverBase implements Reciver
{
	public abstract void addLogoutListener(LogoutListener listener);
	public abstract void addMessageListener(MessageListener listener);
	public abstract void addChatCloseListener(ChatCloseListener listener);
	public abstract void addChatBuildListener(ChatBuildListener listener);
	
	//添加文件发送事件监听器===============================
	public abstract void addSendFileListener(SendFileListener listener);
	public abstract void addReciveFileListener(ReciveFileListener listener);
	public abstract void addFileCancelListener(FileCancelListener listener);
	public abstract void addStartSendListener(StartSendListener listener);
	public abstract void addStartReciveListener(StartReciveListener listener);
	//=================================================
	
	//语音
	public abstract void addAudioReplyListener(AudioReplyListener listener);
	public abstract void addAudioStartListener(AudioStartListener listener);
	public abstract void addAudioStopListener(AudioStopListener listener);
	
	//视频
	public abstract void addVideoStartListener(VideoStartListener listener);
	public abstract void addVideoStopListener(VideoStopListener listener);
	public abstract void addVideoReplyListener(VideoReplyListener listener);
	
}
