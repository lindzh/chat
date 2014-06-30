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

// 接收RTP传输数据的类
public class RTPReceive implements ReceiveStreamListener, SessionListener,ControllerListener {
  String sessions[] = null;            // RTP会话字符串数组
  RTPManager mgrs[] = null;            // RTP管理器数组
  Vector playerWindows = null;         // 管理播放器窗口的向量

  boolean dataReceived = false;        // 是否接收到数据的标志
  Object dataSync = new Object();          // 同步对象

  // 构造函数
  public RTPReceive(String sessions[]) {
    this.sessions = sessions;
  }

  // 初始化RTP会话，准备接收数据
  protected boolean initialize() {
    try {
      playerWindows = new Vector();       // 构造一个向量数组管理多个播放窗口

      mgrs = new RTPManager[sessions.length];           // 为每一个RTP会话建立一个管理器
      SessionLabel session;
      for (int i = 0; i < sessions.length; i++) {          // 对每一个RTP会话
        try {
          session = new SessionLabel(sessions[i]);          // 解析RTP会话地址
        }
        catch (IllegalArgumentException e) {
          System.err.println("Failed to parse the session address given: " + sessions[i]);
          return false;
        }

        System.err.println("  - Open RTP session for: addr: " + session.addr + " port: " + session.port);

        mgrs[i] = (RTPManager) RTPManager.newInstance();       // 为每一个RTP会话产生一个RTP管理器
        mgrs[i].addSessionListener(this);                      // 添加会话监听
        mgrs[i].addReceiveStreamListener(this);                // 添加接收到数据的监听

        InetAddress ipAddr = InetAddress.getByName(session.addr);

        SessionAddress localAddr = null;
        SessionAddress destAddr = null;
        if( ipAddr.isMulticastAddress()) {                     // 对于组播，本地和目的地的IP地址相同，均采用组播地址
          localAddr= new SessionAddress( ipAddr,session.port);
          destAddr = new SessionAddress( ipAddr,session.port);
        }
        else {
          localAddr= new SessionAddress( InetAddress.getLocalHost(),session.port);          // 用本机IP地址和端口号构造源会话地址
          destAddr = new SessionAddress( ipAddr, session.port);                             // 用目的机（发送端）的IP地址和端口号构造目的会话地址
        }

        mgrs[i].initialize( localAddr);              // 将本机会话地址给RTP管理器

        BufferControl bc = (BufferControl)mgrs[i].getControl("javax.media.control.BufferControl");
        if (bc != null)
          bc.setBufferLength(350);         // 设置缓冲区大小（也可以使用其他值）

        mgrs[i].addTarget(destAddr);                 // 加入目的会话地址
      }
    }
    catch (Exception e){
      System.err.println("Cannot create the RTP Session: " + e.getMessage());
      return false;
    }

    // 等待数据到达
    long then = System.currentTimeMillis();
    long waitingPeriod = 60000;   // 最多等待60秒

    try{
      synchronized (dataSync) {
        while (!dataReceived && System.currentTimeMillis() - then < waitingPeriod) {   // 等待上面所设定的时间
          if (!dataReceived)
            System.err.println("  - Waiting for RTP data to arrive...");
          dataSync.wait(1000);
        }
      }
    }
    catch (Exception e) {}

    if (!dataReceived) {               // 在设定的时间内没有等到数据
      System.err.println("No RTP data was received.");
      close();
      return false;
    }

    return true;
  }

  // 关闭播放器和会话管理器
  protected void close() {
    // 关闭播放窗口
    for (int i = 0; i < playerWindows.size(); i++) {
      try {
        ((PlayerWindow)playerWindows.elementAt(i)).close();
      }
      catch (Exception e) {}
    }
    playerWindows.removeAllElements();          // 删除所有播放窗口

    // 关闭RTP会话
    for (int i = 0; i < mgrs.length; i++) {
      if (mgrs[i] != null) {
        mgrs[i].removeTargets( "Closing session from RTPReceive");
        mgrs[i].dispose();                      // 关闭RTP会话管理器
        mgrs[i] = null;
      }
    }
  }

