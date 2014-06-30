package lin.xidian.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransferEngine implements FinishListener,ReciveFileListener,FileCancelListener,StartSendListener,StartReciveListener
{
	private TransferWrapperBase transfer;
	private MessageWrapperBase messager;
	
	private Map<Long,TransFile> sendFiles = new HashMap<Long,TransFile>();
	private Map<Long,TransFile> reciveFiles = new HashMap<Long,TransFile>();
	private long isSending = 0;
	private long isReciving = 0;//MessageWrapper�������˼�����
	
	public void setMesageWrapper(MessageWrapperBase messager)
	{
		messager.addReciveFileListener(this);
		messager.addFileCancelListener(this);
		messager.addStartSendListener(this);
		messager.addStartReciveListener(this);
		this.messager = messager;
	}
	
	public void setDestPort(int port)
	{
		transfer.setDestPort(port);
	}
	
	public void setTransferWrapper(TransferWrapperBase transfer)
	{
		transfer.addSendFinishListener(this);
		transfer.addReciveFinishListener(this);
		this.transfer = transfer;
	}
	//=============================================================================
	public void removeReciveFinishListeners()
	{
		transfer.removeReciveFinishListeners();
	}
	
	public void removeReciveEventListeners()
	{
		transfer.removeReciveEventListeners();
	}
	public void addRecieveEventListener(RecieveEventListener listener)
	{
		transfer.addRecieveEventListener(listener);
	}
	
	public void addReciveFinishListener(FinishListener listener)
	{
		transfer.addReciveFinishListener(listener);
	}
	
	public void addSendEventListener(SendEventListener listener)
	{
		transfer.addSendEventListener(listener);
	}
	
	public void addSendFinishListener(FinishListener listener)
	{
		transfer.addSendFinishListener(listener);
	}
	public void removeSendFinishListeners()
	{
		transfer.removeSendFinishListeners();
	}
	public void removeSendEventListeners()
	{
		transfer.removeSendEventListeners();
	}
	
	//=============================================================================
	
	public void reciveFile(long id,String name,long size,String state)//���������
	{
		if(state.equals("yes"))
		{
			TransFile file = new TransFile(id,"",name,size);
			reciveFiles.put(id, file);
			transfer.addReciveFile(id, file);
		}
		messager.reciveFile(id, state,transfer.getDatagramSocket().getLocalPort());
	}
	
	public void cancelTransfer(long id,String state)//������ȡ��
	{
		messager.cancelFile(id, state);
		if(state.equals("send"))
		{
			sendFiles.remove(id);
			transfer.cancelSend(id);
			isSending = 0;
			sendNext();
			//������һ��
		}
		if(state.equals("recive"))
		{
			reciveFiles.remove(id);
			transfer.cancelRecive(id);
			isReciving = 0;
		}
	}
	
	public void sendFile(long id,TransFile file)//�����������ļ�
	{
		messager.sendFile(file,transfer.getDatagramSocket().getLocalPort());
		sendFiles.put(id, file);
	}
	
	public void reciveEvent(long id, String state,int port)//�Է��Ƿ�ͬ�����
	{
		if(state.equals("yes"))
		{
			transfer.addSendFile(id, sendFiles.get(id));
			transfer.setDestPort(port);
			if(isSending == 0)
			{
				messager.startSend(id);
			}
		}
		if(state.equals("no"))
		{
			sendFiles.remove(id);
		}
		
	}
	
	public void cancelFile(long id, String state)//�Է�ȡ��
	{
		if(state.equals("send"))
		{
			transfer.cancelRecive(id);
			reciveFiles.remove(id);
			isReciving = 0;
		}
		if(state.equals("recive"))
		{
			transfer.cancelSend(id);
			sendFiles.remove(id);
			isSending = 0;
			sendNext();
		}
	}
	
	public void startSendEvent(long id)//�Է���ʼ����
	{
		System.out.println("Engine:�Է���ʼ����");
		if(isReciving == 0)
		{
			isReciving = id;
			transfer.reciveFile(id);
			messager.startRecive(id);
		}
	}
	
	public void startReciveEvent(long id)//�Է���ʼ����
	{
		if(isSending == 0)
		{
			isSending = id;
			transfer.sendFile(id);
		}
	}
	
	public void sendNext()
	{
		Set<Long> keys = sendFiles.keySet();
		long id = 0;
		for(long key:keys)
		{
			if(id == 0)
			{
				id = key;
			}
			else
			{
				if(sendFiles.get(id).getSize()>sendFiles.get(key).getSize())
				{
					id = key;
				}
			}
		}
		if(id!=0)
		{
			messager.startSend(id);
		}
	}
	
	public void finish(String type, String file)//�ҷ��ͻ��߽��ս���
	{
		if(type.equals("send"))
		{
			System.out.println("���ͽ���");
			long id = Long.parseLong(file);
			sendFiles.remove(id);
			isSending = 0;
			sendNext();
		}
		if(type.equals("recive"))
		{
			System.out.println("���ܽ���");
			long id = Long.parseLong(file);
			reciveFiles.remove(id);
			isReciving = 0;
		}
	}
	
	//�رմ���ʱ�Զ��ر�
	public void close()
	{
		if(isSending!=0)
		{
			cancelTransfer(isSending,"send");
			sendFiles.clear();
		}
		
		if(isReciving!=0)
		{
			cancelTransfer(isReciving,"recive");
			reciveFiles.clear();
		}
		
		if(transfer!=null)
		{
			transfer.stop();
			transfer.getDatagramSocket().close();
		}
	}
}
