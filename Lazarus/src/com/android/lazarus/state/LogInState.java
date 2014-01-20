package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;

public class LogInState extends AbstractState {

	private boolean usernamePresent = false;
	private List<String> usernames = null;
	private UserServiceAdapter userServiceAdapter;
	private boolean waitingForPassword = false;
	private boolean waitingForUsername = false;

	public LogInState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		userServiceAdapter = new UserServiceAdapterImpl();
		this.defaultMessage = "Para hablar, mantenga presionada la pantalla y espere a escuchar la señal, "
				+ "¿Es esta la primera vez que usa la aplicacion?";
		this.message = defaultMessage;
	}

	public LogInState(VoiceInterpreterActivity context, String initialMessage) {
		this(context);
		this.message = initialMessage + this.defaultMessage;
	}

	private void initializeMainMenu(String initialText) {
		MainMenuState mainMenuState = new MainMenuState(context, initialText);
		context.setState(mainMenuState);
	}

	public void handleResults(List<String> results) {
		if (usernamePresent == false) {
			message = defaultMessage;
			if (stringPresent(results, "si")) {
				SignUpState signUpState = new SignUpState(context);
				context.setState(signUpState);
				return;
			}
			if (stringPresent(results, "no")) {
				waitingForUsername = true;
				stripAccents = false;
				this.message = "Diga su nombre de usuario";
				return;
			}
			if (waitingForUsername) {
				waitingForUsername = false;
				stripAccents = true;
				this.usernames = results;
				usernamePresent = true;
				this.message = "¿Es su nombre de usuario " + usernames.get(0)
						+ "?";
			}
		} else {
			if (waitingForPassword) {
				List<String> passwords = results;
				String[] args = new String[3];
				for (int i = 0; i < passwords.size(); i++) {
					args[i] = passwords.get(i);
				}
				message = "";
				stripAccents = true;
				LogInTask logInTask = new LogInTask();
				logInTask.execute(args);
			}
			if (stringPresent(results, "no")) {
				cleanValuesForTryAgain();
				this.message = "Repita su nombre de usuario";
				stripAccents = false;
			}
			if (stringPresent(results, "si")) {
				this.message = "Diga su contraseña";
				waitingForPassword = true;
				stripAccents = false;
			}

		}

	}

	private void cleanValues() {
		usernamePresent = false;
		usernames = null;
		waitingForPassword = false;
		waitingForUsername = false;
		stripAccents = true;
		message = "";
	}
	
	private void cleanValuesForTryAgain() {
		usernamePresent = false;
		usernames = null;
		waitingForPassword = false;
		waitingForUsername = true;
		stripAccents = false;
		message = "";
	}

	private class LogInTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			String token = null;
			boolean validCredentialsFound = false;
			for (String arg : args) {
				if (!validCredentialsFound) {
					if(isNumber(arg)){
						arg = getNumber(arg);
					}
					String possibleToken = userServiceAdapter.login(
							usernames.get(0), arg);
					if (possibleToken != null) {
						token = possibleToken;
						context.getSharedPreferences("usrpref", 0).edit()
								.putString("username", usernames.get(0))
								.commit();
						context.getSharedPreferences("usrpref", 0).edit()
								.putString("password", arg).commit();
						initializeMainMenu("Gracias por loguearse, ");
						validCredentialsFound = true;
						cleanValues();
					}
				}
			}
			if (!validCredentialsFound) {
				cleanValuesForTryAgain();
				message = "Nombre de usuario o contraseña incorrecto, diga su nombre de usuario";
			}
			context.sayMessage();
			context.setToken(token);
			return token;
		}

	}

	@Override
	protected void restartState() {
		LogInState logInState = new LogInState(context);
		this.context.setState(logInState);		
	}

}
