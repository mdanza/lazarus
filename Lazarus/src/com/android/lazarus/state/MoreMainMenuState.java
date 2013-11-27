package com.android.lazarus.state;

import java.util.List;

import android.content.SharedPreferences;

import com.android.lazarus.VoiceInterpreterActivity;

public class MoreMainMenuState extends AbstractState {
	
	int action;
	private final int DELETE = 2;

	MoreMainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = "Para llamar a un taxi diga uno, para borrar sus datos del celular diga dos";
		this.defaultMessage = message;
	}

	@Override
	public void handleResults(List<String> results) {
		if(numberPresent(results,2)){
			this.message = "¿Está seguro que desea borrar los datos del celular?";	
			this.action = DELETE;
			return;
		}
		if(stringPresent(results,"si")){
			if(this.action==DELETE){
				context.getSharedPreferences("usrpref", 0).edit().clear().commit();
				String nextMessage = "Sus datos fueron borrados del celular";
				State logInState = new LogInState(this.context,nextMessage);
				this.context.setState(logInState);
			}
		}

	}

}
