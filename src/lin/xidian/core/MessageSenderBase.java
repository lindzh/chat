package lin.xidian.core;

public abstract class MessageSenderBase implements Sender,Runnable
{
	public abstract void sendMessage(String message);
	public abstract void  setDestPort(int port);//�Է������ض���ֻ���ö˿�
	//��ӷ��Ϳ�����Ϣ
}
