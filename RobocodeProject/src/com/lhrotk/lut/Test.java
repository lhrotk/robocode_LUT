package com.lhrotk.lut;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LUTable lut = new LUTable(6, new int[] {1,1,1,1,1,1}, new int[] {8,6,4,4,4,5});
		double[] stateAction = new double[] {1,5,1,1,1,0};
		System.out.println(Arrays.toString(lut.loopUpAction(stateAction, 1, 0)));
		File file = new File("data.properties");
		lut.save(file);
		LUTable lut2 = new LUTable("data.properties");
		System.out.println(Arrays.toString(lut2.loopUpAction(stateAction, 1, 0)));
	}

}
