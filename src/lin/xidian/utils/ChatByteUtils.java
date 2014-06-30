package lin.xidian.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lin.xidian.file.Buffer;

public class ChatByteUtils
{	
	public static byte[] getBytes(Object...objs)
	{
		List<Byte> bytes = new ArrayList<Byte>();
		for(Object obj:objs)
		{
			if(obj instanceof Integer)
			{
				bytes.addAll(toCollection(intToBytes((Integer)obj)));
			}
			else if(obj instanceof Long)
			{
				bytes.addAll(toCollection(longToBytes((Long)obj)));
			}
			else if(obj instanceof Float)
			{
				bytes.addAll(toCollection(floatToBytes((Float)obj)));
			}
			else if(obj instanceof String)
			{
				bytes.addAll(toCollection(stringToBytes((String)obj)));
			}
			else if(obj instanceof Buffer)
			{
				bytes.addAll(toCollection(bufferToBytes((Buffer)obj)));
			}
		}
		byte[] array = new byte[bytes.size()];
		System.arraycopy(bytes, 0, array, 0, bytes.size());
		return array;
	}
	
	public static byte[] subBytes(byte[] b,int from,int to)
	{
		byte[] bb = new byte[to-from+1];
		for(int i=from;i<to+1;i++)
		{
			bb[i-from] = b[i];
		}
		return bb;
	}
	
	public Map getValues(byte[] bytes,String[] paramNames,Class[] types)
	{
		Map<String,Object> map = new HashMap<String,Object>();
		int index = 0;
		int i = 0;
		for(Class c:types)
		{
			if(c == Integer.class)
			{
				int value = bytesToInt(subBytes(bytes,index,index+4));
				map.put(paramNames[i], value);
				index += 4;
			}
			if(c == Float.class)
			{
				float value = bytesToFloat(subBytes(bytes,index,index+4));
				map.put(paramNames[i], value);
				index += 4;
			}
			if(c == Long.class)
			{
				long value = bytesToLong(subBytes(bytes,index,index+8));
				map.put(paramNames[i], value);
				index += 8;
			}
			if(c == String.class)
			{
				String value = bytesToSting(subBytes(bytes,index,bytes.length-1));
				map.put(paramNames[i], value);
				index = bytes.length;
			}
			if(c == Buffer.class)
			{
				Buffer value = bytesToBuffer(subBytes(bytes,index,bytes.length-1));
				map.put(paramNames[i], value);
				index = bytes.length;
			}
		}
		return map;
	}
	
	private Buffer bytesToBuffer(byte[] subBytes)
	{
		Buffer buffer = new Buffer();
		buffer.setBuffer(subBytes);
		return buffer;
	}

	public static byte[] intToBytes(int value)
	{
		byte[] result = new byte[4];
		result[0] = (byte)(value>>24);
		result[1] = (byte)(value>>16);
		result[2] = (byte)(value>>8);
		result[3] = (byte)value;
		return result;
	}
	
	public static List<Byte> toCollection(byte[] bytes)
	{
		List<Byte> list = new ArrayList<Byte>(bytes.length);
		for(byte b:bytes)
		{
			list.add(b);
		}
		return list;
		
	}
	
	public static int bytesToInt(byte[] bytes)
	{
		if(bytes.length!=4) throw new RuntimeException("int length wrong");
		boolean isFu = false;
		if((bytes[0] & 0x80) !=0)//¸ºÊý
		{
			isFu = true;
			bytes[0] &= 0x7f;
		}
		int result = 0;
		int[] res = new int[4];
		for(int i=0;i<4;i++)
		{
			res[i] = byteToInt(bytes[i]);
			result += res[i]*Math.pow(256, 3-i);
		}
		if(isFu)
		{
			return Integer.MIN_VALUE+result;
		}
		return result;
	}
	
	public static int byteToInt(byte value)
	{
		int res = (int)value;
		if(res<0)
		{
			return 256+res;
		}
		return res;
	}
	
	public static byte[] longToBytes(long value)
	{
		byte[] result = new byte[8];
		for(int i=7;i>=0;i--)
		{
			result[i] = (byte)(value%256);
			value >>= 8;
		}
		return result;
	}
	
	public static long bytesToLong(byte[] b)
	{
		if(b.length!=8) throw new RuntimeException("long length wrong");
		  return ((((long) b[0] & 0xff) << 56) 
	                 | (((long) b[1] & 0xff) << 48) 
	                 | (((long) b[2] & 0xff) << 40) 
	                 | (((long) b[3] & 0xff) << 32) 
	                 | (((long) b[4] & 0xff) << 24) 
	                 | (((long) b[5] & 0xff) << 16) 
	                 | (((long) b[6] & 0xff) << 8) 
	                 | (((long) b[7] & 0xff) << 0)); 
	}
	
	public static byte[] stringToBytes(String value)
	{
		return value.getBytes();
	}
	
	public static byte[] bufferToBytes(Buffer value)
	{
		return value.getBuffer();
	}
	
	public static String bytesToSting(byte[] bs)
	{
		StringBuilder sb = new StringBuilder();
		for(byte b:bs)
		{
			sb.append(byteToSting(b));
		}
		return sb.toString();
	}
	
	public static String byteToSting(byte b)
	{
		String res = Integer.toHexString(b & 0xff);
		if(res.length() == 1)
		{
			return '0'+res;
		}
		return res;
	}
	
	public static float bytesToFloat(byte[] b)
	{
	   if(b.length!=4) throw new RuntimeException("float length wrong");  
	   int l;
	   l=b[0];
	   l&=0xff;
	   l|=((long)b[1]<<8);
	   l&=0xffff;
	   l|=((long)b[2]<<16);
	   l&=0xffffff;
	   l|=((long)b[3]<<24);
	   l&=0xffffffffl;
	   return Float.intBitsToFloat(l);  
	}
	
	public static byte[] floatToBytes(float d)
	{
		  byte[] bytes = new byte[4];
		   int l = Float.floatToIntBits(d);
		   for(int i = 0; i < bytes.length; i++ ){
		    bytes[i]= new Integer(l).byteValue();
		    l=l>>8;
		   }
		   return bytes;
	}
	
	public static void main(String[] args)
	{
		int intValue = -910006546;
		byte[] bytes = intToBytes(intValue);
		System.out.println(intValue);
		System.out.println(bytesToSting(bytes));
		System.out.println(bytesToInt(bytes));
		
		System.out.println("LONG TEST");
		long longValue = -9999999999900000L;
		byte[] longBytes = longToBytes(longValue);
		System.out.println(longValue);
		System.out.println(longBytes);
		System.out.println(bytesToLong(longBytes));
		
	}
}
