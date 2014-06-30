package lin.xidian.file.receiver;

import lin.xidian.file.deliver.Deliver;

public interface Receive {
	public void init();
	public void start();
	public void close();
	public void setDeliver(Deliver deliver);
}
