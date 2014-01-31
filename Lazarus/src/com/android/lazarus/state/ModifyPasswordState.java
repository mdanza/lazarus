package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;

public class ModifyPasswordState extends AbstractState {
	private int oldPasswordAttempts = 0;
	private int confirmationAttempts = 0;
	private InternalState state;
	private String oldPassword;
	private AsyncTask<Void, Void, Void> task;
	private String newPassword;

	private enum InternalState {
		AWAITING_OLD_PASSWORD, AWAITING_NEW_PASSWORD, AWAITING_NEW_PASSWORD_CONFIRMATION
	}

	public ModifyPasswordState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.state = InternalState.AWAITING_OLD_PASSWORD;
		this.message = "Diga su contraseña actual";
		this.oldPassword = context.getSharedPreferences("usrpref", 0)
				.getString("password", null);
	}

	@Override
	protected void cancel() {
		context.setState(new ModifyPasswordState(context));
	}

	@Override
	protected void handleResults(List<String> results) {
		if (state.equals(InternalState.AWAITING_OLD_PASSWORD)) {
			boolean success = false;
			for (String attempt : results) {
				if (oldPassword.equals(attempt)) {
					success = true;
					break;
				}
			}
			if (success) {
				message = "Diga su nueva contraseña";
				state = InternalState.AWAITING_NEW_PASSWORD;
			} else {
				message = "Contraseña incorrecta";
				oldPasswordAttempts++;
				if (oldPasswordAttempts == 5)
					context.setState(new MainMenuState(context));
			}
			return;
		}
		if (state.equals(InternalState.AWAITING_NEW_PASSWORD)) {
			newPassword = results.get(confirmationAttempts);
			message = "Desea que su contraseña sea " + newPassword + " ?";
			state = InternalState.AWAITING_NEW_PASSWORD_CONFIRMATION;
		}
		if (state.equals(InternalState.AWAITING_NEW_PASSWORD_CONFIRMATION)) {
			if (stringPresent(results, "si")) {
				if (task == null) {
					task = new ChangePasswordTask();
					task.execute();
				}
				message = "Espere mientras cambiamos su contraseña";
				return;
			}
			if (stringPresent(results, "no")) {
				confirmationAttempts++;
				if (confirmationAttempts < results.size())
					handleResults(results);
				else {
					message = "Diga nuevamente su nueva contraseña";
					state = InternalState.AWAITING_NEW_PASSWORD;
					confirmationAttempts = 0;
					return;
				}
			}
		}
	}

	private class ChangePasswordTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... args) {
			UserServiceAdapter userServiceAdapter = new UserServiceAdapterImpl();
			boolean success = userServiceAdapter.modifyPassword(
					context.getToken(), newPassword);
			String nextMessage = "";
			if (success) {
				nextMessage = "Su contraseña se modificó con éxito,,";
				context.getSharedPreferences("usrpref", 0).edit()
						.putString("password", newPassword).commit();
			} else {
				nextMessage = "Hubo un error al modificar su contraseña,,";
			}
			context.setState(new MainMenuState(context, nextMessage));
			return null;
		}

	}

	@Override
	public void onAttach() {
		// TODO Auto-generated method stub
		
	}

}
