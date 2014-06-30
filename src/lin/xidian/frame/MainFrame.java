package lin.xidian.frame;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import lin.xidian.core.LoginListener;
import lin.xidian.core.LogoutListener;
import lin.xidian.core.MainFrameCloseListener;
import lin.xidian.core.ManagerBase;
import lin.xidian.core.MessageReciver;
import lin.xidian.core.MessageReciverBase;
import lin.xidian.core.MessageSender;
import lin.xidian.core.MessageSenderBase;
import lin.xidian.core.MessageWrapper;
import lin.xidian.core.MessageWrapperBase;
import lin.xidian.core.NewMessageListener;
import lin.xidian.core.User;

public class MainFrame extends JFrame implements LoginListener,NewMessageListener,LogoutListener
{
	private String imgPath = System.getProperty("user.dir")+File.separator+"image\\";
	private ManagerBase manager;
	private Map<Long,UserLabel> users = new HashMap<Long,UserLabel>();
	
	private List<MainFrameCloseListener> mainFrameCloseListeners = new ArrayList<MainFrameCloseListener>();
	
	private JPanel friends = new JPanel();
	private JPanel groups = new JPanel();
	private JScrollPane friendPanel = new JScrollPane();
	
	private MouseAdapter userAdapterListener = new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent v)
		{
			if(v.getClickCount() > 1)
			{
				UserLabel label = (UserLabel)v.getComponent();
				long id = label.getId();
				startNewChatFrame(id);
			}
		}
		
	};
	
	private WindowAdapter exitAdapter = new WindowAdapter()
	{
		public void windowClosing(WindowEvent arg0)
		{
			int choose = JOptionPane.showConfirmDialog(MainFrame.this, "请问你要退出程序吗", "关闭窗口提示", JOptionPane.YES_NO_OPTION);
			if(choose == JOptionPane.YES_OPTION)
			{
				new Thread(new Runnable()
				{
					public void run()
					{
						manager.logout();
						manager.stop();
						fireMainFrameCloseListeners();
						manager.getDatagramSocket().close();
					}
				}).start();
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MainFrame.this.dispose();
				System.exit(0);
			}
		}
	};
	
	public MainFrame(ManagerBase manager)
	{
		this.manager = manager;
		JPanel headPanel = new JPanel();
		JPanel imgPanel = new JPanel();
		ImageIcon icon = new ImageIcon(imgPath+"logo.png");
		JLabel imgLabel = new JLabel(icon);
		imgPanel.add(imgLabel);		
		JLabel nameLabel = new JLabel(this.manager.getUserName());
		JLabel idLabel = new JLabel(String.valueOf(this.manager.getUserId()));
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
		infoPanel.add(nameLabel);
		infoPanel.add(idLabel);
		headPanel.setLayout(new BoxLayout(headPanel,BoxLayout.X_AXIS));
		headPanel.add(imgPanel);
		headPanel.add(infoPanel);
		headPanel.setBorder(BorderFactory.createTitledBorder("个人信息"));
		
		JTabbedPane tablePane = new JTabbedPane();
		friendPanel.setViewportView(friends);
		JScrollPane groupPanel = new JScrollPane();
		groupPanel.setViewportView(groups);
		tablePane.add("好友", friendPanel);
		tablePane.add("Chat群",groupPanel);
		tablePane.setBorder(BorderFactory.createTitledBorder("chat"));
		friends.setLayout(new BoxLayout(friends,BoxLayout.Y_AXIS));
		/*
		User user = new User(4314000,"张三","222222","123455",1234);
		
		UserPanel panel = new UserPanel(user);
		friends.add(panel);
		UserPanel panel2 = new UserPanel(user);
		friends.add(panel2);
		UserPanel panel3 = new UserPanel(user);
		panel3.startTick();
		friends.add(panel3);
		
		UserLabel ul1 = new UserLabel(user);
		friends.add(ul1);
		UserLabel ul2 = new UserLabel(user);
		friends.add(ul2);
		UserLabel ul3 = new UserLabel(user);
		friends.add(ul3);
		ul3.startTick();
		users.put(new Long(4314000), ul3);
		ul3.addMouseListener(userAdapterListener);
		UserLabel ul4 = new UserLabel(user);
		friends.add(ul4);
		*/
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new BorderLayout());
		framePanel.add(headPanel,BorderLayout.NORTH);
		framePanel.add(tablePane,BorderLayout.CENTER);
		this.add(framePanel);
		this.setBounds(400, 100, 300, 600);
		this.setVisible(true);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(exitAdapter);
		
	}

	public void login(long id, String name, String ip, int port)
	{
		if(!users.containsKey(id)&&manager.getUserId() != id)
		{
			System.out.println("添加用户");
			User user = new User(id,name,"",ip,port);
			UserLabel userLabel = new UserLabel(user);
			userLabel.addMouseListener(userAdapterListener);
			//添加事件响应
			users.put(id, userLabel);
			friends.add(userLabel);
			friendPanel.setViewportView(friends);
			//friendPanel.set
		}
	}
	
	public void newMessage(String type, long num, String ip, int port,
			String info)
	{
		System.out.println("new Message");
		if(type.equals("message"))
		{
			users.get(num).addNewMessage(info);
			users.get(num).setOppMessageSocketAddress(ip, port);
		}
		if(type.equals("chatBuild"))
		{
			users.get(num).setOppMessageSocketAddress(ip, port);
		}
		if(type.equals("chatClose"))
		{
			users.get(num).setOppMessageSocketAddress(ip, 0);
		}
	}

	public void logout(long id)
	{
		users.get(id).logout();//其他
	}
	
	public void requestFriendsAndGroups()
	{
		manager.requestFriendsAndGroups();
	}
	
	public void startNewChatFrame(final long id)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				UserLabel label = users.get(id);
				if(label!=null&&label.canbuildFrame())
				{
					MessageWrapperBase messageWrapper = new MessageWrapper();
					MessageSenderBase messageSender = new MessageSender();
					MessageReciverBase messageReciver = new MessageReciver();
					messageWrapper.setDestIp(label.getOppManagerUser().getIp());
					messageWrapper.setDestId(label.getOppManagerUser().getId());
					messageWrapper.setDestName(label.getOppManagerUser().getName());
					messageWrapper.setDestPort(label.getOppManagerUser().getPort());
					messageWrapper.setDestMIp(label.getOppMessageUser().getIp());
					messageWrapper.setDestMPort(label.getOppMessageUser().getPort());
					messageWrapper.setManager(manager);
					messageSender.setWrapper(messageWrapper);
					messageReciver.setWrapper(messageWrapper);
					messageReciver.addChatCloseListener(messageWrapper);
					messageReciver.addTickListener(messageWrapper);
					messageWrapper.setReciver(messageReciver);
					messageWrapper.setSender(messageSender);
					ChatFrame chatFrame = new ChatFrame();
					messageReciver.addChatBuildListener(label);
					messageReciver.addChatCloseListener(label);
					messageReciver.addChatBuildListener((MessageWrapper)messageWrapper);
					
					messageReciver.addMessageListener(chatFrame);
					messageReciver.addChatCloseListener(chatFrame);
					messageReciver.addLogoutListener(chatFrame);
					
					messageReciver.addAudioReplyListener(chatFrame);
					messageReciver.addAudioStartListener(chatFrame);
					messageReciver.addAudioStopListener(chatFrame);
					
					messageReciver.addVideoStartListener(chatFrame);
					messageReciver.addVideoStopListener(chatFrame);
					messageReciver.addVideoReplyListener(chatFrame);
					
					chatFrame.initGUI();
					chatFrame.setMessageWrapper(messageWrapper);
					chatFrame.addMessages(label.getMessages());
					
					chatFrame.addChatFrameCloseListener(label);
					
					addMainFrameCloseListener(chatFrame);
					
					messageWrapper.addFileCancelListener(chatFrame);
					messageWrapper.addSendFileListener(chatFrame);//没有添加进去
					messageWrapper.addReciveFileListener(chatFrame);
					messageWrapper.init();
					messageWrapper.start();
					label.stopTick();
					label.clearMessages();
					label.setFrameBuild(true);
					users.remove(id);
					users.put(id, label);
					//添加其他
					messageWrapper.buildChat();
				}
			}
		}).start();
	}
	
	public void fireMainFrameCloseListeners()
	{
		int len = mainFrameCloseListeners.size();
		for(int i=0;i<len;i++)
		{
			mainFrameCloseListeners.get(i).mainFrameClose(this.manager.getUserId());
		}
	}
	
	public void addMainFrameCloseListener(MainFrameCloseListener listener)
	{
		mainFrameCloseListeners.add(listener);
	}
}
