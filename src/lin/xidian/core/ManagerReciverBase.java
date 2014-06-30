package lin.xidian.core;

public abstract class ManagerReciverBase implements Reciver
{
	public abstract void addLoginListener(LoginListener listener);
	public abstract void addLogfailListener(LogfailListener listener);
	public abstract void addLogoutListener(LogoutListener listener);
	public abstract void addNewMessageListener(NewMessageListener listener);
}
