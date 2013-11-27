package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterStub;
import com.android.lazarus.sharedpreference.ObscuredSharedPreferences;

public class LogInState extends AbstractState {

	private boolean usernamePresent = false;
	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterStub();

	public LogInState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.defaultMessage = "Por favor diga su nombre de usuario, diga nuevo para registrarse, o diga dónde estóy para conocer su ubicación actual en cualquier momento";
		this.message = "Bienvenido a lázarus, "+this.defaultMessage;
	}

	private void initializeMainMenu(String initialText) {
		MainMenuState mainMenuState = new MainMenuState(context,initialText);
		context.setState(mainMenuState);		
	}

	public void handleResults(ArrayList<String> results) {
		if (usernamePresent == false) {
			this.results = results;
			for(String result:results){
				if("nuevo".equals(result)){
					SignUpState signUpState = new SignUpState(context);
					context.setState(signUpState);
				}
			}
			usernamePresent = true;
			this.message = "Por favor diga su contraseña";
		} else {
			String username = null;
			String password = null;
			boolean correctCredentials = false;
			List<String> usernames = this.results;
			List<String> passwords = results;
			for(int i=0;i<usernames.size();i++){
				for(int j=0;j<passwords.size();j++){
					if(userServiceAdapter.login(usernames.get(i), passwords.get(j))==true){
						username = usernames.get(i);
						password = passwords.get(j);
						correctCredentials=true;
					}
				}
			}
			if(correctCredentials==true){
				context.getPreferences(0).edit().putString("username",username).commit();
				context.getPreferences(0).edit().putString("password",password).commit();
				initializeMainMenu("Gracias por loguearse, ");
			}else{
				this.message = "El nombre de usuario o la contraseña no es correcto, por favor repita su nombre de usuario";
				this.usernamePresent=false;
			}
		}

	}


}
