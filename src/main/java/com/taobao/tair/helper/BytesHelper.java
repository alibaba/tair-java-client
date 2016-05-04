package com.taobao.tair.helper;

public class BytesHelper {
	public static boolean isInteger(String s) {
		boolean isOk = false;
		try {
			Integer.valueOf(s);
			isOk = true;
		} catch (NumberFormatException e) {
			isOk = false;
		}
		return isOk;
	}
	
	public static byte[] DoubleToBytes_With_Little_Endian(double number) {
		long tmp = Double.doubleToLongBits(number);
		byte[] bytes = new byte[8];
		for(int i = 0; i < 8; i++) {
			bytes[i] = (byte)(tmp & 0xFF);
			tmp = tmp >> 8;
		}
		return bytes;
	}
	
	public static double BytesToDouble_With_Little_Endian(byte[] bytes) {
		if (bytes == null || bytes.length != 8) {
			return 0;
		}
		
		long tmp = 0;
		for(int i = 0; i < 8; i++) {
			tmp = (tmp << 8) | (bytes[i] & 0xFF);
		}
		
		return Double.longBitsToDouble(tmp);
	}
	
	public static long DoubleToLong_With_Little_Endian(double dd) {
		return Long.reverseBytes(Double.doubleToLongBits(dd));
	}
	
	public static double LongToDouble_With_Little_Endian(long ll) {
		return Double.longBitsToDouble(Long.reverseBytes(ll));
	}
	
//	public static void main(String[] args) {
//		long start = System.currentTimeMillis();
//		for(long i = 0; i < 100000000; i++) {
//			double pp = Math.random() + Math.random() * 100 + Math.random() * 1000;
//			long ll = DoubleToLong_With_Little_Endian(pp);
//			double pp2 = LongToDouble_With_Little_Endian(ll);
//			if (pp != pp2) {
//				System.out.println("pp = " + pp + ", " + "pp2 = " + pp2);
//			}
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("time: " + (end - start));
//		System.out.println("over");
//	}
}
