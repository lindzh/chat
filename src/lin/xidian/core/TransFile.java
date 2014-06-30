package lin.xidian.core;

public class TransFile
{
	private long id;
	private String path;
	private String name;
	private long size;
	
	public TransFile(long id,String path,String name,long size)
	{
		this.id = id;
		this.path = path;
		this.name = name;
		this.size = size;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
	
	
}
