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

// 主界面类
public class MainFrame extends Frame{
  private String fileName = null;                       // 获取要传输的文件名
  private RTPTransmit rtpTransmit = null;               // RTP传输类的对象

  Label labelIP = new Label();
  TextField textIPAdd1 = new TextField();               // IP地址编辑框
  TextField textIPAdd2 = new TextField();
  TextField textIPAdd3 = new TextField();
  TextField textIPAdd4 = new TextField();

  Label labelPort = new Label();
  TextField textPort = new TextField();                 // 端口编辑框

  JLabel jLabelIP = new JLabel();

  Label labelFile = new Label();
  CheckboxGroup checkboxGroupFiles = new CheckboxGroup();
  Checkbox checkboxMov = new Checkbox();                // 选择QuickTime文件（Mov）单选框
  Checkbox checkboxAudio = new Checkbox();              // 选择Audio文件单选框
  Checkbox checkboxMPEG = new Checkbox();               // 选择MPEG文件单选框

  Button buttonFile = new Button();                     // “浏览”文件按钮
  TextField textFile = new TextField();                 // 显示文件名编辑框

  JLabel jLabelFile = new JLabel();

  Button buttonBeginTransmit = new Button();            // “传输”按钮
  Button buttonStopTransmit = new Button();             // “停止”按钮

  // 设置界面和添加事件的监听
  private void jbInit() throws Exception {
    this.setLayout(null);
    this.setBackground(Color.lightGray);

    labelIP.setText("IP地址：");
    labelIP.setBounds(new Rectangle(50, 50, 50, 20));

    textIPAdd1.setBounds(new Rectangle(125, 50, 40, 20));
    textIPAdd2.setBounds(new Rectangle(175, 50, 40, 20));
    textIPAdd3.setBounds(new Rectangle(225, 50, 40, 20));
    textIPAdd4.setBounds(new Rectangle(275, 50, 40, 20));

    labelPort.setText("端口号：");
    labelPort.setBounds(new Rectangle(50, 90, 50, 20));
    textPort.setBounds(new Rectangle(125, 90, 40, 20));

    jLabelIP.setBorder(BorderFactory.createEtchedBorder());
    jLabelIP.setBounds(new Rectangle(29, 33, 313, 91));

    labelFile.setText("文件类型：");
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

    buttonFile.setLabel("浏览");
    buttonFile.setBounds(new Rectangle(50, 240, 58, 20));
    buttonFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonFile_actionPerformed(e);
      }
    });
    textFile.setBounds(new Rectangle(125, 240, 190, 20));

    jLabelFile.setBorder(BorderFactory.createEtchedBorder());
    jLabelFile.setBounds(new Rectangle(29, 147, 314, 127));

    buttonBeginTransmit.setLabel("传输");
    buttonBeginTransmit.setBounds(new Rectangle(94, 296, 58, 20));
    buttonBeginTransmit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        buttonBeginTransmit_actionPerformed(e);
      }
    });

    buttonStopTransmit.setLabel("停止");
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
    this.setTitle("RTP Transmit");                   // 设置框架标题
    this.setVisible(true);                           // 显示出框架
  }

  // 构造函数
  public MainFrame() {
    try {
      jbInit();            // 显示出界面
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  // 得到所需传输文件的类型
  int getFileType() {
    int indexTypeFile = 0;

    if(checkboxGroupFiles.getSelectedCheckbox() == checkboxMov)          // QuickTime文件
      indexTypeFile = 0;
    if(checkboxGroupFiles.getSelectedCheckbox() == checkboxAudio)        // 音频文件
      indexTypeFile = 1;
    if(checkboxGroupFiles.getSelectedCheckbox() == checkboxMPEG)         // MPEG文件
      indexTypeFile = 2;

    return indexTypeFile;
  }

  // 响应“浏览”按钮的点击消息
  void buttonFile_actionPerformed(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser("D:");        // 选择文件的默认路径是“D:”盘
    ExampleFileFilter filter = new ExampleFileFilter();       // 实例化一个文件过滤器

    int iTypeFile = getFileType();                            // 得到所需传输文件的类型
    switch(iTypeFile)
    {
    case 0:                                                   // QuickTime文件
      filter.addExtension("mov");                             // 设置文件扩展名
      filter.setDescription("QuickTime Files");               // 设置文件的类型描述
      break;
    case 1:                                                   // 音频文件
      filter.addExtension("au");
      filter.addExtension("wav");
      filter.setDescription("Audio Files");
      break;
    case 2:                                                   // MPEG文件
      filter.addExtension("mpg");
      filter.addExtension("mpeg");
      filter.setDescription("MPEG Files");
      break;
    }
    fileChooser.setFileFilter(filter);

    int retVal = fileChooser.showOpenDialog(this);             // 打开文件选择对话框

    if(retVal == JFileChooser.APPROVE_OPTION){
        fileName = fileChooser.getSelectedFile().getAbsolutePath();          // 得到所选文件
        textFile.setText(fileName);                    // 将文件名显示到界面上
    }
  }

  // 响应“传输”按钮的点击消息，开始传输数据
  void buttonBeginTransmit_actionPerformed(ActionEvent e) {
    String strIPAddr = textIPAdd1.getText()+"."+textIPAdd2.getText()+"."+textIPAdd3.getText()+"."+textIPAdd4.getText();
                    // 组合得到完整的IP地址
    String strPort = textPort.getText();     // 得到端口地址

    fileName = textFile.getText();                             // 得到文件名

    fileName = "file:/" + fileName;                            // 加上文件标识，以便媒体定位器确认数据类型
    MediaLocator medLoc = new MediaLocator(fileName);          // 用本机的一个磁盘文件作为待传输的媒体数据

    Format fmt = null;
    rtpTransmit = new RTPTransmit(medLoc,strIPAddr,strPort,fmt);

    String result = rtpTransmit.start();             // 开始传输

    if (result != null) {                            // 显示传输错误
      System.out.println("Error : " + result);
    }
    else {
      System.out.println("Start transmission ...");
    }
  }

  // 响应“停止”按钮的点击消息，停止发送数据
  void buttonStopTransmit_actionPerformed(ActionEvent e) {
    if(rtpTransmit == null)
      return;

    rtpTransmit.stop();         // 停止传输

    System.out.println("...transmission ended.");
  }

  // 关闭窗口，退出程序
  void this_windowClosing(WindowEvent e) {
    System.exit(0);
  }

  // 主函数
  public static void main(String [] args) {
    new MainFrame();
  }

}

