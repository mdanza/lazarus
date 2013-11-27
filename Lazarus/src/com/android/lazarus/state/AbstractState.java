package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;

public abstract class AbstractState implements State {
	
	protected VoiceInterpreterActivity context;
	
	protected String message;
	
	protected String defaultMessage;
	
	protected List<String> results;

	public String getMessage(){
		return message;
	}
	
	AbstractState(VoiceInterpreterActivity context) {
		this.context = context;
		this.message = defaultMessage;
	}
	
	protected boolean stringPresent(List<String> results,String search){
		boolean stringPresent = false;
		for(String result:results){
			if(search.equals(result)){
				stringPresent = true;
			}
		}
		return stringPresent;
	}
	
	protected String getWhereAmIMessage(){
		return "Usted se encuentra en la concha de su hermana. ";
	}
	
	public void setResults(ArrayList<String> results){
		if(stringPresent(results,"donde estoy")){
			this.message = getWhereAmIMessage()+this.defaultMessage;
			return;
		}
		handleResults(results);
	}

	abstract protected void handleResults(ArrayList<String> results);

}
