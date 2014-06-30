package lin.xidian.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import javax.swing.filechooser.FileFilter;
import javax.media.format.*;
import javax.media.*;
import java.util.*;

// ��������
public class MainFrame extends Frame{
  private String fileName = null;                       // ��ȡҪ������ļ���
  private RTPTransmit rtpTransmit = null;               // RTP������Ķ���

  Label labelIP = new Label();
  TextField textIPAdd1 = new TextField();               // IP��ַ�༭��
  TextField textIPAdd2 = new TextField();
  TextField textIPAdd3 = new TextField();
  TextField textIPAdd4 = new TextField();

  Label labelPort = new Label();
  TextField textPort = new TextField();                 // �˿ڱ༭��

  JLabel jLabelIP = new JLabel();

  Label labelFile = new Label();
  CheckboxGroup checkboxGroupFiles = new CheckboxGroup();
  Checkbox checkboxMov = new Checkbox();                // ѡ��QuickTime�ļ���Mov����ѡ��
  Checkbox checkboxAudio = new Checkbox();              // ѡ��Audio�ļ���ѡ��
  Checkbox checkboxMPEG = new Checkbox();               // ѡ��MPEG�ļ���ѡ��

  Button buttonFile = new Button();                     // ��������ļ���ť
  TextField textFile = new TextField();                 // ��ʾ�ļ����༭��

  JLabel jLabelFile = new JLabel();

  Button buttonBeginTransmit = new Button();            // �����䡱��ť
  Button buttonStopTransmit = new Button();             // ��ֹͣ����ť

