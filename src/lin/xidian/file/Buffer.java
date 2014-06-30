package lin.xidian.file;

public class Buffer {
	private byte[] buffer;
	private int length;
	
	public byte[] getBuffer() {
		return buffer;
	}
	
	public Buffer()
	{
		
	}
	
	public Buffer(byte[] buffer,int length)
	{
		this.buffer = buffer;
		this.length = length;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}
	
	
	
	
}
