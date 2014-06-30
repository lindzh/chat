package lin.xidian.test;

import java.io.*;
import java.awt.Dimension;
import java.net.InetAddress;
import javax.media.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import javax.media.format.*;
import javax.media.control.TrackControl;
import javax.media.rtp.*;

// 用RTP协议传输数据的类
public class RTPTransmit {
  private MediaLocator locator;     // 媒体定位，可以是一个本机文件，也可以是一个网络文件或采集设备得到的数据源
  private String ipAddress;         // 发送目的地（接收端）的IP地址
  private int portBase;             // 传输端口号

  private Processor processor = null;          // 处理器
  private RTPManager rtpMgrs[];                // RTP管理器
  private DataSource dataOutput = null;        // 输出的数据源

  // 构造函数
  public RTPTransmit(MediaLocator locator, String ipAddress,String pb,Format format) {
    this.locator = locator;
    this.ipAddress = ipAddress;
    Integer integer = Integer.valueOf(pb);
    if (integer != null)
      this.portBase = integer.intValue();
  }

  // 开始传输
  // 如果一切正常，就返回 null，否则返回出错原因
  public synchronized String start() {
    String result;

    result = createProcessor();           // 产生一个处理器
    if (result != null)
      return result;

    result = createTransmitter();        // 产生RTP会话，将处理器输出的数据传给指定的IP地址的指定的端口号
    if (result != null) {
      processor.close();
      processor = null;
      return result;
    }

    processor.start();         // 让处理器开始传输

    return null;
  }

  // 为指定的媒体定位器产生一个处理器
  private String createProcessor() {
    if (locator == null)
      return "Locator is null";

    DataSource ds;

    try {
      ds = javax.media.Manager.createDataSource(locator);           // 为定义的MediaLocator定位并实例化一个适当的数据源。
    }
    catch (Exception e) {
      return "Couldn't create DataSource";
    }

    try {
      processor = javax.media.Manager.createProcessor(ds);          // 通过数据源来产生一个处理器
    }
    catch (NoProcessorException npe) {
      return "Couldn't create processor";
    }
    catch (IOException ioe) {
      return "IOException creating processor";
    }

    boolean result = waitForState(processor, Processor.Configured);         // 等待处理器配置好
    if (result == false)
      return "Couldn't configure processor";

    TrackControl [] tracks = processor.getTrackControls();        // 为媒体流中的每一个磁道得到一个控制器

    if (tracks == null || tracks.length < 1)              // 确保至少有一个可用的磁道
      return "Couldn't find tracks in processor";

    ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
    processor.setContentDescriptor(cd);                // 设置输出的内容描述为RAW_RTP
                                                       // 从而限定每个磁道支持的格式仅为合法的RTP格式，即它影响后面的 Track.getSupportedFormats()
    Format supported[];
    Format chosen = null;
    boolean atLeastOneTrack = false;

    for (int i = 0; i < tracks.length; i++) {          // 对每一个磁道，选择一种RTP支持的传输格式
      Format format = tracks[i].getFormat();
      if (tracks[i].isEnabled()) {                     // 如果该磁道可用
        supported = tracks[i].getSupportedFormats();

        if (supported.length > 0) {
          if (supported[0] instanceof VideoFormat) {
            chosen = checkForVideoSizes(tracks[i].getFormat(),supported[0]); // 检查视频格式的尺寸，以确保正常工作
          }
          else
              chosen = supported[0];       // 前面已经设置了输出内容描述为RIP，这里支持的格式都可以与RTP配合工作
                                           // 这里选择第一种支持的格式

          tracks[i].setFormat(chosen);
          System.err.println("Track " + i + " is set to transmit as:");
          System.err.println("  " + chosen);
          atLeastOneTrack = true;
        }
        else
          tracks[i].setEnabled(false);
      }
      else
        tracks[i].setEnabled(false);
    }

    if (!atLeastOneTrack)
      return "Couldn't set any of the tracks to a valid RTP format";

    result = waitForState(processor, Controller.Realized);        // 等待处理器实现
    if (result == false)
      return "Couldn't realize processor";

    dataOutput = processor.getDataOutput();   // 从处理器得到输出的数据源

    return null;
  }