  // ���ý��������¼��ļ���
  private void jbInit() throws Exception {
    this.setLayout(null);
    this.setBackground(Color.lightGray);

    labelIP.setText("IP��ַ��");
    labelIP.setBounds(new Rectangle(50, 50, 50, 20));

    textIPAdd1.setBounds(new Rectangle(125, 50, 40, 20));
    textIPAdd2.setBounds(new Rectangle(175, 50, 40, 20));
    textIPAdd3.setBounds(new Rectangle(225, 50, 40, 20));
    textIPAdd4.setBounds(new Rectangle(275, 50, 40, 20));

    labelPort.setText("�˿ںţ�");
    labelPort.setBounds(new Rectangle(50, 90, 50, 20));
    textPort.setBounds(new Rectangle(125, 90, 40, 20));

    jLabelIP.setBorder(BorderFactory.createEtchedBorder());
    jLabelIP.setBounds(new Rectangle(29, 33, 313, 91));

    labelFile.setText("�ļ����ͣ�");
    labelFile.setBounds(new Rectangle(50, 180, 70, 20));

    checkboxMov.setLabel("QuickTime Files");
    checkboxMov.setBounds(new Rectangle(125, 160, 120, 15));
    checkboxMov.setCheckboxGroup(checkboxGroupFiles);

    checkboxAudio.setLabel("Audio Files");
    checkboxAudio.setBounds(new Rectangle(125, 180, 120, 15));
    checkboxAudio.setCheckboxGroup(checkboxGroupFiles);

    checkboxMPEG.setLabel("MPEG Files");
    checkboxMPEG.setBounds(new Rectangle(125, 200, 120, 15));
    checkboxMPEG.setCheckboxGroup(checkboxGroupFiles);

    checkboxGroupFiles.setSelectedCheckbox(checkboxMov);

    buttonFile.setLabel("���");
    buttonFile.setBounds(new Rectangle(50, 240, 58, 20));
    buttonFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonFile_actionPerformed(e);
      }
    });
    textFile.setBounds(new Rectangle(125, 240, 190, 20));

    jLabelFile.setBorder(BorderFactory.createEtchedBorder());
    jLabelFile.setBounds(new Rectangle(29, 147, 314, 127));

    buttonBeginTransmit.setLabel("����");
    buttonBeginTransmit.setBounds(new Rectangle(94, 296, 58, 20));
    buttonBeginTransmit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonBeginTransmit_actionPerformed(e);
      }
    });

    buttonStopTransmit.setLabel("ֹͣ");
    buttonStopTransmit.setBounds(new Rectangle(214, 297, 58, 20));
    buttonStopTransmit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonStopTransmit_actionPerformed(e);
      }
    });

    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });

    this.add(buttonStopTransmit, null);
    this.add(buttonBeginTransmit, null);
    this.add(checkboxMov, null);
    this.add(labelIP, null);
    this.add(textIPAdd1, null);
    this.add(textIPAdd2, null);
    this.add(textIPAdd3, null);
    this.add(textIPAdd4, null);
    this.add(labelPort, null);
    this.add(textPort, null);
    this.add(jLabelIP, null);
    this.add(labelFile, null);
    this.add(checkboxAudio, null);
    this.add(checkboxMPEG, null);
    this.add(buttonFile, null);
    this.add(textFile, null);
    this.add(jLabelFile, null);

    this.setSize(new Dimension(371, 335));
    this.setTitle("RTP Transmit");                   // ���ÿ�ܱ���
    this.setVisible(true);                           // ��ʾ�����
  }

  // ���캯��
  public MainFrame() {
    try {
      jbInit();            // ��ʾ������
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  // �õ����贫���ļ�������
  int getFileType() {
    int indexTypeFile = 0;

    if(checkboxGroupFiles.getSelectedCheckbox() == checkboxMov)          // QuickTime�ļ�
      indexTypeFile = 0;
    if(checkboxGroupFiles.getSelectedCheckbox() == checkboxAudio)        // ��Ƶ�ļ�
      indexTypeFile = 1;
    if(checkboxGroupFiles.getSelectedCheckbox() == checkboxMPEG)         // MPEG�ļ�
      indexTypeFile = 2;

    return indexTypeFile;
  }

  // ��Ӧ���������ť�ĵ����Ϣ
  void buttonFile_actionPerformed(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser("D:");        // ѡ���ļ���Ĭ��·���ǡ�D:����
    ExampleFileFilter filter = new ExampleFileFilter();       // ʵ����һ���ļ�������

    int iTypeFile = getFileType();                            // �õ����贫���ļ�������
    switch(iTypeFile)
    {
    case 0:                                                   // QuickTime�ļ�
      filter.addExtension("mov");                             // �����ļ���չ��
      filter.setDescription("QuickTime Files");               // �����ļ�����������
      break;
    case 1:                                                   // ��Ƶ�ļ�
      filter.addExtension("au");
      filter.addExtension("wav");
      filter.setDescription("Audio Files");
      break;
    case 2:                                                   // MPEG�ļ�
      filter.addExtension("mpg");
      filter.addExtension("mpeg");
      filter.setDescription("MPEG Files");
      break;
    }
    fileChooser.setFileFilter(filter);

    int retVal = fileChooser.showOpenDialog(this);             // ���ļ�ѡ��Ի���

    if(retVal == JFileChooser.APPROVE_OPTION){
        fileName = fileChooser.getSelectedFile().getAbsolutePath();          // �õ���ѡ�ļ�
        textFile.setText(fileName);                    // ���ļ�����ʾ��������
    }
  }

  // ��Ӧ�����䡱��ť�ĵ����Ϣ����ʼ��������
  void buttonBeginTransmit_actionPerformed(ActionEvent e) {
    String strIPAddr = textIPAdd1.getText()+"."+textIPAdd2.getText()+"."+textIPAdd3.getText()+"."+textIPAdd4.getText();
                    // ��ϵõ�������IP��ַ
    String strPort = textPort.getText();     // �õ��˿ڵ�ַ

    fileName = textFile.getText();                             // �õ��ļ���

    fileName = "file:/" + fileName;                            // �����ļ���ʶ���Ա�ý�嶨λ��ȷ����������
    MediaLocator medLoc = new MediaLocator(fileName);          // �ñ�����һ�������ļ���Ϊ�������ý������

    Format fmt = null;
    rtpTransmit = new RTPTransmit(medLoc,strIPAddr,strPort,fmt);

    String result = rtpTransmit.start();             // ��ʼ����

    if (result != null) {                            // ��ʾ�������
      System.out.println("Error : " + result);
    }
    else {
      System.out.println("Start transmission ...");
    }
  }

  // ��Ӧ��ֹͣ����ť�ĵ����Ϣ��ֹͣ��������
  void buttonStopTransmit_actionPerformed(ActionEvent e) {
    if(rtpTransmit == null)
      return;

    rtpTransmit.stop();         // ֹͣ����

    System.out.println("...transmission ended.");
  }

  // �رմ��ڣ��˳�����
  void this_windowClosing(WindowEvent e) {
    System.exit(0);
  }

  // ������
  public static void main(String [] args) {
    new MainFrame();
  }

}

