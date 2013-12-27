package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;
import com.google.android.gms.internal.cj;

public class SignUpState extends AbstractState {

	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterImpl();
	private String username = null;
	private String password = null;
	private String mail = null;
	private String phone = null;
	private boolean toConfirmUsername = false;
	private boolean toChoosePassword = false;
	private boolean toConfirmPassword = false;
	private boolean toChooseMail = false;
	private boolean choosingToChooseMail = false;

	public SignUpState(VoiceInterpreterActivity context) {
		super(context);
		this.defaultMessage = "Diga el nombre de usuario que desea tener";
		this.message = defaultMessage;
		this.context = context;
		stripAccents = false;
	}

	public void handleResults(List<String> results) {
		if (username == null) {
			username = results.get(0);
			this.message = "Responda sí, o no. ¿Desea que su nombre de usuario sea "
					+ username + "?";
			toConfirmUsername = true;
			stripAccents = true;
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
		}
		if(toChoosePassword){
			toChoosePassword=false;
			password = results.get(0);
			this.message = "¿Desea que su nombre de contraseña sea "
					+ password + "?";
			stripAccents = true;
			toConfirmPassword = true;
		}
		if(toConfirmPassword){
			toConfirmPassword = false;
			if (stringPresent(results, "no")) {
				toChoosePassword();
			}
			if (stringPresent(results, "si")) {
				this.message = "¿Desea agregar una dirección de mail?";
				choosingToChooseMail  = true;
			}
		}
		if(choosingToChooseMail){
			choosingToChooseMail = false;
			if (stringPresent(results, "no")) {
				this.message = "Gracias por registrarse";
			}
			if (stringPresent(results, "si")) {
				this.message = "Diga su dirección de mail";
				toChooseMail  = true;
			}
		}
		if(toChooseMail){
			toChooseMail = false;
			mail = results.get(0);
			this.message = "¿Es su dirección de mail "
					+ mail + "?";
		}

	}

	private void toChoosePassword() {
		toChoosePassword = true;
		message = "Diga la contraseña que desea tener";
	}

	private void resetData() {
		username = null;
		password = null;
		mail = null;
		phone = null;
		toConfirmUsername = false;
		toChoosePassword = false;
		toConfirmPassword = false;
		toChooseMail = false;
		choosingToChooseMail = false;
		this.message = defaultMessage;
	}

	private class CheckUsernameAvailableTask extends
			AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... args) {
			String username = args[0];
			boolean validUsername = userServiceAdapter.usernameInUse(username);
			if (validUsername) {
				toChoosePassword();
			}else{
				resetData();
				message = "El nombre de usuario ya está en uso, diga otro";
			}
			context.sayMessage();
			return message;
		}

	}
}
