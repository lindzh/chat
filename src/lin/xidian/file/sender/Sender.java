package lin.xidian.file.sender;

import java.io.IOException;


public interface Sender {
	
	public void send(byte[] buffer,int length) throws IOException;
	//TODO
	//  send ������ĳһƽ��ִ��
	
}
