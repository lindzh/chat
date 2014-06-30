package lin.xidian.audio;


public class AudioTest
{
	public static void main(String[] args)
	{
		RTPAudioWrapperBase audioWrapper = new RTPAudioWrapper();
		audioWrapper.setSelfReciveIp("222.25.187.241");
		audioWrapper.setSelfSendIp("222.25.187.241");
		audioWrapper.setDestSendIp("222.25.187.241");
		audioWrapper.setDestReciveIp("222.25.187.241");
		
		audioWrapper.setSelfSendPort(6000);
		audioWrapper.setSelfRecivePort(6010);
		audioWrapper.setDestSendPort(6000);
		audioWrapper.setDestRecivePort(6010);
		audioWrapper.setDestId(1000002);
		audioWrapper.setDestName("’≈—Ô");
		audioWrapper.init();
		audioWrapper.start();
	}

}
