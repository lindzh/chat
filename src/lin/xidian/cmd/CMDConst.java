package lin.xidian.cmd;

public interface CMDConst
{
	static final long TICK_REQUEST = 0x99997777;   //����������
	static final long TICK_ACK = 0x99990000;       //������ȷ��
	static final long SEND_FILE_REQUEST = 0x11110000; //�����ļ�����
	static final long SEND_FILE_ACK_YES = 0x11111111; //ͬ������ļ�
	static final long SEND_FILE_ACK_NO = 0x11112222;  //�ܾ������ļ�
	static final long SEND_FILE_DATA = 0x11113333;
	static final long SEND_FILE_DATA_ACK = 0x11114444;
	static final long SEND_FILE_DATA_RESEND = 0x11115555;
	static final long SEND_FILE_CANCEL = 0x11116666;
}
