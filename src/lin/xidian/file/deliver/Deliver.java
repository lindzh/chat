package lin.xidian.file.deliver;

import lin.xidian.file.listener.FileListener;

public interface Deliver
{
	public void delive(byte[] data,int len);
	
	public void addListener(FileListener listener,long type);
}
