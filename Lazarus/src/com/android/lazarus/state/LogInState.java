package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterStub;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterStub;

public class LogInState extends AbstractState {

	private boolean usernamePresent = false;
	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterStub();
	private AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterStub();

	public LogInState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = "Bienvenido a lázarus, para hablar al sistema presione la pantalla. Por favor diga su nombre de usuario, o diga nuevo para registrarse";
	}

	public void setResults(ArrayList<String> results) {
		if(whereAmIPresent(results)){
			this.message = getWhereAmIMessage();
			return;
		}
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
			boolean correctCredentials = false;
			List<String> usernames = this.results;
			List<String> passwords = results;
			for(int i=0;i<usernames.size();i++){
				for(int j=0;j<passwords.size();j++){
					if(userServiceAdapter.login(usernames.get(i), passwords.get(j))==true){
						correctCredentials=true;
					}
				}
			}
			if(correctCredentials==true){
				MainMenuState mainMenuState = new MainMenuState(context,this.getClass());
				context.setState(mainMenuState);
			}else{
				this.message = "El nombre de usuario o la contraseña no es correcto, por favor repita su nombre de usuario";
				this.usernamePresent=false;
			}
		}

	}

}
