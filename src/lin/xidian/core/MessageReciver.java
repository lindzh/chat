package lin.xidian.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * addListeners set init start
 * @author lindia
 *
 */
public class MessageReciver extends MessageReciverBase implements ChatFrameCloseListener
{

	private Wrapper wrapper;
	private DatagramSocket socket;
	private boolean running = false;
	
	private List<FinishListener> finishListeners = new ArrayList<FinishListener>();
	private List<RecieveEventListener> recieveEventListeners = new ArrayList<RecieveEventListener>();
	private List<LogoutListener> logoutListeners = new ArrayList<LogoutListener>();
	private List<TickListener> tickListeners = new ArrayList<TickListener>();
	private List<MessageListener> messageListeners = new ArrayList<MessageListener>();
	private List<ChatBuildListener> chatBuildListeners = new ArrayList<ChatBuildListener>();
	private List<ChatCloseListener> chatCloseListeners = new ArrayList<ChatCloseListener>();
	
	private List<SendFileListener> sendFileListeners = new ArrayList<SendFileListener>();
	private List<ReciveFileListener> reciveFileListeners = new ArrayList<ReciveFileListener>();
	private List<FileCancelListener> fileCancelListeners = new ArrayList<FileCancelListener>();
	private List<StartSendListener> startSendListeners = new ArrayList<StartSendListener>();
	private List<StartReciveListener> startReciveListeners = new ArrayList<StartReciveListener>();
	
	private List<AudioStopListener> audioStopListeners = new ArrayList<AudioStopListener>();
	private List<AudioStartListener> audioStartListeners = new ArrayList<AudioStartListener>();
	private List<AudioReplyListener> audioReplyListeners = new ArrayList<AudioReplyListener>();
	
	private List<VideoStartListener> videoStartListeners = new ArrayList<VideoStartListener>();
	private List<VideoStopListener> videoStopListeners = new ArrayList<VideoStopListener>();
	private List<VideoReplyListener> videoReplyListeners = new ArrayList<VideoReplyListener>();
	
	public void addFinishListener(FinishListener listener)
	{
		finishListeners.add(listener);
	}

	public void addRecieveEventListener(RecieveEventListener listener)
	{
		recieveEventListeners.add(listener);
	}
	
	@Override
	public void addLogoutListener(LogoutListener listener)
	{
		logoutListeners.add(listener);		
	}
	
	@Override
	public void addMessageListener(MessageListener listener)
	{
		messageListeners.add(listener);
	}
	
	@Override
	public void addChatCloseListener(ChatCloseListener listener)
	{
		chatCloseListeners.add(listener);
	}

	public void addTickListener(TickListener listener)
	{
		tickListeners.add(listener);
	}
	
	@Override
	public void addChatBuildListener(ChatBuildListener listener)
	{
		chatBuildListeners.add(listener);	
	}

