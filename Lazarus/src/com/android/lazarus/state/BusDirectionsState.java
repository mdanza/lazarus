package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.BusRide;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.Transshipment;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapter;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapterImpl;

public class BusDirectionsState extends LocationDependentState {
	private Point destination;
	private String initialMessage = "";
	private static final int NEEDED_ACCURACY = 50;
	private List<BusRide> busRides;
	private List<Transshipment> transshipments;

	public BusDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public BusDirectionsState(VoiceInterpreterActivity context,
			Point destination, String initialMessage) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		this.initialMessage = initialMessage;
		giveInstructions();
	}

	public BusDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		giveInstructions();
	}

	@Override
	protected void handleResults(List<String> results) {
		if (stringPresent(results, "listo")) {
			MainMenuState mainMenuState = new MainMenuState(context);
			context.setState(mainMenuState);
		}
	}

	@Override
	protected void giveInstructions() {
		if (busRides == null && transshipments == null) {
			new LoadBusRidesTask().execute();
		}

	}

	@Override
	protected void restartState() {
		// TODO Auto-generated method stub

	}

	private class LoadBusRidesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			DirectionsServiceAdapter busDirectionsServiceAdapter = new DirectionsServiceAdapterImpl();
			busRides = busDirectionsServiceAdapter.getBusDirections(
					position.getLongitude(), position.getLatitude(),
					destination.getLongitude(), destination.getLatitude(), 100,
					context.getToken());
			if (busRides == null)
				busRides = busDirectionsServiceAdapter.getBusDirections(
						position.getLongitude(), position.getLatitude(),
						destination.getLongitude(), destination.getLatitude(),
						200, context.getToken());
			if (busRides == null)
				busRides = busDirectionsServiceAdapter.getBusDirections(
						position.getLongitude(), position.getLatitude(),
						destination.getLongitude(), destination.getLatitude(),
						300, context.getToken());
			if (busRides == null)
				busRides = busDirectionsServiceAdapter.getBusDirections(
						position.getLongitude(), position.getLatitude(),
						destination.getLongitude(), destination.getLatitude(),
						400, context.getToken());
			if (busRides == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 100,
								context.getToken());
			if (transshipments == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 200,
								context.getToken());
			if (transshipments == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 300,
								context.getToken());
			if (transshipments == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 400,
								context.getToken());
			return null;
		}

	}

}