  // 判断数据是否接收完成
  public boolean isDone() {
    return playerWindows.size() == 0;
  }

  // 通过播放器查找播放窗口
  PlayerWindow find(Player p) {
    for (int i = 0; i < playerWindows.size(); i++) {
      PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
      if (pw.player == p)
        return pw;
    }
    return null;
  }

  // 通过接收数据流查找播放窗口
  PlayerWindow find(ReceiveStream strm) {
    for (int i = 0; i < playerWindows.size(); i++) {
      PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
      if (pw.stream == strm)
        return pw;
    }
    return null;
  }

  /***************************************************************
  * 下面这个函数实现了 ReceiveStreamListener 接口
  ****************************************************************/

  public synchronized void update( ReceiveStreamEvent evt) {
    RTPManager mgr = (RTPManager)evt.getSource();
    Participant participant = evt.getParticipant();	// 得到加入者（发送者）
    ReceiveStream stream = evt.getReceiveStream();      // 得到接收到的数据流

    if (evt instanceof NewReceiveStreamEvent) {           // 接收到新的数据流
      try {
        stream = ((NewReceiveStreamEvent)evt).getReceiveStream();   // 得到新数据流
        DataSource ds = stream.getDataSource();                     // 得到数据源

        RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");  // 得到RTP控制器
        if (ctl != null)
          System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());    // 得到接收数据的格式
        else
          System.err.println("  - Recevied new RTP stream");

        if (participant == null)
          System.err.println("      The sender of this stream had yet to be identified.");
        else
          System.err.println("      The stream comes from: " + participant.getCNAME());

        Player p = javax.media.Manager.createPlayer(ds);       // 通过数据源构造一个媒体播放器
        if (p == null)
          return;

        p.addControllerListener(this);             // 给播放器添加控制器监听
        p.realize();                               // 实现播放器
        PlayerWindow pw = new PlayerWindow(p, stream);     // 通过播放器和数据流构造一个播放窗口
        playerWindows.addElement(pw);         // 将该播放窗口加入向量数组中

        // 通知initialize()函数中的等待过程：已经接收到了一个新数据流
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
    else if (evt instanceof StreamMappedEvent) {         // 数据流映射事件
      if (stream != null && stream.getDataSource() != null) {
        DataSource ds = stream.getDataSource();
        RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
        System.err.println("  - The previously unidentified stream ");
        if (ctl != null)
          System.err.println("      " + ctl.getFormat());         // 得到格式
        System.err.println("      had now been identified as sent by: " + participant.getCNAME());
      }
    }
    else if (evt instanceof ByeEvent) {                  // 数据接收完毕
      System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
      PlayerWindow pw = find(stream);

      if (pw != null) {
         pw.close();                                      // 关闭播放窗口
         playerWindows.removeElement(pw);                 // 从向量中去掉该播放窗口
      }
    }
  }

  /***************************************************************
  * 下面这个函数实现了 SessionListener 接口
  ****************************************************************/

  public synchronized void update(SessionEvent evt) {
    if (evt instanceof NewParticipantEvent) {
      Participant p = ((NewParticipantEvent)evt).getParticipant();
      System.err.println("  - A new participant had just joined: " + p.getCNAME());
    }
  }

  /***************************************************************
  * 下面这个函数实现了 ControllerListener 接口
  ****************************************************************/

  public synchronized void controllerUpdate(ControllerEvent ce) {
    Player p = (Player)ce.getSourceController();            // 得到事件源

    if (p == null)
      return;

    if (ce instanceof RealizeCompleteEvent) {               // 播放器实现完成
      PlayerWindow pw = find(p);
      if (pw == null) {       // 出现了错误
        System.err.println("Internal error!");
        return;
      }
      pw.initialize();             // 初始化播放窗口
      pw.setVisible(true);         // 显示播放窗口
      p.start();                   // 开始播放
    }

    if (ce instanceof ControllerErrorEvent) {              // 控制器错误
      p.removeControllerListener(this);
      PlayerWindow pw = find(p);
      if (pw != null) {
        pw.close();
        playerWindows.removeElement(pw);
      }
      System.err.println("RTPReceive internal error: " + ce);
    }
  }


  // 解析会话地址的类
  class SessionLabel {
    public String addr = null;
    public int port;

    SessionLabel(String session) throws IllegalArgumentException {
      int off;
      String portStr = null;//, ttlStr = null;

      if (session != null && session.length() > 0) {
        while (session.length() > 1 && session.charAt(0) == '/') // 去掉字符串前面的“/”
          session = session.substring(1);

        off = session.indexOf('/');      // 找到字符串中“/”的位置
        if (off == -1) {                 // 如果字符串中没有找到“/”
          if (!session.equals(""))       // 字符串不为空
            addr = session;              // 字符串值作为IP地址
        }
        else {                           // 如果字符串中找到了“/”，说明字符串中还存在端口号
          addr = session.substring(0, off);
          session = session.substring(off + 1);   // 得到第一个“/”后面的子串
          off = session.indexOf('/');    // 找到子串中“/”的位置
          if (off == -1) {               // 如果子串中没有找到“/”
            if (!session.equals(""))
              portStr = session;         // 字符串值作为端口号
          }
        }
      }

      if (addr == null)            // 如果没有给出IP地址，则报错
        throw new IllegalArgumentException();

      if (portStr != null) {       // 如果给出了端口号，则将其转化为整型数
        try {
          Integer integer = Integer.valueOf(portStr);
          if (integer != null)
            port = integer.intValue();
        }
        catch (Throwable t) {
          throw new IllegalArgumentException();
        }
      }
      else                        // 如果没有给出端口号，则报错
        throw new IllegalArgumentException();
    }
  }

  // 播放窗口类
  class PlayerWindow extends Frame {
    Player player;              // 播放器对象
    ReceiveStream stream;       // 接收数据流对象

    // 构造函数
    PlayerWindow(Player p, ReceiveStream strm) {
      player = p;
      stream = strm;
    }

    // 初始化
    public void initialize() {
      add(new PlayerPanel(player));
    }

    // 通知消息
    public void addNotify() {
      super.addNotify();
      pack();                        // 在增加了组件后，重新调整窗口大小
    }

    // 关闭播放器
    public void close() {
      player.close();
      setVisible(false);
      dispose();
    }
  }

  // 播放器组件类
  class PlayerPanel extends Panel {
    Component vc, cc;

    // 构造函数
    PlayerPanel(Player p) {
      setLayout(new BorderLayout());
      if ((vc = p.getVisualComponent()) != null)
        add("Center", vc);     // 添加播放器视频组件
      if ((cc = p.getControlPanelComponent()) != null)
        add("South", cc);      // 添加播放器控制组件
    }
  }

  public static void main(String argv[]) {             // 主函数
    /*
	  if (argv.length == 0)                              // 如果没有给出执行参数
      prUsage();                                       // 提示程序用法
	*/
	  String[] str = new String[]{"222.25.187.61/8001"};
    RTPReceive rtpReceive = new RTPReceive(str);     // 用给定参数产生一个RTPReceive对象
    if (!rtpReceive.initialize()) {                   // 开始RTP数据接收
      System.err.println("Failed to initialize the sessions.");
      System.exit(-1);
    }

    // 等待接收完毕
    try {
      while (!rtpReceive.isDone())
        Thread.sleep(1000);
    }
    catch (Exception e) {}

    System.err.println("Exiting RTPReceive");
  }

  // 程序用法说明
  static void prUsage() {
    System.err.println("Usage: RTPReceive <session> <session> ...");
    System.err.println("     <session>: <address>/<port>");
    System.exit(0);
  }

}
