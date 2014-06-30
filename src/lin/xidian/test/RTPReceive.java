package lin.xidian.test;

import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.util.Vector;
import javax.media.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.protocol.DataSource;
import javax.media.control.BufferControl;

// ����RTP�������ݵ���
public class RTPReceive implements ReceiveStreamListener, SessionListener,ControllerListener {
  String sessions[] = null;            // RTP�Ự�ַ�������
  RTPManager mgrs[] = null;            // RTP����������
  Vector playerWindows = null;         // �����������ڵ�����

  boolean dataReceived = false;        // �Ƿ���յ����ݵı�־
  Object dataSync = new Object();          // ͬ������

  // ���캯��
  public RTPReceive(String sessions[]) {
    this.sessions = sessions;
  }

  // ��ʼ��RTP�Ự��׼����������
  protected boolean initialize() {
    try {
      playerWindows = new Vector();       // ����һ������������������Ŵ���

      mgrs = new RTPManager[sessions.length];           // Ϊÿһ��RTP�Ự����һ��������
      SessionLabel session;
      for (int i = 0; i < sessions.length; i++) {          // ��ÿһ��RTP�Ự
        try {
          session = new SessionLabel(sessions[i]);          // ����RTP�Ự��ַ
        }
        catch (IllegalArgumentException e) {
          System.err.println("Failed to parse the session address given: " + sessions[i]);
          return false;
        }

        System.err.println("  - Open RTP session for: addr: " + session.addr + " port: " + session.port);

        mgrs[i] = (RTPManager) RTPManager.newInstance();       // Ϊÿһ��RTP�Ự����һ��RTP������
        mgrs[i].addSessionListener(this);                      // ��ӻỰ����
        mgrs[i].addReceiveStreamListener(this);                // ��ӽ��յ����ݵļ���

        InetAddress ipAddr = InetAddress.getByName(session.addr);

        SessionAddress localAddr = null;
        SessionAddress destAddr = null;
        if( ipAddr.isMulticastAddress()) {                     // �����鲥�����غ�Ŀ�ĵص�IP��ַ��ͬ���������鲥��ַ
          localAddr= new SessionAddress( ipAddr,session.port);
          destAddr = new SessionAddress( ipAddr,session.port);
        }
        else {
          localAddr= new SessionAddress( InetAddress.getLocalHost(),session.port);          // �ñ���IP��ַ�Ͷ˿ںŹ���Դ�Ự��ַ
          destAddr = new SessionAddress( ipAddr, session.port);                             // ��Ŀ�Ļ������Ͷˣ���IP��ַ�Ͷ˿ںŹ���Ŀ�ĻỰ��ַ
        }

        mgrs[i].initialize( localAddr);              // �������Ự��ַ��RTP������

        BufferControl bc = (BufferControl)mgrs[i].getControl("javax.media.control.BufferControl");
        if (bc != null)
          bc.setBufferLength(350);         // ���û�������С��Ҳ����ʹ������ֵ��

        mgrs[i].addTarget(destAddr);                 // ����Ŀ�ĻỰ��ַ
      }
    }
    catch (Exception e){
      System.err.println("Cannot create the RTP Session: " + e.getMessage());
      return false;
    }

    // �ȴ����ݵ���
    long then = System.currentTimeMillis();
    long waitingPeriod = 60000;   // ���ȴ�60��

    try{
      synchronized (dataSync) {
        while (!dataReceived && System.currentTimeMillis() - then < waitingPeriod) {   // �ȴ��������趨��ʱ��
          if (!dataReceived)
            System.err.println("  - Waiting for RTP data to arrive...");
          dataSync.wait(1000);
        }
      }
    }
    catch (Exception e) {}

    if (!dataReceived) {               // ���趨��ʱ����û�еȵ�����
      System.err.println("No RTP data was received.");
      close();
      return false;
    }

    return true;
  }

  // �رղ������ͻỰ������
  protected void close() {
    // �رղ��Ŵ���
    for (int i = 0; i < playerWindows.size(); i++) {
      try {
        ((PlayerWindow)playerWindows.elementAt(i)).close();
      }
      catch (Exception e) {}
    }
    playerWindows.removeAllElements();          // ɾ�����в��Ŵ���

    // �ر�RTP�Ự
    for (int i = 0; i < mgrs.length; i++) {
      if (mgrs[i] != null) {
        mgrs[i].removeTargets( "Closing session from RTPReceive");
        mgrs[i].dispose();                      // �ر�RTP�Ự������
        mgrs[i] = null;
      }
    }
  }

