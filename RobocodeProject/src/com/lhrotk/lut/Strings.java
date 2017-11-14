package com.lhrotk.lut;

public class Strings {
	public static int[] toArray(String inputString) {
		String[] arrays = inputString.substring(1, inputString.length()-1).split(", ");
		int[] result = new int[arrays.length];
		for(int i=0; i<arrays.length; i++) {
			result[i] = Integer.parseInt(arrays[i]);
		}
		return result;
	}
}
