package lin.xidian.frame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lin.xidian.core.ManagerBase;
import lin.xidian.utils.InetUtils;

public class IpPortConfig extends JDialog
{
	private JTextField ip = new JTextField(20);
	private JTextField port = new JTextField(20);
	private JButton ok = new JButton("ȷ��");
	private JButton cancel = new JButton("ȡ��");
	
	private ManagerBase manager;
	
	
	private ActionListener listener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			JButton btn = (JButton)e.getSource();
			if(btn == ok)
			{
				//����IP��PORT
				String tip = ip.getText();
				String tport = port.getText();
				if(InetUtils.isIPAddress(tip)&&InetUtils.canUse(tport))
				{
					manager.setDestIp(tip);
					manager.setDestPort(Integer.parseInt(tport));
					manager.init();
				}
				IpPortConfig.this.dispose();
			}
			if(btn == cancel)
			{
				IpPortConfig.this.dispose();
			}
		}
	};
	
	
	public IpPortConfig(Frame owner,ManagerBase manager)
	{
	
		super(owner);
		this.manager = manager;
		
		JPanel frame = new JPanel();
		frame.setLayout(new BoxLayout(frame,BoxLayout.Y_AXIS));
		
		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new BoxLayout(ipPanel,BoxLayout.X_AXIS));
		ipPanel.add(new JLabel("��������ַ"));
		ipPanel.add(ip);
		
		JPanel portPanel = new JPanel();
		portPanel.setLayout(new BoxLayout(portPanel,BoxLayout.X_AXIS));
		portPanel.add(new JLabel("�������˿�"));
		portPanel.add(port);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel,BoxLayout.X_AXIS));
		btnPanel.add(ok);
		btnPanel.add(cancel);
		ok.addActionListener(listener);
		cancel.addActionListener(listener);
		
		ip.setText(manager.getDestIp());
		port.setText(String.valueOf(manager.getDestPort()));
		
		frame.add(ipPanel);
		frame.add(portPanel);
		frame.add(btnPanel);
		this.add(frame);
		this.setBounds(450, 260, 200, 120);
		this.setTitle("����������");
		this.setResizable(false);
		this.setVisible(true);
		
	}
}
