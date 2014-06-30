package lin.xidian.audio;

public class AudioStartEvent implements AudioEvent
{
	private boolean success;
	private String info;
	
	public AudioStartEvent(String info,boolean success)
	{
		this.info = info;
		this.success = success;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public String getInfo()
	{
		return info;
	}
}
