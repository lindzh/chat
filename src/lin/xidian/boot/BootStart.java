package lin.xidian.boot;

import lin.xidian.core.LogSuccessListener;
import lin.xidian.core.LogfailListener;
import lin.xidian.core.Manager;
import lin.xidian.core.ManagerBase;
import lin.xidian.core.ManagerReciver;
import lin.xidian.core.ManagerReciverBase;
import lin.xidian.core.ManagerSender;
import lin.xidian.core.Sender;
import lin.xidian.frame.LoginFrame;
import lin.xidian.frame.MainFrame;

public class BootStart implements LogSuccessListener,LogfailListener
{
	private ManagerBase manager;
	private Sender sender;
	private ManagerReciverBase reciver;
	private LoginFrame loginFrame;
	private MainFrame mainFrame;
	
	public void init()
	{
		manager = new Manager();
		sender = new ManagerSender();
		reciver = new ManagerReciver();
		manager.setReciver(reciver);
		manager.setSender(sender);
		manager.init();
		loginFrame = new LoginFrame(manager);
		loginFrame.addLogSuccessListener(this);
		reciver.addLoginListener(loginFrame);
		reciver.addLogfailListener(loginFrame);
		reciver.addLogfailListener(this);
	}
	
	public static void main(String[] args)
	{
		BootStart boot = new BootStart();
		boot.init();
	}

	public void logSuccess(long id, String name, String ip, int port)
	{
		if(id == manager.getUserId())
		{	
			mainFrame = new MainFrame(manager);
			reciver.addLoginListener(mainFrame);
			reciver.addLogoutListener(mainFrame);
			reciver.addNewMessageListener(mainFrame);
			mainFrame.requestFriendsAndGroups();
		}
	}

	public void logFail(long id)
	{
		
	}
}
