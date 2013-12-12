package com.android.lazarus.state;

import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.stubs.UserServiceAdapterStub;

public class LogInState extends AbstractState {

	private boolean usernamePresent = false;
	private List<String> usernames = null;
	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterStub();

	public LogInState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.defaultMessage = "Por favor diga su nombre de usuario, o diga nuevo para registrarse";
		this.message = defaultMessage;
	}

	public LogInState(VoiceInterpreterActivity context, String initialMessage) {
		this(context);
		this.message = initialMessage+this.defaultMessage;
	}

	private void initializeMainMenu(String initialText) {
		MainMenuState mainMenuState = new MainMenuState(context,initialText);
		context.setState(mainMenuState);		
	}

	public void handleResults(List<String> results) {
		if (usernamePresent == false) {
			this.usernames = results;
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
				context.getSharedPreferences("usrpref", 0).edit().putString("username",username).commit();
				context.getSharedPreferences("usrpref", 0).edit().putString("password",password).commit();
				initializeMainMenu("Gracias por loguearse, ");
			}else{
				this.message = "El nombre de usuario o la contraseña  no es correcto, por favor repita su nombre de usuario";
				this.usernamePresent=false;
			}
		}

	}


}