  // 为处理器的每一个媒体磁道产生一个RTP会话
  private String createTransmitter() {
    PushBufferDataSource pbds = (PushBufferDataSource)dataOutput;     // 将数据源转化为“Push”（推）数据源
    PushBufferStream pbss[] = pbds.getStreams();   // 得到“Push”数据流

    rtpMgrs = new RTPManager[pbss.length];       // 为每个磁道产生一个RTP会话管理器

    for (int i = 0; i < pbss.length; i++) {
      try {
        rtpMgrs[i] = RTPManager.newInstance();

        int port = portBase + 2 * i;                         // 每增加一个磁道，端口号加2
        InetAddress ipAddr = InetAddress.getByName(ipAddress);     // 得到发送目的地的IP地址

        SessionAddress localAddr = new SessionAddress( InetAddress.getLocalHost(),port);    // 得到本机的会话地址
                                                                // 这里传输端使用和接收目的端相同的端口号（实际上也可以不同）
        SessionAddress destAddr = new SessionAddress( ipAddr, port);          // 得到目的机器（接收端）的会话地址

        rtpMgrs[i].initialize( localAddr);                  // 将本机会话地址传给RTP管理器

        rtpMgrs[i].addTarget( destAddr);                    // 加入目的会话地址

        System.err.println( "Created RTP session: " + ipAddress + " " + port);

        SendStream sendStream = rtpMgrs[i].createSendStream(dataOutput, i);           // 产生数据源的RTP传输流

        sendStream.start();                                // 开始RTP数据流发送
      }
      catch (Exception  e) {
        return e.getMessage();
      }
    }

    return null;
  }

  // 由于JPEG和H.263编码标准，只支持一些特定的图像大小，所以这里进行必要的检查，以确保其可以正确编码
  Format checkForVideoSizes(Format original, Format supported) {
    int width, height;
    Dimension size = ((VideoFormat)original).getSize();         // 得到视频图像的尺寸
    Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
    Format h263Fmt = new Format(VideoFormat.H263_RTP);

    if (supported.matches(jpegFmt)) {          // 对JPEG格式，视频图像的宽和高必须是8像素的整数倍
      width = size.width % 8 == 0 ? size.width : ((int)(size.width / 8) * 8);
      height = size.height % 8 == 0 ? size.height : ((int)(size.height / 8) * 8);
    }
    else if (supported.matches(h263Fmt)) {     // H.263格式仅支持三种特定的图像尺寸
      if (size.width <= 128) {
          width = 128;
          height = 96;
      }
      else if (size.width <= 176) {
          width = 176;
          height = 144;
      }
      else {
          width = 352;
          height = 288;
      }
    }
    else {         // 对其他格式不予处理
      return supported;
    }

    return (new VideoFormat(null,new Dimension(width, height),Format.NOT_SPECIFIED,
                            null,Format.NOT_SPECIFIED)).intersects(supported);        // 返回经过处理后的视频格式
  }

  // 停止传输
  public void stop() {
    synchronized (this) {
      if (processor != null) {
        processor.stop();
        processor.close();                          // 停止处理器
        processor = null;                           // 关闭处理器
        for (int i = 0; i < rtpMgrs.length; i++) {  // 删除所有RTP管理器
          rtpMgrs[i].removeTargets( "Session ended.");
          rtpMgrs[i].dispose();
        }
      }
    }
  }

  // 以下两个变量为对处理器状态改变的处理服务
  private Integer stateLock = new Integer(0);        // 状态锁变量
  private boolean failed = false;                    // 是否失败的状态标志

  // 得到状态锁
  Integer getStateLock() {
    return stateLock;
  }

  // 设置失败标志
  void setFailed() {
    failed = true;
  }

  // 等待处理器达到相应的状态
  private synchronized boolean waitForState(Processor p, int state) {
    p.addControllerListener(new StateListener());          // 为处理器加上状态监听
    failed = false;

    if (state == Processor.Configured) {         // 配置处理器
      p.configure();
    }
    else if (state == Processor.Realized) {    // 实现处理器
      p.realize();
    }

    // 一直等待，直到成功达到所需状态，或失败
    while (p.getState() < state && !failed) {
      synchronized (getStateLock()) {
        try {
          getStateLock().wait();
        }
        catch (InterruptedException ie) {
          return false;
        }
      }
    }

    if (failed)
      return false;
    else
      return true;
  }

  // 内部类：处理器的状态监听器
  class StateListener implements ControllerListener {
    public void controllerUpdate(ControllerEvent ce) {
      // 如果在处理器配置或实现过程中出现错误，它将关闭
      if (ce instanceof ControllerClosedEvent)   // 控制器关闭
        setFailed();

      // 对于所有的控制器事件，通知在waitForState方法中等待的线程
      if (ce instanceof ControllerEvent) {
        synchronized (getStateLock()) {
          getStateLock().notifyAll();
        }
      }
    }
  }

}



