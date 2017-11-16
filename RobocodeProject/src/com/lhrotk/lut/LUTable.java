package com.lhrotk.lut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import robocode.RobocodeFileOutputStream;

/**
 * 
 * @author lhrotk
 *
 */
public class LUTable implements LUTInterface {
	public HashMap<StateAction, Double> LUTable;
	private int inputDim;
	private int allState;// the amount of all possiable states
	private int[] argVariableFloor;
	private int[] argVariableCeiling;
	private int[] argVariableSpan; // Ceiling[i] - Floor[i]
	private StateAction lastState;
	private final double alpha = 0.2;
	private final double gamma = 0.9;
	private double maxQ;
	private boolean maxFound;

	/**
	 * Constructor funtion
	 * 
	 * @param argNumInputs
	 *            dim of state and action
	 * @param argVariableFloor
	 *            floor value of inputs
	 * @param argVariableCeiling
	 *            ceiling value of inputs
	 */
	public LUTable(int argNumInputs, int[] argVariableFloor, int[] argVariableCeiling) {
		int Acc = 1;
		this.argVariableCeiling = argVariableCeiling;
		this.argVariableFloor = argVariableFloor;
		this.argVariableSpan = new int[argNumInputs];
		for (int i = 0; i < argNumInputs; i++) {
			Acc *= (argVariableCeiling[i] - argVariableFloor[i] + 1);
			this.argVariableSpan[i] = argVariableCeiling[i] - argVariableFloor[i];
		}
		this.allState = Acc;
		this.inputDim = argNumInputs;
		this.initialiseLUT();

	}

	/**
	 * construct the class with saved file
	 * 
	 * @param argFileName
	 *            the file name
	 */
	public LUTable(String argFileName) {
		try {
			this.load(argFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * according to state-action, check the Q value if the state-action is new to
	 * the class, return 0
	 */
	@Override
	public double outputFor(double[] X) {
		StateAction tempState = new StateAction(this.inputQuantizer(X));
		if (this.LUTable.containsKey(tempState)) {
			return LUTable.get(tempState);
		} else {
			return 0.00;
		}

	}

	@Override
	public double train(double[] X, double argValue, boolean offPolicy, double epsilon) {
		double result = 0;
		if (this.lastState == null) {
			this.lastState = new StateAction(this.inputQuantizer(X));
		}else {
			StateAction currentState = new StateAction(this.inputQuantizer(X));//Q(s', a');
			if(offPolicy){
				this.loopUpAction(X, 1, 0);//important maxQ(s', a')
			}
			else{
				double[] stateAction = this.loopUpAction(X, 1, epsilon);//important maxQ(s', a')
				this.maxQ = this.outputFor(stateAction);
				result = stateAction[stateAction.length-1];
			}
			double val;//Q(s, a)
			if (!this.LUTable.containsKey(this.lastState)) {
				val = 0;
			} else {
				val = this.LUTable.get(this.lastState);
			}
			val += this.alpha * (argValue + this.gamma * this.maxQ - val);
			//System.out.println(Arrays.toString(X));
			this.LUTable.put(this.lastState, val);
     
			//System.out.println(this.lastState.getStateAction()[6]+" get bonus: "+argValue);
			this.lastState = currentState;
		}
		return result;
	}

	/**
	 * save the arguments to a file
	 */
	@Override
	public void save(File argFile) {
		PrintStream saveFile = null;
		try {
			saveFile = new PrintStream(new RobocodeFileOutputStream(argFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveFile.println(Arrays.toString(this.argVariableFloor));
		saveFile.println(Arrays.toString(this.argVariableCeiling));
		Iterator<StateAction> iter = this.LUTable.keySet().iterator();
		int counter = 0;
		while (iter.hasNext()) {
			StateAction key = (StateAction) iter.next();
			double val = this.LUTable.get(key);
			saveFile.println(Arrays.toString(key.getStateAction()));
			saveFile.println(val);
			counter++;
		}
		saveFile.close();
		System.out.println("--+ Number of LUT table entries saved is: " + counter);
	}

	/**
	 * load the arguments and initialize the class
	 */
	@Override
	public void load(String argFileName) throws IOException {
		FileInputStream inputFile = new FileInputStream(argFileName);
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputFile));
		this.argVariableFloor = Strings.toArray(inputReader.readLine());
		this.argVariableCeiling = Strings.toArray(inputReader.readLine());
		this.inputDim = this.argVariableCeiling.length;
		int Acc = 1;
		this.argVariableSpan = new int[this.inputDim];
		for (int i = 0; i < this.inputDim; i++) {
			Acc *= (argVariableCeiling[i] - argVariableFloor[i] + 1);
			this.argVariableSpan[i] = argVariableCeiling[i] - argVariableFloor[i];
		}
		this.allState = Acc;
		this.initialiseLUT();
		int counter = 0;
		while (inputReader.ready()) {
			StateAction newEntry = new StateAction(Strings.toArray(inputReader.readLine()));
			double val = Double.parseDouble(inputReader.readLine());
			this.LUTable.put(newEntry, val);
			counter++;
		}
		inputReader.close();
		System.out.println("--+ Number of LUT entries loaded was: " + counter);
	}

	@Override
	public void initialiseLUT() {
		this.LUTable = new HashMap<StateAction, Double>(this.inputDim);

	}

	@Override
	public int indexFor(double[] X) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] inputQuantizer(double[] input) {
		int[] result = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			result[i] = (int) Math.floor(input[i]);
		}
		return result;
	}

	public double[] loopUpAction(double[] stateAction, int actionDim, double epsilno) {
		if (Math.random() < epsilno) {
			for (int i = this.inputDim - actionDim; i < inputDim; i++) {
				stateAction[i] = this.argVariableFloor[i] + Math.floor(Math.random() * (this.argVariableSpan[i] + 1));
			}
			return stateAction;
		} else {
			List<double[]> result = new ArrayList<double[]>();
			this.maxFound = false;
			this.exploreBestAction(result, stateAction, actionDim, 0);
			return result.get((int) Math.floor(Math.random() * result.size()));
		}
	}

	public void exploreBestAction(List<double[]> result, double[] stateAction, int actionDim, int currentState) {
		if (currentState == actionDim) {
			// loop up the vector and return
			// System.out.println(Arrays.toString(stateAction));
			if (!maxFound) {
				this.maxQ = this.outputFor(stateAction);
				result.add(stateAction.clone());
				maxFound = true;
			} else {
				double tempResult = this.outputFor(stateAction);
				if (tempResult > this.maxQ) {
					result.removeAll(result);
					result.add(stateAction.clone());
					this.maxQ = tempResult;
				} else if (tempResult == this.maxQ) {
					result.add(stateAction.clone());
				}
			}
		} else {
			for (int i = this.argVariableFloor[this.inputDim - actionDim
					+ currentState]; i <= this.argVariableCeiling[this.inputDim - actionDim + currentState]; i++) {
				stateAction[this.inputDim - actionDim + currentState] = i;
				exploreBestAction(result, stateAction, actionDim, currentState + 1);
			}
		}

	}
}
