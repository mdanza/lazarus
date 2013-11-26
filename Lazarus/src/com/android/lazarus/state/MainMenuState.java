package com.android.lazarus.state;

import java.util.ArrayList;

import com.android.lazarus.VoiceInterpreterActivity;

public class MainMenuState extends AbstractState {
	
	String defaultMessage = "Para ingresar una direccion de destino diga uno, "
			+ "Para llamar a un taxi diga dos,"
			+ "Para modificar sus datos de usuario diga tres,"
			+ "Para realizar un logout diga cuatro"
			+ "Para obtener su ubicación actual en cualquier momento diga, donde estoy.";
	

	public MainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = defaultMessage;
	}

	public MainMenuState(VoiceInterpreterActivity context,
			Class<? extends AbstractState> comingFrom) {
		super(context);
		this.context = context;
		if(LogInState.class.equals(comingFrom)){
			this.message = "Gracias por loguearse. "+defaultMessage;
		}
	}

	public void setResults(ArrayList<String> results) {
		if(whereAmIPresent(results)){
			this.message = getWhereAmIMessage();
			return;
		}
		this.message = results.get(0);
	}

}
