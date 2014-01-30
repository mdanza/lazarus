package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

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
	CheckUsernameAvailableTask checkUsernameAvailableTask = new CheckUsernameAvailableTask();

	public SignUpState(VoiceInterpreterActivity context) {
		super(context);
		this.defaultMessage = "Diga el nombre de usuario que desea tener";
		this.message = defaultMessage;
		this.context = context;
	}

	public SignUpState(VoiceInterpreterActivity context, String initialMessage) {
		super(context);
		this.defaultMessage = "Diga el nombre de usuario que desea tener";
		this.message = initialMessage + defaultMessage;
		this.context = context;
	}

	public void handleResults(List<String> results) {
		if (username == null) {
			username = results.get(0);
			this.message = "¿Desea que su nombre de usuario sea " + username
					+ "?";
			toConfirmUsername = true;
			return;
		}
		if (toConfirmUsername) {
			toConfirmUsername = false;
			if (stringPresent(results, "no")) {
				resetData(defaultMessage);
			}
			if (stringPresent(results, "si")) {
				message = "";
				String[] args = new String[1];
				args[0] = username;
				if (checkUsernameAvailableTask.getStatus() != AsyncTask.Status.RUNNING) {
					if (checkUsernameAvailableTask.getStatus() == AsyncTask.Status.PENDING) {
						checkUsernameAvailableTask.execute(args);
					} else {
						if (checkUsernameAvailableTask.getStatus() == AsyncTask.Status.FINISHED) {
							checkUsernameAvailableTask = new CheckUsernameAvailableTask();
							checkUsernameAvailableTask.execute(args);
						}
					}
				}
			}
			return;
		}
		if (toChoosePassword) {
			toChoosePassword = false;
			password = results.get(0);
			String passwordToBeUttered = password;
			if (isNumber(password)) {
				password = getNumber(password);
				passwordToBeUttered = getStringDigits(Integer.valueOf(password));
			}
			this.message = "¿Desea que su contraseña sea "
					+ passwordToBeUttered + "?";
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
				message = "Espere mientras guardamos sus datos";
				if (saveDataTask.getStatus() != AsyncTask.Status.RUNNING) {
					if (saveDataTask.getStatus() == AsyncTask.Status.PENDING) {
						saveDataTask.execute(args);
					} else {
						if (saveDataTask.getStatus() == AsyncTask.Status.FINISHED) {
							saveDataTask = new SaveDataTask();
							saveDataTask.execute(args);
						}
					}
				} else {
					message = "Espere mientras guardamos sus datos";
				}
			}
			return;
		}
	}

	private void toChoosePassword() {
		toChoosePassword = true;
		message = "Diga la contraseña que desea tener,, si quiere elegir un número, dígalo dígito a dígito ";
	}

	private void resetData(String message) {
		username = null;
		password = null;
		toConfirmUsername = false;
		toChoosePassword = false;
		toConfirmPassword = false;
		this.message = message;
	}

	private class CheckUsernameAvailableTask extends
			AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... args) {
			message = "Espere mientras cargamos sus datos por favor";
			String username = args[0];
			boolean validUsername = !userServiceAdapter.usernameInUse(username);
			if (validUsername) {
				toChoosePassword();
			} else {
				resetData("El nombre de usuario ya está en uso, diga otro");
			}
			context.sayMessage();
			return message;
		}
	}

	private class SaveDataTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			message = "Espere mientras guardamos sus datos";
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
							"Gracias por registrarse, " + generalInstructions);
					context.setState(mainMenuState);
					context.sayMessage();
				}
				if (result == null) {
					LogInState logInState = new LogInState(context, "Gracias por registrarse, ");
					context.setState(logInState);
					context.sayMessage();
				}
			}
			if (!success) {
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
