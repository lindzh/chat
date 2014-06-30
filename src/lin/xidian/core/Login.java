package lin.xidian.core;

public interface Login
{
	public void setServerIp(String serverip);
	public void setServerPort(int port);
	public void setUserId(long id);
	public void setUserName(String name);
	public void setUserPassword(String psw);
	public String getUserName();
	public String getServerIp();
	public int getServerPort();
	public long getUserId();
}
