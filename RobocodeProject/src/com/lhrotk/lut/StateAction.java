package com.lhrotk.lut;

import java.util.Arrays;

/**
 * 
 * @author lhrotk
 *
 */
public class StateAction {
	private final int[] stateAction;
	public  final int length;
	
	public StateAction(int[] initializer) {
		this.stateAction = initializer;
		this.length = initializer.length;
	}
	
	public int[] getStateAction() {
		return this.stateAction;
	}
	
	public int getElement(int index) {
		return this.stateAction[index];
	}
	
	@Override
	  public boolean equals(Object o) {
	    if (this == o) {
	    	return true;
	    }
	    if (o == null || this.getClass() != o.getClass()) {
	    	return false;
	    }
	 
	    StateAction oprehand = (StateAction) o;
	 
	    if(this.length != oprehand.length) {
	    	return false;
	    }else {
	    	for(int i=0; i<this.length; i++) {
	    		if(this.stateAction[i]!=oprehand.getElement(i)) {
	    			return false;
	    		}
	    	}
	    	return true;
	    }
	  }
	 
	  @Override
	  public int hashCode() {
	    return Arrays.toString(this.stateAction).hashCode();
	  }
	  
}
