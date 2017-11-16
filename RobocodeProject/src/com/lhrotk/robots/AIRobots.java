package com.lhrotk.robots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.lhrotk.lut.LUTable;
import com.lhrotk.lut.StateAction;
import com.lhrotk.lut.Strings;

import robocode.*;

public class AIRobots extends AdvancedRobot {
	LUTable lut;
	double currentAction;
	double[] rawInputs;
	double reward;
	int lastFoundEnemy;
	int battleResult[];
	List<Double> history;
	
	public void saveInt(File argFile) {
		PrintStream saveFile = null;
		try {
			saveFile = new PrintStream(new RobocodeFileOutputStream(argFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0; i<100; i++) {
			saveFile.println(battleResult[i]);
			//System.out.println(this.battleResult[this.getRoundNum()%100]);
		}
		saveFile.close();
	}
	
	public void saveList(File argFile) {
		PrintStream saveFile = null;
		try {
			saveFile = new PrintStream(new RobocodeFileOutputStream(argFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0; i<this.history.size(); i++) {
			saveFile.println(this.history.get(i));
		}
		saveFile.close();
	}
	
	public void loadInt(String argFileName) throws IOException {
		FileInputStream inputFile = new FileInputStream(argFileName);
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputFile));
		this.battleResult = new int[100];
		int counter = 0;
		while (inputReader.ready()) {
			this.battleResult[counter++] = Integer.parseInt(inputReader.readLine());
		}
		inputReader.close();
	}
	
	public void loadList(String argFileName) throws IOException {
		FileInputStream inputFile = new FileInputStream(argFileName);
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputFile));
		this.history = new ArrayList<Double>();
		int counter = 0;
		while (inputReader.ready()) {
			this.history.add(Double.parseDouble(inputReader.readLine()));
		}
		inputReader.close();
	}
	

	public void parseAction() {
		if (this.currentAction == 1) {
			ahead(100);
		} else if (this.currentAction == 2) {
			back(100);
		} else if (this.currentAction == 4) {
			turnRight(this.rawInputs[4] + 90);
			ahead(100);
		} else if (this.currentAction == 5) {
			turnRight(this.rawInputs[4] - 90);
			ahead(100);
		} else if (this.currentAction == 3) {
			turnRight(this.rawInputs[4]);
			fire(3);
		} else if (this.currentAction == 6) {
			turnLeft(90);
			ahead(100);
		} else if (this.currentAction == 7) {
			turnRight(90);
			ahead(100);
		} else if (this.currentAction == 8) {
			turnRight(180);
			ahead(100);
		}
	}

	public double[] getState(double[] rawInputs) {
		double[] result = new double[rawInputs.length];
		result[0] = Math.floor(rawInputs[0] / 100 + 1);
		result[1] = Math.floor(rawInputs[0] / 100 + 1);
		if (rawInputs[2] <= 50) {
			result[2] = 1;
		} else if (rawInputs[2] > 200) {
			result[2] = 3;
		} else {
			result[2] = 2;
		}

		if (rawInputs[3] < 90) {
			result[3] = 1;
		} else if (rawInputs[3] < 180) {
			result[3] = 2;
		} else if (rawInputs[3] < 270) {
			result[3] = 3;
		} else {
			result[3] = 4;
		}
		if (rawInputs[4] < -90) {
			result[4] = 1;
		} else if (rawInputs[4] < 0) {
			result[4] = 2;
		} else if (rawInputs[4] < 90) {
			result[4] = 3;
		} else {
			result[4] = 4;
		}

		if (rawInputs[5] < -90) {
			result[5] = 1;
		} else if (rawInputs[5] < 0) {
			result[5] = 2;
		} else if (rawInputs[5] < 90) {
			result[5] = 3;
		} else {
			result[5] = 4;
		}

		result[6] = rawInputs[6];
		return result;
	}

	public AIRobots() {
		lut = new LUTable("data.properties");
		this.reward = 0;
		this.currentAction = 4;
		this.rawInputs = new double[] { 1, 1, 1, 1, 1, 1, 1 };
		try {
			this.loadList("history");
			this.loadInt("winResult");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		this.battleResult[this.getRoundNum()%100]=0;
		while (true) {
			this.reward = 0;
			//System.out.println(Arrays.toString(this.getState(rawInputs)));
			this.parseAction();
			turnGunRight(360);
			//System.out.println(this.reward+", action: "+this.currentAction);
			this.currentAction = this.lut.loopUpAction(this.getState(rawInputs), 1, 0.05)[6];// decide a
			this.rawInputs[6] = this.currentAction;
			this.lut.train(this.getState(rawInputs), 0, true, 1);
			//System.out.println(this.reward);
//			this.currentAction = this.lut.train(this.getState(rawInputs), this.reward, false, 0.2);
//			this.rawInputs[6] = this.currentAction;
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		rawInputs[0] = this.getX();
		rawInputs[1] = this.getY();
		rawInputs[2] = e.getDistance();
		rawInputs[3] = this.getHeading();
		rawInputs[4] = e.getBearing();
		rawInputs[5] = e.getHeading();
	}

	public void onRoundEnded(RoundEndedEvent e) {
		System.out.println("entries: " + this.lut.LUTable.size());
		File file = new File("data.properties");
		File fileH = new File("history");
		File fileW = new File("winResult");
		this.lut.save(file);
		if(this.getRoundNum()%100==99) {
			int acc =0;
			for(int i=0; i<100; i++) {
				acc+= this.battleResult[i];
			}
			this.history.add(((double)acc)/100);
		}
		this.saveInt(fileW);
		this.saveList(fileH);
	}
	
	public void onWin(WinEvent e) {
		this.battleResult[this.getRoundNum()%100]=1;
		this.lut.train(this.getState(rawInputs), 5, true, 0.05);
		File fileH = new File("history");
		File fileW = new File("winResult");
		System.out.println("win");
		if(this.getRoundNum()%100==99) {
			int acc =0;
			for(int i=0; i<100; i++) {
				acc+= this.battleResult[i];
			}
			this.history.remove(this.history.size()-1);
			this.history.add(((double)acc)/100);
		}
		this.saveInt(fileW);
		this.saveList(fileH);//overwrite the wrong result
		File file = new File("data.properties");
		this.lut.save(file);
	}
	
	public void onDeathEvent(RobotDeathEvent e) {
		reward += 5;
	}

	public void onDeath() {
		this.reward -= 5;
	}

	public void onHitRobot(HitRobotEvent event) {
		this.reward -= 2;
	}

	public void onBulletHit(BulletHitEvent event) {
		this.reward += 3;
	}

	public void onHitByBullet(HitByBulletEvent event) {
		this.reward -= 3;
	}

	public void onHitWall(HitWallEvent e) {
		this.reward -= 2;
	}
}
