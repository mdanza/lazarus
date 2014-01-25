package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.location.Location;
import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.GPScoordinateHelper;
import com.android.lazarus.model.BusRide;
import com.android.lazarus.model.BusStop;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.Transshipment;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapter;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapterImpl;
import com.android.lazarus.serviceadapter.ScheduleServiceAdapter;
import com.android.lazarus.serviceadapter.ScheduleServiceAdapterImpl;

public class BusDirectionsState extends LocationDependentState {
	private Point destination;
	private static final int NEEDED_ACCURACY = 50;
	private List<BusRide> busRides;
	private List<Transshipment> transshipments;
	private List<List<String>> schedule;
	private int pageNumber = 0;
	private InternalState state = InternalState.SEARCH_OPTIONS;

	private enum InternalState {
		SEARCH_OPTIONS, AWAITING_USER_DECISION_BUS_RIDE, AWAITING_USER_DECISION_TRANSSHIPMENT, NO_OPTIONS_FOUND
	}

	public BusDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		giveInstructions();
	}

	@Override
	protected void handleResults(List<String> results) {
		if (state.equals(InternalState.AWAITING_USER_DECISION_BUS_RIDE)) {
			for (int i = 0; i < busRides.size(); i++) {
				if (containsNumber(results, i + 1)) {
					context.setState(new BusRideState(this.context,
							destination, busRides.get(i)));
					return;
				}
			}
		}
		if (state.equals(InternalState.AWAITING_USER_DECISION_TRANSSHIPMENT)) {
			for (int i = 0; i < transshipments.size(); i++) {
				if (containsNumber(results, i + 1)) {
					context.setState(new TransshipmentState(this.context,
							destination, transshipments.get(i)));
					return;
				}
			}
		}
		giveInstructions();
	}

	@Override
	protected void giveInstructions() {
		if (state.equals(InternalState.SEARCH_OPTIONS))
			new LoadBusRidesTask().execute();
		if (state.equals(InternalState.AWAITING_USER_DECISION_BUS_RIDE)) {
			message = "Las opciones de bus son";
			for (int i = 0; i < busRides.size(); i++) {
				message += ",,diga " + (i + 1) + " , para tomar un "
						+ busRides.get(i).getLineName() + ", "
						+ busRides.get(i).getDestination();
				appendDistanceToStop(busRides.get(i).getStartStop());
				appendSchedule(i);
			}
			context.speak(message);
		}
		if (state.equals(InternalState.AWAITING_USER_DECISION_TRANSSHIPMENT)) {
			message = "Las opciones de bus son";
			for (int i = 0; i < transshipments.size(); i++) {
				message += ",,diga "
						+ (i + 1)
						+ " , para tomar un "
						+ transshipments.get(i).getFirstRoute().getLineName()
						+ " "
						+ transshipments.get(i).getFirstRoute()
								.getDestination()
						+ " y luego un "
						+ transshipments.get(i).getSecondRoute().getLineName()
						+ " "
						+ transshipments.get(i).getSecondRoute()
								.getDestination();
				appendDistanceToStop(transshipments.get(i).getFirstRoute()
						.getStartStop());
				appendSchedule(i);
			}
			context.speak(message);
		}
		if (state.equals(InternalState.NO_OPTIONS_FOUND)) {
			message = "No se encontraron buses hacia ese destino, ni siquiera con conexión";
			context.speak(message);
		}
	}

	private void appendSchedule(int pos) {
		message += " a las";
		int counter = 0;
		for (String time : schedule.get(pos)) {
			message += ",," + time;
			counter++;
			if (counter == 3)
				break;
		}
	}

	private void appendDistanceToStop(BusStop stop) {
		message += " a "
				+ Double.valueOf(
						Math.floor(GPScoordinateHelper
								.getDistanceBetweenPoints(position
										.getLatitude(), stop.getPoint()
										.getLatitude(),
										position.getLongitude(), stop
												.getPoint().getLongitude())))
						.intValue() + " metros";
	}

	@Override
	protected void restartState() {
		state = InternalState.SEARCH_OPTIONS;
	}

	private void filterBusRides() {
		List<BusRide> unique = new ArrayList<BusRide>();
		for (int i = 0; i < busRides.size(); i++) {
			if (!containsBusRide(unique, busRides.get(i)))
				unique.add(busRides.get(i));
		}
		busRides = unique;
	}

	private boolean containsBusRide(List<BusRide> list, BusRide newItem) {
		for (BusRide busRide : list) {
			if (busRide.getLineName().equals(newItem.getLineName())
					&& busRide.getDestination()
							.equals(newItem.getDestination()))
				return true;
		}
		return false;
	}

	private void filterTransshipments() {
		List<Transshipment> unique = new ArrayList<Transshipment>();
		for (int i = 0; i < transshipments.size(); i++) {
			if (!containsTransshipment(unique, transshipments.get(i)))
				unique.add(transshipments.get(i));
		}
		transshipments = unique;
	}

	private boolean containsTransshipment(List<Transshipment> list,
			Transshipment newItem) {
		for (Transshipment transshipment : list) {
			if (transshipment.getFirstRoute().getLineName()
					.equals(newItem.getFirstRoute().getLineName())
					&& transshipment.getFirstRoute().getDestination()
							.equals(newItem.getFirstRoute().getDestination())
					&& transshipment.getSecondRoute().getLineName()
							.equals(newItem.getSecondRoute().getLineName())
					&& transshipment.getSecondRoute().getDestination()
							.equals(newItem.getSecondRoute().getDestination()))
				return true;
		}
		return false;
	}

	@Override
	public void setPosition(Location position) {

		if (position == null) {
			this.message = notEnoughAccuracyMessage;
			context.speak(this.message);
		} else {
			if (!(position.getAccuracy() < minimumAccuraccy)) {
				enoughAccuraccy = false;
				this.message = notEnoughAccuracyMessage;
				context.speak(this.message);
			} else {
				enoughAccuraccy = true;
				this.position = position;
				giveInstructions();
			}
		}
	}

	private class LoadBusRidesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			DirectionsServiceAdapter busDirectionsServiceAdapter = new DirectionsServiceAdapterImpl();
			busRides = busDirectionsServiceAdapter.getBusDirections(
					position.getLongitude(), position.getLatitude(),
					destination.getLongitude(), destination.getLatitude(), 100,
					pageNumber, context.getToken());
			if (busRides == null)
				busRides = busDirectionsServiceAdapter.getBusDirections(
						position.getLongitude(), position.getLatitude(),
						destination.getLongitude(), destination.getLatitude(),
						200, pageNumber, context.getToken());
			if (busRides == null)
				busRides = busDirectionsServiceAdapter.getBusDirections(
						position.getLongitude(), position.getLatitude(),
						destination.getLongitude(), destination.getLatitude(),
						300, pageNumber, context.getToken());
			if (busRides == null)
				busRides = busDirectionsServiceAdapter.getBusDirections(
						position.getLongitude(), position.getLatitude(),
						destination.getLongitude(), destination.getLatitude(),
						400, pageNumber, context.getToken());
			if (busRides == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 100, pageNumber,
								context.getToken());
			if (busRides == null && transshipments == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 200, pageNumber,
								context.getToken());
			if (busRides == null && transshipments == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 300, pageNumber,
								context.getToken());
			if (busRides == null && transshipments == null)
				transshipments = busDirectionsServiceAdapter
						.getBusDirectionsWithTransshipment(
								position.getLongitude(),
								position.getLatitude(),
								destination.getLongitude(),
								destination.getLatitude(), 400, pageNumber,
								context.getToken());
			if (busRides == null && transshipments == null)
				state = InternalState.NO_OPTIONS_FOUND;
			else {
				if (busRides != null) {
					filterBusRides();
					loadSchedule();
					if (!busRides.isEmpty())
						state = InternalState.AWAITING_USER_DECISION_BUS_RIDE;
					else
						moreOptions();
				}
				if (transshipments != null) {
					filterTransshipments();
					loadSchedule();
					if (!transshipments.isEmpty())
						state = InternalState.AWAITING_USER_DECISION_TRANSSHIPMENT;
					else
						moreOptions();
				}
			}
			giveInstructions();
			return null;
		}

		private void moreOptions() {
			pageNumber++;
			doInBackground();
		}

		private void loadSchedule() {
			ScheduleServiceAdapter scheduleServiceAdapter = new ScheduleServiceAdapterImpl();
			schedule = new ArrayList<List<String>>();
			DateTime now = new DateTime();
			if (busRides != null)
				for (int i = 0; i < busRides.size(); i++) {
					List<String> times = scheduleServiceAdapter.getBusSchedule(
							context.getToken(), busRides.get(i).getLineName(),
							busRides.get(i).getSubLineDescription(), busRides
									.get(i).getStartStop()
									.getBusStopLocationCode(),
							now.getMinuteOfDay());
					if (times != null && !times.isEmpty())
						schedule.add(times);
					else {
						busRides.remove(i);
						i--;
					}
				}
			if (transshipments != null)
				for (int i = 0; i < transshipments.size(); i++) {
					List<String> times = scheduleServiceAdapter.getBusSchedule(
							context.getToken(), transshipments.get(i)
									.getFirstRoute().getLineName(),
							transshipments.get(i).getFirstRoute()
									.getSubLineDescription(), transshipments
									.get(i).getFirstRoute().getStartStop()
									.getBusStopLocationCode(),
							now.getMinuteOfDay());
					if (times != null && !times.isEmpty())
						schedule.add(times);
					else {
						transshipments.remove(i);
						i--;
					}
				}
		}
	}

}
