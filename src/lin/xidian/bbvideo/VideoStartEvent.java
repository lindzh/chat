package lin.xidian.bbvideo;

public class VideoStartEvent implements VideoEvent
{
	private boolean state;
	private String info;
	
	public VideoStartEvent(String info,boolean state)
	{
		this.state = state;
		this.info = info;
	}

	public boolean getState()
	{
		return state;
	}

	public String getInfo()
	{
		return info;
	}
	
	
	
}
