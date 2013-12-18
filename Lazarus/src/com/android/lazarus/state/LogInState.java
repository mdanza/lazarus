package com.android.lazarus.state;

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

	public LogInState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		userServiceAdapter = new UserServiceAdapterImpl();
		this.defaultMessage = "Por favor diga su nombre de usuario, o diga nuevo para registrarse";
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
			if (stringPresent(results, "nuevo")) {
				SignUpState signUpState = new SignUpState(context);
				context.setState(signUpState);
			} else {
				this.usernames = results;
				usernamePresent = true;
				this.message = "¿Es su nombre de usuario " + usernames.get(0)
						+ "?";
			}
		} else {
			if (waitingForPassword) {
				String username = usernames.get(0);
				String password = null;
				boolean correctCredentials = false;
				List<String> passwords = results;
				String[] args = new String[3];
				for (int i = 0; i < passwords.size(); i++) {
					args[i] = passwords.get(i);
				}
				message = "";
				LogInTask logInTask = new LogInTask(this.context);
				logInTask.doInBackground(args);
			}
			if (stringPresent(results, "no")) {
				cleanValues();
				this.message = "Repita su nombre de usuario";
			}
			if (stringPresent(results, "si")) {
				this.message = "Diga su contraseña";
				waitingForPassword = true;
			}

		}

	}

	private void cleanValues() {
		usernamePresent = false;
		usernames = null;
		waitingForPassword = false;
		message = "";
	}

	private class LogInTask extends AsyncTask<String, Void, String> {

		VoiceInterpreterActivity voiceInterpreterActivity;

		public LogInTask(VoiceInterpreterActivity context) {
			super();
			this.voiceInterpreterActivity = context;
		}

		@Override
		protected String doInBackground(String... args) {
			String token = null;
			boolean validCredentialsFound = false;
			for (String arg : args) {
				if (!validCredentialsFound) {
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
				cleanValues();
				message = "Nombre de usuario o contraeña incorrecto, diga su nombre de usuario";
			}
			voiceInterpreterActivity.sayMessage();
			voiceInterpreterActivity.setToken(token);
			return token;
		}

	}

}
