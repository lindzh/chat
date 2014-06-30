package lin.xidian.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import lin.xidian.audio.AudioEvent;
import lin.xidian.audio.AudioEventListener;
import lin.xidian.audio.AudioStartEvent;
import lin.xidian.audio.RTPAudioWrapper;
import lin.xidian.audio.RTPAudioWrapperBase;
import lin.xidian.core.AudioCancelListener;
import lin.xidian.core.AudioReplyListener;
import lin.xidian.core.AudioStartListener;
import lin.xidian.core.AudioStopListener;
import lin.xidian.core.ChatCloseListener;
import lin.xidian.core.ChatFrameCloseListener;
import lin.xidian.core.FileCancelListener;
import lin.xidian.core.FinishListener;
import lin.xidian.core.LogoutListener;
import lin.xidian.core.MainFrameCloseListener;
import lin.xidian.core.MessageListener;
import lin.xidian.core.MessageWrapperBase;
import lin.xidian.core.RecieveEventListener;
import lin.xidian.core.ReciveFileListener;
import lin.xidian.core.SendEventListener;
import lin.xidian.core.SendFileListener;
import lin.xidian.core.TransFile;
import lin.xidian.core.TransferEngine;
import lin.xidian.core.TransferReciver;
import lin.xidian.core.TransferReciverBase;
import lin.xidian.core.TransferSender;
import lin.xidian.core.TransferSenderBase;
import lin.xidian.core.TransferWrapper;
import lin.xidian.core.TransferWrapperBase;
import lin.xidian.core.VideoCancelListener;
import lin.xidian.core.VideoReplyListener;
import lin.xidian.core.VideoStartListener;
import lin.xidian.core.VideoStopListener;
import lin.xidian.newcore.file.NewFileReceiver;
import lin.xidian.newcore.file.NewFileSender;
import lin.xidian.utils.Constant;
import lin.xidian.utils.IDUtils;
import lin.xidian.utils.InetUtils;
import lin.xidian.video.VideoEvent;
import lin.xidian.video.VideoEventListener;
import lin.xidian.video.VideoStartEvent;
import lin.xidian.video.VideoVisualEvent;
import lin.xidian.video.VideoWrapper;
import lin.xidian.video.VideoWrapperBase;

/**
 * 
 * @author lindia
 * ������ߺ͹رմ���ͼ��,��Ҫ���ǽ��Լ����ļ����߶Է����ն˿�
 */
