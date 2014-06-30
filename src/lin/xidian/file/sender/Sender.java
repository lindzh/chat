package lin.xidian.file.sender;

import java.io.IOException;


public interface Sender {
	
	public void send(byte[] buffer,int length) throws IOException;
	//TODO
	//  send 必须以某一平率执行
	
}
