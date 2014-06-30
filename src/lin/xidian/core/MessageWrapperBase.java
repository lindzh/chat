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
	
	//�����������,����ļ�����ʱ�Ŀ�����Ϣ
	//���Transfer�ļ�֧��
	public abstract void sendFile(TransFile file,int port);
	public abstract void reciveFile(long id,String state,int port);//state�����֣�һ����yes(ͬ�����)��һ����no�������գ�
	public abstract void cancelFile(long id,String state);//state����״̬,send�жԷ�ȡ������,һ����recive���жԷ�ȡ������
	public abstract void startSend(long id);
	public abstract void startRecive(long id);
	
	public abstract void addSendFileListener(SendFileListener listener);
	public abstract void addReciveFileListener(ReciveFileListener listener);
	public abstract void addFileCancelListener(FileCancelListener listener);
	public abstract void addStartSendListener(StartSendListener listener);
	public abstract void addStartReciveListener(StartReciveListener listener);
	//=====================================================
	
	//����
	public abstract void startAudio();//������������
	public abstract void stopAudio();//������������
	public abstract void replyAudio(String state);//�ظ�ͬ���������ǲ�ͬ��
	
	//��Ƶ
	public abstract void startVideo();
	public abstract void stopVideo();
	public abstract void replyVideo(String state);
}
