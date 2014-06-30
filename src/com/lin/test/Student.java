package com.lin.test;

public class Student extends Person implements Comparable
{
	
	private long num;
	private String school;
	private String className;
	
	
	

	public long getNum()
	{
		return num;
	}




	public void setNum(long num)
	{
		this.num = num;
	}




	public String getSchool()
	{
		return school;
	}




	public void setSchool(String school)
	{
		this.school = school;
	}




	public String getClassName()
	{
		return className;
	}




	public void setClassName(String className)
	{
		this.className = className;
	}




	public void work()
	{
		

	}




	public boolean compare(Object obj)
	{
		
		return false;
	}

}
