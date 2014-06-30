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

// ��RTPЭ�鴫�����ݵ���
public class RTPTransmit {
  private MediaLocator locator;     // ý�嶨λ��������һ�������ļ���Ҳ������һ�������ļ���ɼ��豸�õ�������Դ
  private String ipAddress;         // ����Ŀ�ĵأ����նˣ���IP��ַ
  private int portBase;             // ����˿ں�

  private Processor processor = null;          // ������
  private RTPManager rtpMgrs[];                // RTP������
  private DataSource dataOutput = null;        // ���������Դ

  // ���캯��
  public RTPTransmit(MediaLocator locator, String ipAddress,String pb,Format format) {
    this.locator = locator;
    this.ipAddress = ipAddress;
    Integer integer = Integer.valueOf(pb);
    if (integer != null)
      this.portBase = integer.intValue();
  }

  // ��ʼ����
  // ���һ���������ͷ��� null�����򷵻س���ԭ��
  public synchronized String start() {
    String result;

    result = createProcessor();           // ����һ��������
    if (result != null)
      return result;

    result = createTransmitter();        // ����RTP�Ự������������������ݴ���ָ����IP��ַ��ָ���Ķ˿ں�
    if (result != null) {
      processor.close();
      processor = null;
      return result;
    }

    processor.start();         // �ô�������ʼ����

    return null;
  }

