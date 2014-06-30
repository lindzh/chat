package lin.xidian.video;

import java.awt.Component;

public class VideoVisualEvent implements VideoEvent
{
	private  Component visualComponent;
	
	public VideoVisualEvent(Component visualComponent)
	{
		this.visualComponent = visualComponent;
	}

	public Component getVisualComponent()
	{
		return visualComponent;
	}
}