public class ChatFrame extends JFrame implements ActionListener,ChatCloseListener,MessageListener,LogoutListener,
SendFileListener,ReciveFileListener,FileCancelListener,FinishListener,SendEventListener,RecieveEventListener,
MainFrameCloseListener,AudioStartListener,AudioStopListener,AudioReplyListener,AudioCancelListener,AudioEventListener,
VideoStartListener,VideoStopListener,VideoReplyListener,VideoEventListener,VideoCancelListener
{
	//==============================================================
	private String userName;
	private String oppName;
	private MessageWrapperBase messageWrapper = null;
	private TransferWrapperBase transferWrapper = null;
	private TransferEngine transEngine = new TransferEngine();

	private Map<Long,TransPanel> transferFiles = new HashMap<Long,TransPanel>();//���֣����ͻ��ǽ��ն��в�ͬ
	private JPanel transFilesPane = new JPanel();
	private JScrollPane transPane = new JScrollPane();//�ļ��������
	private boolean inited = false;
	
	private RTPAudioWrapperBase audioEngine;
	private JTabbedPane tabbed = new JTabbedPane();
	private AudioPanel audioPanel;
	
	private VideoWrapperBase videoEngine;
	private VideoPanel videoPanel;
	
	private boolean isTabbedOK = false;
	private boolean isTransferRunning = false;
	private boolean isAudioRunning = false;
	private boolean isVedioRunning = false;//Ԥ���ӿ�
	private boolean isVideoStart = false;
	private boolean isAudioStart = false;
	
	private List<ChatFrameCloseListener> chatFrameCloseListeners = new ArrayList<ChatFrameCloseListener>();
	
	//============================================================
	private JButton transferFile = new JButton();
	private JButton transferVoice = new JButton();
	private JButton transferVideo = new JButton();
	private String imgPath = Constant.ROOT_DIR+"\\image\\";
	
	private JPanel framePanel = new JPanel();
	private JPanel bodyPanel = new JPanel();//���,����ļ����ͣ����գ��������������
	private JPanel showPanel = new JPanel();
	
	private JLabel oppShowLabel = new JLabel();
	private JLabel myShowLabel = new JLabel();
	
	private JTextArea reciveText = new JTextArea(13,15);
	private JTextArea sendText = new JTextArea(4,15);
	private JButton sendButton = new JButton("����");
	
	private WindowAdapter exitAdapter = new WindowAdapter()
	{
		@Override        //�����رմ����¼�
		public void windowClosing(WindowEvent we)
		{
			int choose = JOptionPane.showConfirmDialog(ChatFrame.this,"��ȷ���رմ�����?","�ر���ʾ",JOptionPane.YES_NO_OPTION);
			if(choose == JOptionPane.NO_OPTION)
			{
				
			}
			if(choose == JOptionPane.YES_OPTION)
			{
				dispose();
			}
			/*
			else
			{
				ChatFrame.this.setVisible(true);
				System.out.println("�㰴��0");
			}
			*/
		}
	};
	
	public ChatFrame()
	{
		
	}
	
	public void setMessageWrapper(MessageWrapperBase wrapper)
	{
		this.messageWrapper = wrapper;
		this.userName = messageWrapper.getManager().getUserName();
		this.oppName = messageWrapper.getDestName();
		System.out.println("IP:"+InetUtils.getLocalIp()+" "+messageWrapper.getDatagramSocket().getLocalPort());
	}
	
	public void initGUI()
	{
		
		JToolBar toolbar = new JToolBar();
		transferVoice.setIcon(new ImageIcon(imgPath+"svoice.png"));
		transferVideo.setIcon(new ImageIcon(imgPath+"svedio.png"));
		transferFile.setIcon(new ImageIcon(imgPath+"sfile.png"));
		toolbar.add(transferVoice);
		toolbar.add(transferVideo);
		toolbar.add(transferFile);
		toolbar.setFloatable(false);
		
		JPanel messagePanel = new JPanel();
		reciveText.setEditable(false);
		reciveText.setLineWrap(true);
		reciveText.setFont(new Font("����",Font.BOLD,15));
		sendText.setLineWrap(true);
		sendText.setFont(new Font("����",Font.BOLD,15));
		JScrollPane recivePane = new JScrollPane();
		JScrollPane sendPane = new JScrollPane();
		recivePane.setViewportView(reciveText);
		recivePane.setBorder(BorderFactory.createTitledBorder("Chat Recieve"));
		sendPane.setViewportView(sendText);
		sendPane.setBorder(BorderFactory.createTitledBorder("Chat Recieve"));
		
		JPanel btnPanel = new JPanel();
		sendButton.addActionListener(this);
		btnPanel.add(sendButton);
		btnPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		messagePanel.setLayout(new BoxLayout(messagePanel,BoxLayout.Y_AXIS));
		messagePanel.add(recivePane);
		messagePanel.add(sendPane);
		messagePanel.add(btnPanel);
		
		ImageIcon oppIcon = new ImageIcon(imgPath+"show.png");
		oppShowLabel.setIcon(oppIcon);
		myShowLabel.setIcon(oppIcon);
		showPanel.setLayout(new BoxLayout(showPanel,BoxLayout.Y_AXIS));
		showPanel.add(oppShowLabel);
		showPanel.add(myShowLabel);
		
		bodyPanel.setLayout(new BoxLayout(bodyPanel,BoxLayout.X_AXIS));
		bodyPanel.add(messagePanel);
		
		bodyPanel.add(showPanel);
		
		transFilesPane.setLayout(new BoxLayout(transFilesPane,BoxLayout.Y_AXIS));
		
		framePanel.setLayout(new BorderLayout());
		framePanel.add(toolbar,BorderLayout.NORTH);
		framePanel.add(bodyPanel, BorderLayout.CENTER);
		
		this.add(framePanel);
		this.setBounds(300, 100, 600, 500);
		this.setVisible(true);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(exitAdapter);
		
		transferFile.addActionListener(this);
		transferVoice.addActionListener(this);
		transferVideo.addActionListener(this);
	}

	public void addMessages(List<String> messages)
	{
		int len = messages.size();
		for(int i=0;i<len;i++)
		{
			reciveText.append(oppName+" ˵��"+messages.get(i)+"\r\n");
		}
	}
	
	//�Է������ڹر�
	public void chatClose(long id)
	{
		//����TransferEngine,MessageWrapper���Զ�ת��
		transEngine.close();
		Set<Long> set = transferFiles.keySet();
		for(long fid:set)
		{
			removeFromPanel(fid);
		}
		transferFiles.clear();
		inited = false;
		if(isAudioStart)
		{
			audioEngine.stop();
			audioEngine = null;
			isAudioStart = false;
			audioPanel.stopTimer();
			removeAudioPanel();
			transferVoice.setEnabled(true);
			reciveText.append("System : �Է���������������..\r\n");
		}
		if(isVideoStart)
		{
			videoEngine.stop();
			isVideoStart = false;
			removeVideoPanel();
			videoEngine = null;
			transferVideo.setEnabled(true);
			reciveText.append("System:�Է���ֹ����Ƶ����...\r\n");
		}
	}

	public void message(String message)
	{
		reciveText.append(oppName+" ˵��"+message+"\r\n");
	}

	public void logout(long id)
	{
		
		
	}

	//button�¼�������
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof JButton)
		{
			JButton button = (JButton)e.getSource();
			if(sendButton == button)
			{
				String message = sendText.getText().trim();
				messageWrapper.sendMessage(message);
				reciveText.append(userName+" ˵��"+message+"\r\n");
				sendText.setText("");
			}
			if(button == transferFile)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(null);
				chooser.setAcceptAllFileFilterUsed(true);
				int state = chooser.showOpenDialog(this);
				File selectFile = chooser.getSelectedFile();
				if(selectFile!=null&&state == JFileChooser.APPROVE_OPTION)
				{
					long id = IDUtils.getInstance().getID();
					String name = selectFile.getName();
					int index = selectFile.getPath().indexOf(name);
					String path = selectFile.getPath().substring(0, index);
					long size = selectFile.length();
					TransFile transFile = new TransFile(id,path,name,size);
					sendInit(transFile);
					//����
					TransPanel panel = new TransPanel("send",transFile);
					//��Ӽ�����
					panel.addFileCancelListeners(this);
					addToPanel(id,panel);
					transEngine.sendFile(transFile.getId(), transFile);//���������ļ�
				}
			}
			
			if(button == transferVoice)
			{
				//������������
				//������
				messageWrapper.startAudio();
				audioPanel = new AudioPanel(messageWrapper.getDestId());
				audioPanel.initGUI();
				audioPanel.addAudioCancelListener(this);
				addAudioPanel();
				transferVoice.setEnabled(false);
			}
			
			if(button == transferVideo)
			{
				//������Ϣ
				messageWrapper.startVideo();
				addVideoPanel();
				transferVideo.setEnabled(false);
			}
		}
	}

	public void sendFileEvent(long id, String name, long size,int port)//�����Է����ļ�����
	{
		//��������ѯ��,��������ӵ�����Transfer�У�������ӵ������//yes 0,no 1
		System.out.println("����������ѡ��");
		
		int choose = JOptionPane.showConfirmDialog(this,"����  "+name+" ��","��ѡ������ļ�",JOptionPane.YES_NO_OPTION);
		if(choose == JOptionPane.YES_OPTION)//��ʾ��ͬ����ո��ļ�
		{
			TransFile transFile = new TransFile(id,"",name,size);
			sendInit(transFile);
			TransPanel panel = new TransPanel("recive",transFile);
			//��Ӽ�����
			panel.addFileCancelListeners(this);
			addToPanel(id,panel);
			transEngine.reciveFile(id, name, size, "yes");
			transEngine.setDestPort(port);
			//��ӵ������
			//����

		}
		else
		{
			transEngine.reciveFile(id, name, size, "no");
		}
	}

	public void reciveEvent(long id, String state,int port)//�����Է��Ƿ�Ը������ҷ��͵��ļ�
	{
		if(state.equals("yes"))//��ʾ�Է��Ƿ�Ը����ܸ��ļ�
		{
			//TransEngine���Զ����͸��ļ�
		}
		if(state.equals("no"))
		{
			//�Է���Ը����ܸ��ļ�,�������ɾ��
			removeFromPanel(id);
		}
	}

	public void cancelFile(long id, String state)//�����Է�ȡ�����ļ��Ľ��ܻ���//���ֺ��Լ�ȡ������ʲô��ͬ
	{//����state�Ƿ����self�������Լ�����Opp state send recive; selfsend self recive
		//����в��� �����Լ�������engine����ȡ��
		if(state.equals("selfsend"))//�Լ�ȡ������
		{
			reciveText.append("System : ��ȡ�����ļ� "+transferFiles.get(id).getTransFile().getName()+" �ķ��͡�");
			removeFromPanel(id);
			transEngine.cancelTransfer(id,"send");
		}
		if(state.equals("selfrecive"))//�Լ�ȡ������
		{
			reciveText.append("System : ��ȡ�����ļ� "+transferFiles.get(id).getTransFile().getName()+" �Ľ��ա�");
			removeFromPanel(id);
			transEngine.cancelTransfer(id,"recive");
		}
		if(state.equals("send"))//�Է�ȡ���˷���
		{
			reciveText.append("System : �Է�ȡ�����ļ� "+transferFiles.get(id).getTransFile().getName()+" �ķ��͡�\r\n");
			removeFromPanel(id);
		}
		if(state.equals("recive"))//�Է�ȡ���˽���
		{
			reciveText.append("System : �Է�ȡ�����ļ� "+transferFiles.get(id).getTransFile().getName()+" �Ľ��ա�\r\n");
			removeFromPanel(id);
		}
	}

	public void finish(String type, String file)
	{
		//����в���
		System.out.println("finish");
		long id = Long.parseLong(file);
		if(type.equals("send"))
		{
			String name = transferFiles.get(id).getTransFile().getName();
			reciveText.append("System : �ļ� "+name+" �ѷ��� ..\r\n");
		}
		if(type.equals("recive"))
		{
			String name = transferFiles.get(id).getTransFile().getName();
			reciveText.append("System : �ļ� "+name+" �ѽ���..\r\n");
		}
		removeFromPanel(id);
	}
	
	public void sendEvent(String file, byte[] data)
	{
		long id = Long.parseLong(file);
		int len = data.length;
		if(transferFiles.get(id) == null)
		{
			System.out.println("Send: TransferPanelֵΪNULL");
		}
		else
		{
			transferFiles.get(id).addValue(len);
		}
	}

	public void RecieveEvent(String file, byte[] data)
	{
		long id = Long.parseLong(file);
		int len = data.length;
		
		if(transferFiles.get(id) == null)
		{
			System.out.println("Rec: TransferPanelֵΪNULL");
		}
		else
		{
			transferFiles.get(id).addValue(len);
		}
	}	
	
	public void sendInit(TransFile transFile)
	{
		if(!inited)
		{
//			TransferReciverBase reciver = new TransferReciver();
//			TransferSenderBase sender = new TransferSender();
//			//  2013-4-29
			//-----------------------------------------------
			TransferReciverBase reciver = new NewFileReceiver();
			TransferSenderBase sender = new NewFileSender();
			//---------------------------------------------------
			transferWrapper = new TransferWrapper();
			transferWrapper.setDestIp(messageWrapper.getDestIp());
			transferWrapper.setDestId(transferWrapper.getDestId());
			transferWrapper.setDestName(transferWrapper.getDestName());
			transferWrapper.setSender(sender);
			transferWrapper.setReciver(reciver);
			transferWrapper.setDestPort(0);
			transferWrapper.addSendFinishListener(this);
			transferWrapper.addReciveFinishListener(this);
			transferWrapper.addSendEventListener(this);
			transferWrapper.addRecieveEventListener(this);
			reciver.setWrapper(transferWrapper);
			sender.setWrapper(transferWrapper);
			transferWrapper.init();
			//��Ӹ��ּ�����

			transEngine.setMesageWrapper(messageWrapper);
			transEngine.setTransferWrapper(transferWrapper);
			inited = true;
		}
	}
	
	public void addChatFrameCloseListener(ChatFrameCloseListener listener)
	{
		chatFrameCloseListeners.add(listener);
	}
	
	public void addToPanel(long id,TransPanel panel)//��������size//�ļ�����
	{
		transferFiles.put(id, panel);
		System.out.println("TransPanel�м��룺"+id);
		
		transFilesPane.add(panel);
		transPane.setViewportView(transFilesPane);
		if(!isTabbedOK)
		{
			bodyPanel.remove(showPanel);
			tabbed.add("�ļ�����",transPane);
			isTransferRunning = true;
			bodyPanel.add(tabbed);
			isTabbedOK = true;
			this.setSize(700, 500);
		}
		else
		{
			if(!isTransferRunning)
			{
				tabbed.add("�ļ�����",transPane);
				isTransferRunning = true;
			}
		}
	}
	
	public void removeFromPanel(long id)//��������size//�ļ�����
	{
		transFilesPane.remove(transferFiles.get(id));
		transPane.setViewportView(transFilesPane);
		transferFiles.remove(id);
		if(transferFiles.size()<1)//���û�е������//����
		{
			tabbed.remove(transPane);
			isTransferRunning = false;
			if(!isTransferRunning&&!isAudioRunning&&!isVedioRunning)
			{
				bodyPanel.remove(tabbed);
				bodyPanel.add(showPanel);
				this.setSize(600, 500);
				isTabbedOK = false;
			}
		}
	}
	
	public void mainFrameClose(long userid)
	{
		//���͹ر�ѡ����ʾ
		transEngine.close();
		messageWrapper.closeChat();
		messageWrapper.stop();
		messageWrapper.getDatagramSocket().close();
		ChatFrame.this.dispose();//��һ�·���
		
		if(isAudioStart)
		{
			audioEngine.stop();
			audioEngine = null;
			isAudioStart = false;
			audioPanel.stopTimer();
			removeAudioPanel();
		}
		if(isVideoStart)
		{
			videoEngine.stop();
			isVideoStart = false;
		}
		removeVideoPanel();
		videoEngine = null;
		this.dispose();
	}
	
	//-----------------------------------------------------------
	
	public void addAudioPanel()//��������Ҽ�
	{
		if(!isTabbedOK)
		{
			bodyPanel.remove(showPanel);
			bodyPanel.add(tabbed);
			isTabbedOK = true;
			this.setSize(700, 500);
		}
		if(!isAudioRunning)
		{
			tabbed.add("��������",audioPanel);
			isAudioRunning = true;
		}
	}
	
	public void removeAudioPanel()//�Ƴ������Ҽ�
	{
		if(isAudioRunning)
		{
			tabbed.remove(audioPanel);
			audioPanel = null;
			isAudioRunning = false;
			if(!isTransferRunning&&!isAudioRunning&&!isVedioRunning)
			{
				bodyPanel.remove(tabbed);
				bodyPanel.add(showPanel);
				this.setSize(600, 500);
				isTabbedOK = false;
			}
		}
	}

	public void audioStart(long destId)//�Է���������
	{
		if(destId == messageWrapper.getDestId())
		{
			//��������
			int choose = JOptionPane.showConfirmDialog(this, "�Ƿ�ͬ��Է��������죿","��������",JOptionPane.YES_NO_OPTION);
			if(choose == JOptionPane.YES_OPTION)
			{
				messageWrapper.replyAudio("yes");
				startAudio();
				//������
				audioPanel = new AudioPanel(messageWrapper.getDestId());
				audioPanel.initGUI();
				audioPanel.addAudioCancelListener(this);
				audioPanel.startTimer();
				addAudioPanel();
				transferVoice.setEnabled(false);
			}
			else
			{
				messageWrapper.replyAudio("no");
			}
		}
	}

	public void audioStop(long destId)//�Է���������
	{
		if(destId == messageWrapper.getDestId())
		{
			audioEngine.stop();
			audioEngine = null;
			isAudioStart = false;
			audioPanel.stopTimer();
			removeAudioPanel();
			transferVoice.setEnabled(true);
			reciveText.append("System : �Է���������������..\r\n");
			//������������
			//���ɾ��
		}
	}

	public void audioReply(long id, String state)//�Է��ظ���������
	{
		if(id == messageWrapper.getDestId())
		{
			if(state.equals("yes"))
			{
				//˵���Է�ͬ����������
				startAudio();
				audioPanel.startTimer();
			}
			if(state.equals("no"))
			{
				//˵���Է���ͬ����������
				removeAudioPanel();
				transferVoice.setEnabled(true);
				reciveText.append("System : �Է�û��ͬ�����������������..\r\n");
			}
		}
	}

	public void audioCancel(long destId)//��������
	{
		//������Ϣ
		messageWrapper.stopAudio();
		//��������Ƴ�
		if(isAudioStart)
		{
			audioEngine.stop();
		}
		audioPanel.stopTimer();
		removeAudioPanel();
		transferVoice.setEnabled(true);
		reciveText.append("System : ���������������..\r\n");
	}

	public void audioUpdate(AudioEvent event, String type)
	{
		if(type.equals("send"))
		{
			if(event instanceof AudioStartEvent)
			{
				AudioStartEvent ev = (AudioStartEvent)event;
				if(ev.isSuccess())
				{
					System.out.println(ev.getInfo()+ev.isSuccess());
				}
				else
				{
					reciveText.append("System : "+ev.getInfo()+ev.isSuccess()+"\r\n");
					if(isAudioStart)
					{
						/*
						audioEngine.stop();
						audioEngine = null;
						isAudioStart = false;
						audioPanel.stopTimer();
						removeAudioPanel();
						transferVoice.setEnabled(true);
						*/
					}
				}
			}
		}
		if(type.equals("recive"))
		{
			if(event instanceof AudioStartEvent)
			{
				AudioStartEvent ev = (AudioStartEvent)event;
				if(ev.isSuccess())
				{
					System.out.println(ev.getInfo()+ev.isSuccess());
				}
				else
				{
					reciveText.append("System : "+ev.getInfo()+ev.isSuccess()+"\r\n");
				}
			}
		}
	}
	
	private void startAudio()
	{
		if(!isAudioStart)
		{
			audioEngine = new RTPAudioWrapper();
			audioEngine.setDestSendIp(messageWrapper.getDestIp());
			audioEngine.setDestReciveIp(messageWrapper.getDestIp());
			audioEngine.setSelfSendIp(InetUtils.getLocalIp());
			audioEngine.setSelfReciveIp(InetUtils.getLocalIp());
			audioEngine.setDestSendPort(Constant.audioSendPort);
			audioEngine.setDestRecivePort(Constant.audioRecivePort);
			audioEngine.setSelfSendPort(Constant.audioSendPort);
			audioEngine.setSelfRecivePort(Constant.audioRecivePort);
			audioEngine.init();
			audioEngine.addAudioEventListener(this, "send");
			audioEngine.addAudioEventListener(this, "recive");
			audioEngine.start();
			isAudioStart = true;
		}
	}
	
	//-----------------------------------------------------------------------------
	public void videoStartEvent(long id)
	{
		if(id == messageWrapper.getDestId())//����ǶԷ���������
		{
			int choose = JOptionPane.showConfirmDialog(this, "�Ƿ�ͬ��Է���Ƶ���죿","��Ƶ����",JOptionPane.YES_NO_OPTION);
			if(choose == JOptionPane.YES_OPTION)
			{
				messageWrapper.replyVideo("yes");
				startVideo();
				startAudio();
				//������
				addVideoPanel();
			}
			else
			{
				messageWrapper.replyVideo("no");
			}
		}
	}

	public void videoStopEvent(long id)
	{
		if(id == messageWrapper.getDestId())
		{
			if(isVideoStart)
			{
				videoEngine.stop();
				audioEngine.stop();
				isVideoStart = false;
				isAudioStart = false;
			}
			removeVideoPanel();
			//����Ƴ�
			videoEngine = null;
			audioEngine = null;
			transferVideo.setEnabled(true);
			transferVoice.setEnabled(true);
			reciveText.append("System:�Է���ֹ����Ƶ����...\r\n");
			//��ʾ��Ϣ���
		}
	}

	public void videoReplyEvent(long id, String state)//�ҷ���ȥ���Է��ظ�
	{
		if(id == messageWrapper.getDestId())
		{
			if(state.equals("yes"))//�Է�ͬ�⣬������һ���Ĳ���
			{
				startVideo();
				startAudio();
			}
			if(state.equals("no"))//�Է���ͬ�⣬�Ƴ����
			{
				removeVideoPanel();
				transferVideo.setEnabled(true);
				transferVoice.setEnabled(true);
				reciveText.append("System:�Է��ܾ��������Ƶ��������...\r\n");
			}
		}
	}

	public void videoUpdate(VideoEvent event, String type)//�豸����״̬
	{
		if(type.equals("send"))
		{
			if(event instanceof VideoStartEvent)
			{
				VideoStartEvent vse = (VideoStartEvent)event;
				boolean isOK = vse.getState();
				if(isOK)
				{
					System.out.println(vse.getInfo()+vse.getState());
				}
				else
				{
					reciveText.append("System:��ʼ����Ƶ�����豸ʧ�ܣ�\r\n");
				}
			}
			if(event instanceof VideoVisualEvent)
			{
				VideoVisualEvent vve = (VideoVisualEvent)event;
				videoPanel.addLocalVideo(vve.getVisualComponent());
			}
		}
		
		if(type.equals("recive"))
		{			
			if(event instanceof VideoStartEvent)
			{
				VideoStartEvent vse = (VideoStartEvent)event;
				boolean isOK = vse.getState();
				if(isOK)
				{
					System.out.println(vse.getInfo()+vse.getState());
				}
				else
				{
					reciveText.append("System:��ʼ����Ƶ����ʧ�ܣ�\r\n");
				}
			}
			if(event instanceof VideoVisualEvent)
			{
				VideoVisualEvent vve = (VideoVisualEvent)event;
				videoPanel.addDestVideo(vve.getVisualComponent());
			}
		}
	}

	public void videoCancel(long id)//����ȡ��
	{
		messageWrapper.stopVideo();
		if(isVideoStart)
		{
			videoEngine.stop();
			audioEngine.stop();
			isVideoStart = false;
			isAudioStart = false;
		}
		removeVideoPanel();
		videoEngine = null;
		audioEngine = null;
		transferVideo.setEnabled(true);
		transferVoice.setEnabled(true);
		reciveText.append("System:�Է���ֹ����Ƶ����...\r\n");
	}
	
	public void addVideoPanel()
	{
		videoPanel = new VideoPanel();
		videoPanel.init(messageWrapper.getDestId());
		videoPanel.addVideoCancelListener(this);
		if(!isTabbedOK)
		{
			bodyPanel.remove(showPanel);
			bodyPanel.add(tabbed);
			isTabbedOK = true;
			this.setSize(700, 500);
		}
		if(!isVedioRunning)
		{
			tabbed.add("��Ƶ����",videoPanel);
			isVedioRunning = true;
			isAudioRunning = true;
		}
	}
	
	public void removeVideoPanel()
	{
		if(isVedioRunning)
		{
			tabbed.remove(videoPanel);
			videoPanel = null;
			isVedioRunning = false;
			isAudioRunning = false;
			if(!isTransferRunning&&!isAudioRunning&&!isVedioRunning)
			{
				bodyPanel.remove(tabbed);
				bodyPanel.add(showPanel);
				this.setSize(600, 500);
				isTabbedOK = false;
			}
			videoPanel.stopTimer();
			videoPanel.removeAllAdd();
			videoPanel = null;
		}
	}
	
	private void startVideo()
	{
		if(!isVideoStart)
		{
			videoEngine = new VideoWrapper();
			videoEngine.setDestId(messageWrapper.getDestId());
			videoEngine.setDestIp(messageWrapper.getDestIp());
			videoEngine.setDestName(messageWrapper.getDestName());
			videoEngine.setSelfReciveIp(InetUtils.getLocalIp());
			videoEngine.setSelfSendIp(InetUtils.getLocalIp());
			videoEngine.setDestReciveIp(messageWrapper.getDestIp());
			videoEngine.setDestSendIp(messageWrapper.getDestIp());
			videoEngine.setSelfSendPort(Constant.videoSendPort);
			videoEngine.setSelfRecivePort(Constant.videoRecivePort);
			videoEngine.setDestSendPort(Constant.videoSendPort);
			videoEngine.setDestRecivePort(Constant.videoRecivePort);
			
			videoEngine.init();
			videoEngine.addVideoEventListener(this, "send");
			videoEngine.addVideoEventListener(this, "recive");
			videoEngine.start();
			isVideoStart = true;
		}
	}
	//------------------------------------------------------------------------------------------

	@Override
	public void dispose()
	{				//���͹ر�ѡ����ʾ
		messageWrapper.closeChat();
		transEngine.close();
		messageWrapper.stop();
		messageWrapper.getDatagramSocket().close();
		if(isAudioStart)
		{
			audioEngine.stop();
			audioEngine = null;
			isAudioStart = false;
			audioPanel.stopTimer();
			removeAudioPanel();
		}
		if(isVideoStart)
		{
			videoEngine.stop();
			audioEngine.stop();
		}
		removeVideoPanel();
		audioEngine = null;
		
		int len = chatFrameCloseListeners.size();
		for(int i=0;i<len;i++)
		{
			chatFrameCloseListeners.get(i).chatFrameClose(messageWrapper.getDestId(), messageWrapper.getManager().getUserId());
		}
		super.dispose();
	}

	@Override
	public void hide()
	{
		super.hide();
	}
	
	
}
