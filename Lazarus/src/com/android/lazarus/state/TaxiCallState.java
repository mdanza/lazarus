package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Taxi;

public class TaxiCallState extends AbstractState {
	private InternalState state;
	private List<Taxi> taxiOptions;

	private enum InternalState {
		SEARCHING_OPTIONS, WAITING_USER_DECISION, NO_OPTIONS
	}

	public TaxiCallState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.state = InternalState.SEARCHING_OPTIONS;
	}

	@Override
	protected void restartState() {
		state = InternalState.SEARCHING_OPTIONS;
	}

	@Override
	protected void handleResults(List<String> results) {
		if (state.equals(InternalState.WAITING_USER_DECISION)) {
			for (int i = 0; i < taxiOptions.size(); i++) {
				if (containsNumber(results, i + 1)) {
					context.makeCall(taxiOptions.get(i).getNumber());
					context.setState(new MainMenuState(context));
				}
			}
		}
		giveInstructions();
	}

	private void giveInstructions() {
		if (state.equals(InternalState.SEARCHING_OPTIONS)) {
			new FindTaxiOptions().execute();
			message = "Buscando opciones de taxi";
			context.speak(message);
		}
		if (state.equals(InternalState.WAITING_USER_DECISION)) {
			message = "Las opciones encontradas son,";
			for (int i = 0; i < taxiOptions.size(); i++) {
				message += "diga " + (i + 1) + " para llamar a "
						+ taxiOptions.get(i).getCompanyName() + ",,";
			}
			context.speak(message);
		}
		if (state.equals(InternalState.NO_OPTIONS)) {
			message = "No se encontraron opciones de taxi";
			context.speak(message);
			context.setState(new MainMenuState(context));
		}
	}

	private class FindTaxiOptions extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			taxiOptions = null;
			state = InternalState.NO_OPTIONS;
			giveInstructions();
			return null;
		}

	}
}
