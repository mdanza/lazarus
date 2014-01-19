package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

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
	private String message = "";
	private static final int NEEDED_ACCURACY = 50;
	private List<BusRide> busRides;
	private List<Transshipment> transshipments;
	private List<List<String>> schedule;
	private int pageNumber = 0;
	private InternalState state = InternalState.SEARCH_OPTIONS;

	private enum InternalState {
		SEARCH_OPTIONS, AWAITING_USER_DECISION_BUS_RIDE, AWAITING_USER_DECISION_TRANSSHIPMENT, NO_OPTIONS_FOUND, BUS_RIDE_SELECTED, TRANSSHIPMENT_SELECTED, BUS_RIDE_WALKING_TO_STOP, BUS_RIDE_WAITING_BUS
	}

	public BusDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public BusDirectionsState(VoiceInterpreterActivity context,
			Point destination, String initialMessage) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		this.message = initialMessage;
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
		if (state.equals(InternalState.BUS_RIDE_SELECTED)
				|| state.equals(InternalState.TRANSSHIPMENT_SELECTED)) {
			if (stringPresent(results, "destino")) {
				MainMenuState mainMenuState = new MainMenuState(context);
				context.setState(mainMenuState);
			}
		} else
			giveInstructions();
	}

	@Override
	protected void giveInstructions() {
		if (state.equals(InternalState.SEARCH_OPTIONS))
			new LoadBusRidesTask().execute();
		if (state.equals(InternalState.AWAITING_USER_DECISION_BUS_RIDE)) {
			message = "Las opciones de bus son";
			for (int i = 0; i < busRides.size(); i++) {
				message += ",,diga " + i + " , para tomar un "
						+ busRides.get(i).getLineName() + " con destino "
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
						+ i
						+ " , para tomar un "
						+ transshipments.get(i).getFirstRoute().getLineName()
						+ " con destino "
						+ transshipments.get(i).getFirstRoute()
								.getDestination()
						+ " y luego un "
						+ transshipments.get(i).getSecondRoute().getLineName()
						+ " con destino "
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
		for (String time : schedule.get(pos))
			message += ",," + time;
	}

	private void appendDistanceToStop(BusStop stop) {
		message += " a "
				+ GPScoordinateHelper
						.getDistanceBetweenPoints(position.getLatitude(), stop
								.getPoint().getLatitude(), position
								.getLongitude(), stop.getPoint().getLongitude())
				+ " metros de su posición actual";
	}

	@Override
	protected void restartState() {
		state = InternalState.SEARCH_OPTIONS;
	}

	private void filterBusRides() {
		List<String> distinctLines = new ArrayList<String>();
		for (int i = 0; i < busRides.size(); i++) {
			if (distinctLines.contains(busRides.get(i).getLineName())) {
				busRides.remove(i);
				i--;
			} else
				distinctLines.add(busRides.get(i).getLineName());
		}
	}

	private void filterTransshipments() {
		List<String> distinctFirstLines = new ArrayList<String>();
		List<String> distinctSecondLines = new ArrayList<String>();
		for (int i = 0; i < transshipments.size(); i++) {
			if (distinctFirstLines.contains(transshipments.get(i)
					.getFirstRoute().getLineName())) {
				int pos = distinctFirstLines.indexOf(transshipments.get(i)
						.getFirstRoute().getLineName());
				if (distinctSecondLines.get(pos).equals(
						transshipments.get(i).getSecondRoute().getLineName())) {
					transshipments.remove(i);
					i--;
				} else {
					distinctFirstLines.add(transshipments.get(i)
							.getFirstRoute().getLineName());
					distinctSecondLines.add(transshipments.get(i)
							.getSecondRoute().getLineName());
				}
			} else {
				distinctFirstLines.add(transshipments.get(i).getFirstRoute()
						.getLineName());
				distinctSecondLines.add(transshipments.get(i).getSecondRoute()
						.getLineName());
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