  // �ж������Ƿ�������
  public boolean isDone() {
    return playerWindows.size() == 0;
  }

  // ͨ�����������Ҳ��Ŵ���
  PlayerWindow find(Player p) {
    for (int i = 0; i < playerWindows.size(); i++) {
      PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
      if (pw.player == p)
        return pw;
    }
    return null;
  }

  // ͨ���������������Ҳ��Ŵ���
  PlayerWindow find(ReceiveStream strm) {
    for (int i = 0; i < playerWindows.size(); i++) {
      PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
      if (pw.stream == strm)
        return pw;
    }
    return null;
  }

  /***************************************************************
  * �����������ʵ���� ReceiveStreamListener �ӿ�
  ****************************************************************/

  public synchronized void update( ReceiveStreamEvent evt) {
    RTPManager mgr = (RTPManager)evt.getSource();
    Participant participant = evt.getParticipant();	// �õ������ߣ������ߣ�
    ReceiveStream stream = evt.getReceiveStream();      // �õ����յ���������

    if (evt instanceof NewReceiveStreamEvent) {           // ���յ��µ�������
      try {
        stream = ((NewReceiveStreamEvent)evt).getReceiveStream();   // �õ���������
        DataSource ds = stream.getDataSource();                     // �õ�����Դ

        RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");  // �õ�RTP������
        if (ctl != null)
          System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());    // �õ��������ݵĸ�ʽ
        else
          System.err.println("  - Recevied new RTP stream");

        if (participant == null)
          System.err.println("      The sender of this stream had yet to be identified.");
        else
          System.err.println("      The stream comes from: " + participant.getCNAME());

        Player p = javax.media.Manager.createPlayer(ds);       // ͨ������Դ����һ��ý�岥����
        if (p == null)
          return;

        p.addControllerListener(this);             // ����������ӿ���������
        p.realize();                               // ʵ�ֲ�����
        PlayerWindow pw = new PlayerWindow(p, stream);     // ͨ��������������������һ�����Ŵ���
        playerWindows.addElement(pw);         // ���ò��Ŵ��ڼ�������������

        // ֪ͨinitialize()�����еĵȴ����̣��Ѿ����յ���һ����������
        synchronized (dataSync) {
          dataReceived = true;
          dataSync.notifyAll();
        }
      }
      catch (Exception e) {
        System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
        return;
      }
    }
    else if (evt instanceof StreamMappedEvent) {         // ������ӳ���¼�
      if (stream != null && stream.getDataSource() != null) {
        DataSource ds = stream.getDataSource();
        RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
        System.err.println("  - The previously unidentified stream ");
        if (ctl != null)
          System.err.println("      " + ctl.getFormat());         // �õ���ʽ
        System.err.println("      had now been identified as sent by: " + participant.getCNAME());
      }
    }
    else if (evt instanceof ByeEvent) {                  // ���ݽ������
      System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
      PlayerWindow pw = find(stream);

      if (pw != null) {
         pw.close();                                      // �رղ��Ŵ���
         playerWindows.removeElement(pw);                 // ��������ȥ���ò��Ŵ���
      }
    }
  }

  /***************************************************************
  * �����������ʵ���� SessionListener �ӿ�
  ****************************************************************/

  public synchronized void update(SessionEvent evt) {
    if (evt instanceof NewParticipantEvent) {
      Participant p = ((NewParticipantEvent)evt).getParticipant();
      System.err.println("  - A new participant had just joined: " + p.getCNAME());
    }
  }

  /***************************************************************
  * �����������ʵ���� ControllerListener �ӿ�
  ****************************************************************/

  public synchronized void controllerUpdate(ControllerEvent ce) {
    Player p = (Player)ce.getSourceController();            // �õ��¼�Դ

    if (p == null)
      return;

    if (ce instanceof RealizeCompleteEvent) {               // ������ʵ�����
      PlayerWindow pw = find(p);
      if (pw == null) {       // �����˴���
        System.err.println("Internal error!");
        return;
      }
      pw.initialize();             // ��ʼ�����Ŵ���
      pw.setVisible(true);         // ��ʾ���Ŵ���
      p.start();                   // ��ʼ����
    }

    if (ce instanceof ControllerErrorEvent) {              // ����������
      p.removeControllerListener(this);
      PlayerWindow pw = find(p);
      if (pw != null) {
        pw.close();
        playerWindows.removeElement(pw);
      }
      System.err.println("RTPReceive internal error: " + ce);
    }
  }


  // �����Ự��ַ����
  class SessionLabel {
    public String addr = null;
    public int port;

    SessionLabel(String session) throws IllegalArgumentException {
      int off;
      String portStr = null;//, ttlStr = null;

      if (session != null && session.length() > 0) {
        while (session.length() > 1 && session.charAt(0) == '/') // ȥ���ַ���ǰ��ġ�/��
          session = session.substring(1);

        off = session.indexOf('/');      // �ҵ��ַ����С�/����λ��
        if (off == -1) {                 // ����ַ�����û���ҵ���/��
          if (!session.equals(""))       // �ַ�����Ϊ��
            addr = session;              // �ַ���ֵ��ΪIP��ַ
        }
        else {                           // ����ַ������ҵ��ˡ�/����˵���ַ����л����ڶ˿ں�
          addr = session.substring(0, off);
          session = session.substring(off + 1);   // �õ���һ����/��������Ӵ�
          off = session.indexOf('/');    // �ҵ��Ӵ��С�/����λ��
          if (off == -1) {               // ����Ӵ���û���ҵ���/��
            if (!session.equals(""))
              portStr = session;         // �ַ���ֵ��Ϊ�˿ں�
          }
        }
      }

      if (addr == null)            // ���û�и���IP��ַ���򱨴�
        throw new IllegalArgumentException();

      if (portStr != null) {       // ��������˶˿ںţ�����ת��Ϊ������
        try {
          Integer integer = Integer.valueOf(portStr);
          if (integer != null)
            port = integer.intValue();
        }
        catch (Throwable t) {
          throw new IllegalArgumentException();
        }
      }
      else                        // ���û�и����˿ںţ��򱨴�
        throw new IllegalArgumentException();
    }
  }

  // ���Ŵ�����
  class PlayerWindow extends Frame {
    Player player;              // ����������
    ReceiveStream stream;       // ��������������

    // ���캯��
    PlayerWindow(Player p, ReceiveStream strm) {
      player = p;
      stream = strm;
    }

    // ��ʼ��
    public void initialize() {
      add(new PlayerPanel(player));
    }

    // ֪ͨ��Ϣ
    public void addNotify() {
      super.addNotify();
      pack();                        // ����������������µ������ڴ�С
    }

    // �رղ�����
    public void close() {
      player.close();
      setVisible(false);
      dispose();
    }
  }

  // �����������
  class PlayerPanel extends Panel {
    Component vc, cc;

    // ���캯��
    PlayerPanel(Player p) {
      setLayout(new BorderLayout());
      if ((vc = p.getVisualComponent()) != null)
        add("Center", vc);     // ��Ӳ�������Ƶ���
      if ((cc = p.getControlPanelComponent()) != null)
        add("South", cc);      // ��Ӳ������������
    }
  }

  public static void main(String argv[]) {             // ������
    /*
	  if (argv.length == 0)                              // ���û�и���ִ�в���
      prUsage();                                       // ��ʾ�����÷�
	*/
	  String[] str = new String[]{"222.25.187.61/8001"};
    RTPReceive rtpReceive = new RTPReceive(str);     // �ø�����������һ��RTPReceive����
    if (!rtpReceive.initialize()) {                   // ��ʼRTP���ݽ���
      System.err.println("Failed to initialize the sessions.");
      System.exit(-1);
    }

    // �ȴ��������
    try {
      while (!rtpReceive.isDone())
        Thread.sleep(1000);
    }
    catch (Exception e) {}

    System.err.println("Exiting RTPReceive");
  }

  // �����÷�˵��
  static void prUsage() {
    System.err.println("Usage: RTPReceive <session> <session> ...");
    System.err.println("     <session>: <address>/<port>");
    System.exit(0);
  }

}
