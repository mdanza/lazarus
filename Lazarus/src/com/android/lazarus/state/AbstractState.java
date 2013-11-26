package com.android.lazarus.state;

import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;

public abstract class AbstractState implements State {
	
	VoiceInterpreterActivity context;
	
	String message;
	
	List<String> results;

	public String getMessage(){
		return message;
	}
	
	AbstractState(VoiceInterpreterActivity context) {
		this.context = context;
	}
	
	boolean whereAmIPresent(List<String> results){
		boolean whereAmIPresent = false;
		for(String result:results){
			if("donde estoy".equals(result)){
				whereAmIPresent = true;
			}
		}
		return whereAmIPresent;
	}
	
	String getWhereAmIMessage(){
		return "En el otro del mundo";
	}

}
