package lin.xidian.file.message;

public interface MessageType {
	
	public static final String FILE_START_SEND = "file_start_send";
	public static final String FILE_IN_SEND = "file_in_send";
	public static final String FILE_END_SEND = "file_end_send";
	public static final String FILE_START_RECEIVE = "file_start_receive";
	public static final String FILE_IN_RECEIVE = "file_in_receive";
	public static final String FILE_END_RECEIVE = "file_end_receive";
	
	interface ProtocolKey
	{
		//tick
		public static final String CMD = "cmd";
		public static final String PORT = "port";
		public static final String ECHO_ID = "echo_id";
		public static final String ECHO_ACK_ID = "echo_ack_id";
		
		public static final String VERSION = "version";
		
		//file send and recieve
		public static final String FILE_ID = "file_id";
		public static final String FILE_SIZE = "file_size";
		public static final String FILE_NAME = "file_name";
		
		
		public static final String ID = "id";
		public static final String DATA = "data";
		public static final String MARK = "mark";
		
		
	
	}
}
