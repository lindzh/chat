package lin.xidian.frame;

import javax.swing.JOptionPane;

public class ChooseTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		/*
		int op = JOptionPane.showConfirmDialog(null, "你确定退出吗？YES", "提示", JOptionPane.YES_NO_OPTION);
		System.out.println("YES:"+op);
		op = JOptionPane.showConfirmDialog(null, "你确定退出吗？NO", "提示", JOptionPane.YES_NO_OPTION);
		System.out.println("NO:"+op);
		
		System.out.println("YES OPTION:"+JOptionPane.YES_OPTION);
		System.out.println("NO OPTION:"+JOptionPane.NO_OPTION);
		*/
		Integer[] options = new Integer[]{JOptionPane.OK_OPTION,JOptionPane.CANCEL_OPTION};
		int rst = JOptionPane.showOptionDialog(null, "你想退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, JOptionPane.OK_OPTION);
		System.out.println("OP:"+rst);
	
	}

}
