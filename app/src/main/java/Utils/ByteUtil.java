package Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这是从网上收集并对其进行提炼形成的一个最终结果，包括如下主要方法： 1.将一个int型转换成4字节的数组 2.将一个4字节的数组转换成int类型
 * 3.将源字节数组src复制到目标字节数组中
 * 4.在字符串转换成字节数组的基础之上，不位置不够totalLength情况之下，右侧补placeholder字符 5.BCD码的互相转换
 * 6.hex与byte数组间的互相转换
 * 
 * @author 刘军海
 * 
 */
public class ByteUtil {
	
	/**
	 * 整型转换为4位字节数组
	 * 
	 * @param intValue
	 * @return
	 */
	public static byte[] int2Byte(int intValue) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
		}
		return b;
	}

	/**
	 * 4位字节数组转换为整型
	 * 
	 * @param b
	 * @return
	 */
	public static int byte2Int(byte[] b) {
		int intValue = 0;
		for (int i = 0; i < b.length; i++) {
			intValue += (b[i] & 0xFF) << (8 * (3 - i));
		}
		return intValue;
	}

	/**
	 * 将源字节数组src复制到目标字节数组中
	 * 
	 * @param src
	 * @param srcOffset
	 * @param dst
	 * @param dstOffset
	 * @param count
	 */
	public static void copyByte(byte[] src, int srcOffset, byte[] dst,
			int dstOffset, int count) {
		if (dstOffset + count > dst.length) {
			throw new RuntimeException("目标字节数组所分配的长度不够");
		}
		if (srcOffset + count > src.length) {
			throw new RuntimeException("源字节数组的长度与要求复制的长度不符");
		}
		for (int i = 0; i < count; i++) {
			dst[dstOffset + i] = src[srcOffset + i];
		}
	}

	/**
	 * 在字符串转换成字节数组的基础之上，不位置不够totalLength情况之下，右侧补placeholder字符
	 * 
	 * @param bytes
	 *            字符串转换的字节数组，注意编码格式
	 * @param totalLength
	 *            总长度
	 * @param placeholder
	 *            长度不够总长度情况下被的字符
	 * @return
	 */
	public static byte[] padRight(byte[] bytes, int totalLength,
			char placeholder) {
		byte[] b = new byte[totalLength];
		copyByte(bytes, 0, b, 0, bytes.length);
		for (int i = bytes.length; i < totalLength; i++) {
			b[i] = (byte) placeholder;
		}
		return b;
	}

	/**
	 * 将一个char字符转换成一个字节类型
	 * 
	 * @param c
	 * @return
	 */
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * 把16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	
	/** 
     * long to byte[] 
     *  
     * @param s 
     *            long 
     * @return byte[] 
     * */  
    public static byte[] longToByteArray(long s) {  
        byte[] targets = new byte[2];  
        for (int i = 0; i < 8; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((s >>> offset) & 0xff);  
        }  
        return targets;  
    }  
	
	public static long hexStringToLong(String hex){
		byte[] bs = new byte[8];
		byte[] _bs = hexStringToByte(hex);
		ByteUtil.copyByte(_bs, 0, bs, 8-_bs.length, _bs.length);
		return readLong(bs, 0);
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		return bytesToHexString(bArray,false);
	}
	public static final String bytesToHexString(byte[] bArray,boolean formated) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
			if(formated)sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * 把字节数组转换为对象
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object bytesToObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	/**
	 * 把可序列化对象转换成字节数组
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final byte[] objectToBytes(Serializable s) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream ot = new ObjectOutputStream(out);
		ot.writeObject(s);
		ot.flush();
		ot.close();
		return out.toByteArray();
	}

	/**
	 * 将一个可序列化的对象转换成hex字符串
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final String objectToHexString(Serializable s)
			throws IOException {
		return bytesToHexString(objectToBytes(s));
	}

	/**
	 * 将一个hex字符串转换成可序列化的对象
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final Object hexStringToObject(String hex)
			throws IOException, ClassNotFoundException {
		return bytesToObject(hexStringToByte(hex));
	}

	/**
	 * BCD码转为10进制串(阿拉伯数据)
	 * @param: BCD码
	 * @return: 10进制串
	 */
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * 10进制串转为BCD码
	 * @param: 10进制串
	 * @return: BCD码
	 */
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}
	/**
	 * 每行打印10个
	 * @param bytes
	 */
	public static String p(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		String s = "";
		for(int i=0;i<bytes.length;i++){
			if(i>0 && i % 10 == 0){
				System.out.println();
			}
			s = Integer.toHexString(bytes[i])+"  ";
			sb.append(s);
			System.out.print(s);
		}
		return sb.toString();
	}
	/**
	 * 将一个int类型的值放到指定的字节数组bytes中的指定的位置(index)
	 * @param bytes 待操作的数组
	 * @param value 要放的值
	 * @param offset 相对于0的偏移量 
	 */
	public static void setByte(byte[] bytes,int value,int offset){
		bytes[offset] = (byte)value;
	}
	
	/**
	 * 将一个指定的字符串str以charset编码格式转换成字节数组，然后放到bytes数组中，不足count长度的，
	 * 则在后边补空格
	 * @param bytes
	 * @param str
	 * @param offset
	 * @param count
	 */
	public static void setString(byte[] bytes,String str,
			int offset,int count,String charset){
		try {
			//将字符串str转换成指定长度的字节数组，不足后补空格
			byte[] strB = str.getBytes(charset);
			byte[] strC = padRight(strB,count,' ');
			//将strC放到bytes以0为基的offset便移量，长度为count的字节数组中
			copyByte(strC, 0, bytes, offset, count);
		} catch (Exception e) {
		}
	}
	/**
	 * 将指定的整形变量value以4字节长度放到bytes字节数组中
	 * @param bytes
	 * @param value
	 * @param offset
	 */
	public static void setInt(byte[] bytes,int value,int offset){
		byte[] b = int2Byte(value);
		copyByte(b, 0, bytes, offset, 4);
	}
	
	/**
	 * 将整型转换为2字节数组并放到指定的数组bytes中
	 * @param bytes
	 * @param intValue
	 * @param offset
	 */
	public static void set2Int(byte[] bytes,int intValue,int offset){
		byte[] b = new byte[2];
		for(int i=0;i<2;i++){
			b[i] = (byte) (intValue >> 8 * (1 - i) & 0xFF);
		}
		copyByte(b, 0, bytes, offset, 2);
	}
	
	/**
	 * 从指定的字节数组bytes中读取2字节子数组并转换为int类型
	 * @param bytes
	 * @param offset
	 * @return
	 */
	public static int read2Int(byte[] bytes,int offset){
		int intValue = 0;
		byte[] b = new byte[2];
		copyByte(bytes, offset, b, 0, 2);
		for (int i = 0; i < 2; i++) {
			intValue += (b[i] & 0xFF) << (8 * (1 - i));
		}
		return intValue;
	}
	
	public static void setLong(byte[] bytes,long longValue,int offset){
		byte[] b = new byte[8];
		for(int i=0;i<8;i++){
			b[i] = (byte) (longValue >> 8 * (7 - i));
		}
		copyByte(b, 0, bytes, offset, 8);
		/*
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			out.writeLong(longValue);
			byte[] b = bout.toByteArray();
			bout.close();
			copyByte(b, 0, bytes, offset, 8);
		}catch(Exception e){}
		*/
	}
	public static long readLong(byte[] bytes,int offset){
		long intValue = 0;
		byte[] b = new byte[8];
		copyByte(bytes, offset, b, 0, 8);		
		for (int i = 0; i < 8; i++) {
			intValue |= (long)(b[i] & 0xff) << (8*(7-i));
		}
		return intValue;
		/*
		long longValue = 0l;
		try{
			byte[] b = new byte[8];
			copyByte(bytes,offset,b,0,8);
			ByteArrayInputStream bin = new ByteArrayInputStream(b);
			DataInputStream din = new DataInputStream(bin);
			longValue = din.readLong();
			bin.close();
		}catch(Exception e){}
		return longValue;
		*/
	}
	
	/**
	 * 将指定的日期对象压缩成7字节的bcd码并放到目标数组中
	 * @param bytes
	 * @param date
	 * @param offset
	 */
	public static void setBcdDate(byte[] bytes,Date date,int offset){
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
		String timesequence=ft.format(date);
		//将时间序列压缩成7字节的bcd码
		byte[] a = str2Bcd(timesequence);
		//将bcd字节数组中放到指定的bytes数组中
		copyByte(a, 0, bytes, offset, 7);
	}
	
	/**
	 * 从指定的字节数组bytes中读取第offset个字节
	 * @param bytes
	 * @param offset 相对于0的偏移量
	 * @return
	 */
	public static byte readByte(byte[] bytes, int offset){
		return bytes[offset];
	}
	/**
	 * 从指定的字节数组中读取出int 型值，该值占4个字节
	 * @param bytes
	 * @param offset
	 * @return
	 */
	public static int readInt(byte[] bytes,int offset){
		byte[] b = new byte[4];
		copyByte(bytes, offset, b, 0, 4);
		return byte2Int(b);
	}
	/**
	 * 从指定的字节数组中第offset位置开始读取count个字节的数组，并生成字符串
	 * @param bytes
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String readString(byte[] bytes,
			int offset,int count,String charset){
		try{
			String s = "";
			byte[] b = new byte[count];
			copyByte(bytes, offset, b, 0, count);
			s = new String(b,charset);
			return s;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String readBcdDate(byte[] bytes,int offset){
		byte[] b = new byte[7];
		copyByte(bytes, offset, b, 0, 7);
		return bcd2Str(b);
	}
	/**
	 * 从指定的字节流中读取日期类型，字节流的日期为7字节长的bcd码
	 * @param bytes
	 * @param offset
	 * @return
	 */
	public static Date readDate(byte[] bytes,int offset){
		String tmp = ByteUtil.readBcdDate(bytes, offset);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try{
			Date date = sdf.parse(tmp);
			return date;
		}catch(Exception e){}
		return null;
	}
	
	/** 
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit 
     */  
    public static byte[] getBooleanArray(byte b) {  
        byte[] array = new byte[8];  
        for (int i = 7; i >= 0; i--) {  
            array[i] = (byte)(b & 1);  
            b = (byte) (b >> 1);  
        }  
        return array;  
    }  
    
    /**
     * bit数组转String
     * @param b
     * @return
     */
    public static String booleanArray2String(byte[] b) { 
    	StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {  
        	sb.append(b[i]);
        }  
        return sb.toString();  
    }  
    
    /** 
     * 把byte转为字符串的bit 
     */  
    public static String byteToBit(byte b) {  
        return ""  
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)  
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)  
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)  
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);  
    }  
    
    /** 
     * 二进制字符串转byte 
     */  
    public static byte decodeBinaryString(String byteStr) {  
        int re, len;  
        if (null == byteStr) {  
            return 0;  
        }  
        len = byteStr.length();  
        if (len != 4 && len != 8) {  
            return 0;  
        }  
        if (len == 8) {// 8 bit处理  
            if (byteStr.charAt(0) == '0') {// 正数  
                re = Integer.parseInt(byteStr, 2);  
            } else {// 负数  
                re = Integer.parseInt(byteStr, 2) - 256;  
            }  
        } else {// 4 bit处理  
            re = Integer.parseInt(byteStr, 2);  
        }  
        return (byte) re;  
    }  
	public static void main(String[] args) {
		byte[] bs = new byte[]{39,16};
		System.out.println(read2Int(bs, 0));
	}
}

