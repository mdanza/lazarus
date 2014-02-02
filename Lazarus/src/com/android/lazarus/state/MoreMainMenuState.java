package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;

public class MoreMainMenuState extends AbstractState {

	private InternalState state;
	private DeactivateAccountTask task = new DeactivateAccountTask();

	private enum InternalState {
		AWAITING_USER_OPTION, DELETE_PHONE_DATA_SELECTED, DEACTIVATE_ACCOUNT_SELECTED
	}

	public MoreMainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = "Para llamar a un taxi diga uno, para borrar sus datos del celular diga dos, para modificar su contraseña diga tres, para desactivar su cuenta del sistema diga cuatro";
		this.defaultMessage = message;
		this.state = InternalState.AWAITING_USER_OPTION;
	}

	@Override
	public void handleResults(List<String> results) {
		if (state.equals(InternalState.AWAITING_USER_OPTION)) {
			if (containsNumber(results, 1)) {
				context.setState(new TaxiCallState(context));
				return;
			}
			if (containsNumber(results, 2)) {
				this.message = "¿Está seguro de que desea borrar los datos del celular?";
				this.state = InternalState.DELETE_PHONE_DATA_SELECTED;
				return;
			}
			if (containsNumber(results, 3)) {
				context.setState(new ModifyPasswordState(context));
				return;
			}
			if (containsNumber(results, 4)) {
				this.message = "¿Está seguro de que desea desactivar su cuenta del sistema?";
				this.state = InternalState.DEACTIVATE_ACCOUNT_SELECTED;
				return;
			}
		}
		if (state.equals(InternalState.DELETE_PHONE_DATA_SELECTED)) {
			if (stringPresent(results, "si")) {
				context.getSharedPreferences("usrpref", 0).edit().clear()
						.commit();
				String nextMessage = "Sus datos fueron borrados del celular.";
				State logInState = new LogInState(this.context, nextMessage);
				this.context.setState(logInState);
			}
			if (stringPresent(results, "no")) {
				message = defaultMessage;
				state = InternalState.AWAITING_USER_OPTION;
				return;
			}
		}
		if (state.equals(InternalState.DEACTIVATE_ACCOUNT_SELECTED)) {
			if (stringPresent(results, "si")) {
				message = "Espere mientras desactivamos su cuenta";
				if (task.getStatus() != AsyncTask.Status.RUNNING) {
					if (task.getStatus() == AsyncTask.Status.PENDING) {
						task.execute();
					} else {
						if (task.getStatus() == AsyncTask.Status.FINISHED) {
							task = new DeactivateAccountTask();
							task.execute();
						}
					}
				}
				return;
			}
			if (stringPresent(results, "no")) {
				message = defaultMessage;
				state = InternalState.AWAITING_USER_OPTION;
				return;
			}
		}
	}

	@Override
	protected void cancel() {
		MoreMainMenuState moreMainMenuState = new MoreMainMenuState(context);
		this.context.setState(moreMainMenuState);
		state = InternalState.AWAITING_USER_OPTION;
	}

	private class DeactivateAccountTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			UserServiceAdapter userServiceAdapter = new UserServiceAdapterImpl();
			if(isCancelled())
				return null;
			boolean success = userServiceAdapter.deactivateUser(context
					.getToken());
			String nextMessage = "";
			if (success) {
				nextMessage = "Su cuenta se desactivó con éxito";
			} else {
				nextMessage = "Hubo un error al desactivar su cuenta";
			}
			context.setState(new LogInState(context, nextMessage));
			return null;
		}

	}

	@Override
	public void onAttach() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void cancelAsyncTasks() {
		task.cancel(true);
	}

}
