package com.lin.main;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class NewJFrame extends javax.swing.JFrame {

	private JPanel panel;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() {
				NewJFrame inst = new NewJFrame();
				
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public NewJFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			pack();
			panel.addComponentListener(new ComponentListener()
			{

				public void componentHidden(ComponentEvent arg0)
				{
					// TODO Auto-generated method stub
					
				}

				public void componentMoved(ComponentEvent arg0)
				{
					// TODO Auto-generated method stub
					
				}

				public void componentResized(ComponentEvent arg0)
				{
					// TODO Auto-generated method stub
					
				}

				public void componentShown(ComponentEvent arg0)
				{
					// TODO Auto-generated method stub
					
				}
				
			});
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