  // Ϊָ����ý�嶨λ������һ��������
  private String createProcessor() {
    if (locator == null)
      return "Locator is null";

    DataSource ds;

    try {
      ds = javax.media.Manager.createDataSource(locator);           // Ϊ�����MediaLocator��λ��ʵ����һ���ʵ�������Դ��
    }
    catch (Exception e) {
      return "Couldn't create DataSource";
    }

    try {
      processor = javax.media.Manager.createProcessor(ds);          // ͨ������Դ������һ��������
    }
    catch (NoProcessorException npe) {
      return "Couldn't create processor";
    }
    catch (IOException ioe) {
      return "IOException creating processor";
    }

    boolean result = waitForState(processor, Processor.Configured);         // �ȴ����������ú�
    if (result == false)
      return "Couldn't configure processor";

    TrackControl [] tracks = processor.getTrackControls();        // Ϊý�����е�ÿһ���ŵ��õ�һ��������

    if (tracks == null || tracks.length < 1)              // ȷ��������һ�����õĴŵ�
      return "Couldn't find tracks in processor";

    ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
    processor.setContentDescriptor(cd);                // �����������������ΪRAW_RTP
                                                       // �Ӷ��޶�ÿ���ŵ�֧�ֵĸ�ʽ��Ϊ�Ϸ���RTP��ʽ������Ӱ������ Track.getSupportedFormats()
    Format supported[];
    Format chosen = null;
    boolean atLeastOneTrack = false;

    for (int i = 0; i < tracks.length; i++) {          // ��ÿһ���ŵ���ѡ��һ��RTP֧�ֵĴ����ʽ
      Format format = tracks[i].getFormat();
      if (tracks[i].isEnabled()) {                     // ����ôŵ�����
        supported = tracks[i].getSupportedFormats();

        if (supported.length > 0) {
          if (supported[0] instanceof VideoFormat) {
            chosen = checkForVideoSizes(tracks[i].getFormat(),supported[0]); // �����Ƶ��ʽ�ĳߴ磬��ȷ����������
          }
          else
              chosen = supported[0];       // ǰ���Ѿ������������������ΪRIP������֧�ֵĸ�ʽ��������RTP��Ϲ���
                                           // ����ѡ���һ��֧�ֵĸ�ʽ

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

    result = waitForState(processor, Controller.Realized);        // �ȴ�������ʵ��
    if (result == false)
      return "Couldn't realize processor";

    dataOutput = processor.getDataOutput();   // �Ӵ������õ����������Դ

    return null;
  }

  // Ϊ��������ÿһ��ý��ŵ�����һ��RTP�Ự
  private String createTransmitter() {
    PushBufferDataSource pbds = (PushBufferDataSource)dataOutput;     // ������Դת��Ϊ��Push�����ƣ�����Դ
    PushBufferStream pbss[] = pbds.getStreams();   // �õ���Push��������

    rtpMgrs = new RTPManager[pbss.length];       // Ϊÿ���ŵ�����һ��RTP�Ự������

    for (int i = 0; i < pbss.length; i++) {
      try {
        rtpMgrs[i] = RTPManager.newInstance();

        int port = portBase + 2 * i;                         // ÿ����һ���ŵ����˿ںż�2
        InetAddress ipAddr = InetAddress.getByName(ipAddress);     // �õ�����Ŀ�ĵص�IP��ַ

        SessionAddress localAddr = new SessionAddress( InetAddress.getLocalHost(),port);    // �õ������ĻỰ��ַ
                                                                // ���ﴫ���ʹ�úͽ���Ŀ�Ķ���ͬ�Ķ˿ںţ�ʵ����Ҳ���Բ�ͬ��
        SessionAddress destAddr = new SessionAddress( ipAddr, port);          // �õ�Ŀ�Ļ��������նˣ��ĻỰ��ַ

        rtpMgrs[i].initialize( localAddr);                  // �������Ự��ַ����RTP������

        rtpMgrs[i].addTarget( destAddr);                    // ����Ŀ�ĻỰ��ַ

        System.err.println( "Created RTP session: " + ipAddress + " " + port);

        SendStream sendStream = rtpMgrs[i].createSendStream(dataOutput, i);           // ��������Դ��RTP������

        sendStream.start();                                // ��ʼRTP����������
      }
      catch (Exception  e) {
        return e.getMessage();
      }
    }

    return null;
  }

  // ����JPEG��H.263�����׼��ֻ֧��һЩ�ض���ͼ���С������������б�Ҫ�ļ�飬��ȷ���������ȷ����
  Format checkForVideoSizes(Format original, Format supported) {
    int width, height;
    Dimension size = ((VideoFormat)original).getSize();         // �õ���Ƶͼ��ĳߴ�
    Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
    Format h263Fmt = new Format(VideoFormat.H263_RTP);

    if (supported.matches(jpegFmt)) {          // ��JPEG��ʽ����Ƶͼ��Ŀ�͸߱�����8���ص�������
      width = size.width % 8 == 0 ? size.width : ((int)(size.width / 8) * 8);
      height = size.height % 8 == 0 ? size.height : ((int)(size.height / 8) * 8);
    }
    else if (supported.matches(h263Fmt)) {     // H.263��ʽ��֧�������ض���ͼ��ߴ�
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
    else {         // ��������ʽ���账��
      return supported;
    }

    return (new VideoFormat(null,new Dimension(width, height),Format.NOT_SPECIFIED,
                            null,Format.NOT_SPECIFIED)).intersects(supported);        // ���ؾ�����������Ƶ��ʽ
  }

  // ֹͣ����
  public void stop() {
    synchronized (this) {
      if (processor != null) {
        processor.stop();
        processor.close();                          // ֹͣ������
        processor = null;                           // �رմ�����
        for (int i = 0; i < rtpMgrs.length; i++) {  // ɾ������RTP������
          rtpMgrs[i].removeTargets( "Session ended.");
          rtpMgrs[i].dispose();
        }
      }
    }
  }

  // ������������Ϊ�Դ�����״̬�ı�Ĵ������
  private Integer stateLock = new Integer(0);        // ״̬������
  private boolean failed = false;                    // �Ƿ�ʧ�ܵ�״̬��־

  // �õ�״̬��
  Integer getStateLock() {
    return stateLock;
  }

  // ����ʧ�ܱ�־
  void setFailed() {
    failed = true;
  }

  // �ȴ��������ﵽ��Ӧ��״̬
  private synchronized boolean waitForState(Processor p, int state) {
    p.addControllerListener(new StateListener());          // Ϊ����������״̬����
    failed = false;

    if (state == Processor.Configured) {         // ���ô�����
      p.configure();
    }
    else if (state == Processor.Realized) {    // ʵ�ִ�����
      p.realize();
    }

    // һֱ�ȴ���ֱ���ɹ��ﵽ����״̬����ʧ��
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

  // �ڲ��ࣺ��������״̬������
  class StateListener implements ControllerListener {
    public void controllerUpdate(ControllerEvent ce) {
      // ����ڴ��������û�ʵ�ֹ����г��ִ��������ر�
      if (ce instanceof ControllerClosedEvent)   // �������ر�
        setFailed();

      // �������еĿ������¼���֪ͨ��waitForState�����еȴ����߳�
      if (ce instanceof ControllerEvent) {
        synchronized (getStateLock()) {
          getStateLock().notifyAll();
        }
      }
    }
  }

}