	//设置一下协议
	public void fireChatCloseListeners(final long id)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = chatCloseListeners.size();
				for(int i=0;i<len;i++)
				{
					chatCloseListeners.get(i).chatClose(id);
				}
			}
		}).start();
	}
	
	public void fireTickListeners(final String tickInfo)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = tickListeners.size();
				for(int i=0;i<len;i++)
				{
					tickListeners.get(i).tick(tickInfo);
				}
			}
		}).start();
	}
	
	public void fireMessageListeners(final String message)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = messageListeners.size();
				for(int i=0;i<len;i++)
				{
					messageListeners.get(i).message(message);
				}
			}
		}).start();
	}
	
	public void fireChatBuildListeners(final long id,final String ip,final int port)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = chatBuildListeners.size();
				for(int i=0;i<len;i++)
				{
					chatBuildListeners.get(i).chatBuild(id, ip, port);
				}
			}
		}).start();

	}
	
	public void init()
	{
		socket = wrapper.getDatagramSocket();
	}

	public void setWrapper(Wrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	public void start()
	{
		running = true;
		new Thread(this).start();
	}

	public void stop()
	{
		running = false;
		socket = null;
		wrapper = null;
		recieveEventListeners = null;
		finishListeners = null;
		logoutListeners = null;
		tickListeners = null;
	}

	public void run()
	{
		byte[] buffer;
		DatagramPacket packet;
		while(running)
		{
			try {
				buffer = new byte[1024];
				packet = new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);
				String recStr = new String(buffer).trim();
				
				System.out.println(recStr);
				
				if(recStr.startsWith("message"))
				{
					int index = recStr.indexOf(":");
					String str = recStr.substring(index+1);
					fireMessageListeners(str);
				}
				if(recStr.startsWith("tick"))
				{
					String tickInfo = recStr.split(":")[1].trim();
					fireTickListeners(tickInfo);
				}
				if(recStr.startsWith("chatBuild"))//chatBuild:id,ip,port
				{
					String[] rst = recStr.split(":")[1].trim().split("\\,");
					long id = Long.parseLong(rst[0]);
					String ip = rst[1];
					int port = Integer.parseInt(rst[2]);
					fireChatBuildListeners(id,ip,port);
				}
				if(recStr.startsWith("chatClose"))//chatClose:id
				{
					String rst = recStr.split(":")[1].trim();
					long id = Long.parseLong(rst);
					fireChatCloseListeners(id);
				}
				if(recStr.startsWith("sendFile"))//对方给我发送文件sendFile:id,name,size,port
				{
					String[] rst = recStr.split(":")[1].split("\\,");
					long id = Long.parseLong(rst[0]);
					String name = rst[1];
					long size = Long.parseLong(rst[2]);
					int port = Integer.parseInt(rst[3]);
					fireSendFileListeners(id,name,size,port);
				}
				if(recStr.startsWith("reciveFile"))//对方是否同意接收文件reciveFile:id,state,port
				{
					String[] rst = recStr.split(":")[1].split("\\,");
					long id = Long.parseLong(rst[0]);
					String state = rst[1];
					int port = Integer.parseInt(rst[2]);
					fireReciveFileListeners(id,state,port);
				}
				if(recStr.startsWith("cancelFile"))//对方终止文件(发送，还是接收，由state决定)cancelFile:id,state
				{
					String[] rst = recStr.split(":")[1].split("\\,");
					long id = Long.parseLong(rst[0]);
					String state = rst[1];
					fireFileCancelListeners(id,state);
				}
				if(recStr.startsWith("startRecive"))//对方开始发送文件startRecive:id
				{
					String rst = recStr.split(":")[1];
					long id = Long.parseLong(rst);
					fireStartReciveListeners(id);
				}
				if(recStr.startsWith("startSend"))//对方开始接收文件startSend:id
				{
					String rst = recStr.split(":")[1];
					long id = Long.parseLong(rst);
					fireStartSendListeners(id);
					
					System.out.println("对方开始接收");
				}
				if(recStr.startsWith("startAudio"))
				{
					String rst = recStr.split(":")[1];
					long id = Long.parseLong(rst);
					fireAudioStartListeners(id);
				}
				if(recStr.startsWith("stopAudio"))
				{
					String rst = recStr.split(":")[1];
					long id = Long.parseLong(rst);
					fireAudioStopListeners(id);
				}
				if(recStr.startsWith("replyAudio"))
				{
					String[] rst = recStr.split(":")[1].split("\\,");
					long id = Long.parseLong(rst[0]);
					String state = rst[1];
					fireAudioReplyListeners(id,state);
				}
				
				if(recStr.startsWith("startVideo"))
				{
					long id = Long.parseLong(recStr.split(":")[1]);
					fireVideoStartListeners(id);
				}
				if(recStr.startsWith("stopVideo"))
				{
					long id = Long.parseLong(recStr.split(":")[1]);
					fireVideoStopListeners(id);
				}
				if(recStr.startsWith("replyVideo"))
				{
					String[] rst = recStr.split(":")[1].split("\\,");
					long id = Long.parseLong(rst[0]);
					fireVideoReplyListeners(id,rst[1]);
				}
				//其他监听器
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void chatFrameClose(long id, long myid)
	{
		
	}

	//-------------------------------------------------------------------
	@Override
	public void addFileCancelListener(FileCancelListener listener)
	{
		fileCancelListeners.add(listener);
	}

	public void fireFileCancelListeners(final long id,final String state)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = fileCancelListeners.size();
				for(int i=0;i<len;i++)
				{
					fileCancelListeners.get(i).cancelFile(id, state);
				}
			}
		}).start();
	}
	
	@Override
	public void addReciveFileListener(ReciveFileListener listener)
	{
		reciveFileListeners.add(listener);
	}

	public void fireReciveFileListeners(final long id,final String state,final int port)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = reciveFileListeners.size();
				for(int i=0;i<len;i++)
				{
					reciveFileListeners.get(i).reciveEvent(id, state,port);
				}
			}
		}).start();

	}
	
	@Override
	public void addSendFileListener(SendFileListener listener)
	{
		sendFileListeners.add(listener);
	}

	public void fireSendFileListeners(final long id,final String name,final long size,final int port)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = sendFileListeners.size();
				for(int i=0;i<len;i++)
				{
					sendFileListeners.get(i).sendFileEvent(id, name, size,port);
				}
			}
		}).start();
	}
	
	@Override
	public void addStartReciveListener(StartReciveListener listener)
	{
		startReciveListeners.add(listener);
	}

	public void fireStartReciveListeners(final long id)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = startReciveListeners.size();
				for(int i=0;i<len;i++)
				{
					startReciveListeners.get(i).startReciveEvent(id);
				}
			}
		}).start();
	}
	
	@Override
	public void addStartSendListener(StartSendListener listener)
	{
		startSendListeners.add(listener);
	}
	
	public void fireStartSendListeners(final long id)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = startSendListeners.size();
				for(int i=0;i<len;i++)
				{
					startSendListeners.get(i).startSendEvent(id);
				}
			}
		}).start();
	}

	@Override
	public void addAudioReplyListener(AudioReplyListener listener)
	{
		audioReplyListeners.add(listener);		
	}

	public void fireAudioReplyListeners(final long id,final String state)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = audioReplyListeners.size();
				for(int i=0;i<len;i++)
				{
					audioReplyListeners.get(i).audioReply(id, state);
				}
			}
		}).start();

	}
	
	@Override
	public void addAudioStartListener(AudioStartListener listener)
	{
		audioStartListeners.add(listener);		
	}

	public void fireAudioStartListeners(final long destId)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = audioStartListeners.size();
				for(int i=0;i<len;i++)
				{
					audioStartListeners.get(i).audioStart(destId);
				}
			}
		}).start();
	}
	
	@Override
	public void addAudioStopListener(AudioStopListener listener)
	{
		audioStopListeners.add(listener);		
	}
	
	public void fireAudioStopListeners(final long destId)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = audioStopListeners.size();
				for(int i=0;i<len;i++)
				{
					audioStopListeners.get(i).audioStop(destId);
				}
			}
		}).start();
	}

	@Override
	public void addVideoReplyListener(VideoReplyListener listener)
	{
		videoReplyListeners.add(listener);
	}

	@Override
	public void addVideoStartListener(VideoStartListener listener)
	{
		videoStartListeners.add(listener);		
	}

	@Override
	public void addVideoStopListener(VideoStopListener listener)
	{
		videoStopListeners.add(listener);
	}
	
	private void fireVideoReplyListeners(final long id,final String state)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = videoReplyListeners.size();
				for(int i=0;i<len;i++)
				{
					videoReplyListeners.get(i).videoReplyEvent(id, state);
				}
			}
		}).start();
	}
	
	private void fireVideoStartListeners(final long id)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = videoStartListeners.size();
				for(int i=0;i<len;i++)
				{
					videoStartListeners.get(i).videoStartEvent(id);
				}
			}
		}).start();
	}
	
	private void fireVideoStopListeners(final long id)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int len = videoStopListeners.size();
				for(int i=0;i<len;i++)
				{
					videoStopListeners.get(i).videoStopEvent(id);
				}
			}
		}).start();
	}
}
