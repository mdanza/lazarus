package com.android.lazarus.state;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;

public class SignUpState extends AbstractState {

	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterImpl();
	private String username = null;
	private String password = null;
	private boolean toConfirmUsername = false;
	private boolean toChoosePassword = false;
	private boolean toConfirmPassword = false;

	public SignUpState(VoiceInterpreterActivity context) {
		super(context);
		this.defaultMessage = "Diga el nombre de usuario que desea tener";
		this.message = defaultMessage;
		this.context = context;
		stripAccents = false;
	}

	public SignUpState(VoiceInterpreterActivity context, String initialMessage) {
		super(context);
		this.defaultMessage = "Diga el nombre de usuario que desea tener";
		this.message = initialMessage + defaultMessage;
		this.context = context;
		stripAccents = false;
	}

	public void handleResults(List<String> results) {
		if (username == null) {
			username = results.get(0);
			this.message = "¿Desea que su nombre de usuario sea " + username
					+ "?";
			toConfirmUsername = true;
			stripAccents = true;
			return;
		}
		if (toConfirmUsername) {
			toConfirmUsername = false;
			if (stringPresent(results, "no")) {
				resetData();
			}
			if (stringPresent(results, "si")) {
				stripAccents = false;
				message = "";
				CheckUsernameAvailableTask checkUsernameAvailableTask = new CheckUsernameAvailableTask();
				String[] args = new String[1];
				args[0] = username;
				checkUsernameAvailableTask.doInBackground(args);
			}
			return;
		}
		if (toChoosePassword) {
			toChoosePassword = false;
			password = results.get(0);
			this.message = "¿Desea que su contraseña sea " + password
					+ "?";
			stripAccents = true;
			toConfirmPassword = true;
			return;
		}
		if (toConfirmPassword) {
			toConfirmPassword = false;
			if (stringPresent(results, "no")) {
				toChoosePassword();
			}
			if (stringPresent(results, "si")) {
				SaveDataTask saveDataTask = new SaveDataTask();
				String[] args = new String[3];
				args[0] = username;
				args[1] = password;
				saveDataTask.doInBackground(args);
			}
			return;
		}
	}

	private void toChoosePassword() {
		toChoosePassword = true;
		message = "Diga la contraseña que desea tener";
	}

	private void resetData() {
		username = null;
		password = null;
		toConfirmUsername = false;
		toChoosePassword = false;
		toConfirmPassword = false;
		this.message = defaultMessage;
	}

	private class CheckUsernameAvailableTask extends
			AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... args) {
			String username = args[0];
			boolean validUsername = !userServiceAdapter.usernameInUse(username);
			if (validUsername) {
				toChoosePassword();
			} else {
				resetData();
				message = "El nombre de usuario ya está en uso, diga otro";
			}
			context.sayMessage();
			return message;
		}
	}

	private String getPhoneNumber() {
		TelephonyManager tMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	private class SaveDataTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			String username = args[0];
			String password = args[1];
			boolean success = userServiceAdapter.register(username, password,
					"");
			String result = null;
			if (success) {
				result = userServiceAdapter.login(args[0], args[1]);
				if (result != null) {
					context.setToken(result);
					context.getSharedPreferences("usrpref", 0).edit()
							.putString("username", username).commit();
					context.getSharedPreferences("usrpref", 0).edit()
							.putString("password", password).commit();
					MainMenuState mainMenuState = new MainMenuState(context,
							"Gracias por registrarse, "+instructions);
					context.setState(mainMenuState);
					context.sayMessage();
				}
			}
			if (!success || result == null) {
				SignUpState signUpState = new SignUpState(context,
						"Ha ocurrido un error al registrar sus datos, ");
				context.setState(signUpState);
				context.sayMessage();
			}
			return message;
		}
	}

	@Override
	protected void restartState() {
		SignUpState signUpState = new SignUpState(context);
		context.setState(signUpState);
		
	}
}
