package lin.xidian.core;

public abstract class ManagerBase implements Wrapper,Login,LoginListener,LogoutListener,NewMessageListener,TickListener
{
	 public abstract void login();
	 public abstract void logout();
	 public abstract void addLogoutListener(LogoutListener listener);
	 public abstract void removeLogoutLstener(LogoutListener listener);
	 public abstract void requestFriendsAndGroups();
}
