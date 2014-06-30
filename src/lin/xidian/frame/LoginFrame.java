package lin.xidian.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import lin.xidian.core.LogSuccessListener;
import lin.xidian.core.LogfailListener;
import lin.xidian.core.LoginListener;
import lin.xidian.core.ManagerBase;

public class LoginFrame extends JFrame implements LoginListener,LogfailListener
{	
	private JButton login = new JButton("登陆");
	private JButton set = new JButton("设置");
	private JButton cancel = new JButton("取消");
	private JTextField name = new JTextField(20);
	private JPasswordField psw = new JPasswordField(20);
	private String imgPath = System.getProperty("user.dir")+File.separator+"image"+"\\"; 
	
	private long myId = 0;
	private ManagerBase manager;
	private List<LogSuccessListener> listeners = new ArrayList<LogSuccessListener>();
	
	private boolean isReplyed = false;
	private long hasWait = 0;
	private Thread loginTimer;
	private Thread loginThread;
	
	private ActionListener btnListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent ae)
		{
			JButton button = (JButton)ae.getSource();
			if(button.equals(login))
			{
				logInit();
				loginThread.start();
			}
			
			if(button.equals(set))
			{
				IpPortConfig config = new IpPortConfig(LoginFrame.this,manager);
			}
			
			if(button.equals(cancel))
			{
				//添加退出信息
				loginThread = null;
				System.exit(0);
			}
		}
		
	};
	
	private void logInit()
	{
		loginThread = new Thread(new Runnable()
		{
			public void run()
			{
				String sid = name.getText();
				String pass = psw.getText();
				try
				{
					long id = Long.parseLong(sid);
					manager.setUserId(id);
					myId = id;
					manager.setUserPassword(pass);
					loginTimer.start();
					manager.start();
					manager.login();
				}catch(Exception x)
				{
					x.printStackTrace();
				}
			}
		});
		
		loginTimer = new Thread(new Runnable()
		{
			public void run()
			{
				long start = System.currentTimeMillis();
				while(hasWait<30000&&!isReplyed)
				{
					long now =  System.currentTimeMillis();
					hasWait = now-start;
				}
				if(!isReplyed)
				{
					JOptionPane.showMessageDialog(LoginFrame.this, "服务器没有打开！", "登录提示", JOptionPane.WARNING_MESSAGE);
				}
				isReplyed = false;
			}
		});
	}
	
	public LoginFrame(ManagerBase manager)
	{
		//图片
		this.manager = manager;
		ImageIcon icon = new ImageIcon(imgPath+"login.png");
		JLabel imageLabel = new JLabel(icon);
		JPanel imgPanel = new JPanel();
		imgPanel.add(imageLabel);
		
		JPanel namepanel = new JPanel();
		JLabel nameLabel = new JLabel("账号");
		namepanel.setLayout(new BoxLayout(namepanel,BoxLayout.X_AXIS));
		namepanel.add(nameLabel);
		namepanel.add(name);
		
		JPanel pswpanel = new JPanel();
		JLabel pswLabel = new JLabel("密码");
		pswpanel.setLayout(new BoxLayout(pswpanel,BoxLayout.X_AXIS));
		pswpanel.add(pswLabel);
		pswpanel.add(psw);
		
		ImageIcon logoIcon = new ImageIcon(imgPath+"logo.png");
		JLabel logoLabel = new JLabel(logoIcon);
		JPanel logoPanel = new JPanel();
		logoPanel.add(logoLabel);
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
		infoPanel.add(namepanel);
		infoPanel.add(pswpanel);
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel,BoxLayout.X_AXIS));
		middlePanel.add(logoPanel);
		middlePanel.add(infoPanel);
		
		JPanel setpanel = new JPanel();
		setpanel.setLayout(new BoxLayout(setpanel,BoxLayout.X_AXIS));
		setpanel.add(set);
		setpanel.add(login);
		setpanel.add(cancel);
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel,BoxLayout.Y_AXIS));
		loginPanel.add(imgPanel);
		loginPanel.add(middlePanel);
		loginPanel.add(setpanel);
		
		login.addActionListener(btnListener);
		set.addActionListener(btnListener);
		cancel.addActionListener(btnListener);
		
		this.add(loginPanel);
		this.setTitle("Chat登陆");
		this.setBounds(440, 230, 300, 215);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);	
	}

	public void addLogSuccessListener(LogSuccessListener listener)
	{
		listeners.add(listener);
	}
	
	//自己登陆成功提示
	public void login(long id, String name, String ip, int port)
	{
		if(id == myId)
		{
			isReplyed = true;
			this.dispose();//登陆成功
			int len = listeners.size();
			for(int i=0;i<len;i++)
			{
				listeners.get(i).logSuccess(id, name, ip, port);
			}
		}
	}

	public void logFail(long id)
	{
		if(id == myId)
		{
			isReplyed = true;
			manager.stop();
			JOptionPane.showMessageDialog(LoginFrame.this, "账号密码输入不正确，请重新输入！", "登录提示", JOptionPane.WARNING_MESSAGE);
		}
	}
}
