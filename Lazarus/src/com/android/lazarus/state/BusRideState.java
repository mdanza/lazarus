package com.android.lazarus.state;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import android.location.Location;
import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.GPScoordinateHelper;
import com.android.lazarus.helpers.MessageHelper;
import com.android.lazarus.model.Bus;
import com.android.lazarus.model.BusRide;
import com.android.lazarus.model.Point;
import com.android.lazarus.serviceadapter.BusReportingServiceAdapter;
import com.android.lazarus.serviceadapter.BusReportingServiceAdapterImpl;
import com.android.lazarus.serviceadapter.ScheduleServiceAdapter;
import com.android.lazarus.serviceadapter.ScheduleServiceAdapterImpl;

public class BusRideState extends LocationDependentState {
	private BusRide ride;
	private List<BusRide> otherRides;
	private Point destination;
	private Bus bus;
	private List<String> schedule;
	private static final int NEEDED_ACCURACY = 100;
	private InternalState state;
	private ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(1);
	private ScheduledFuture<?> busUpdateTask;
	private boolean passedSecondLastStop = false;
	private boolean passedThirdLastStop = false;
	private Location lastSpokenLocation;
	private TransshipmentState parent;
	private boolean instructionsGivenOnConstruct = false;
	private BusFinderTask busFinderTask = new BusFinderTask();
	private boolean transhipmentIntermediaryStopSameForBothRoutes;
	private boolean enjoyMessageGiven = false;

	public enum InternalState {
		INITIALIZING, WALKING_TO_START_STOP, SEARCHING_BUS, WAITING_BUS, AWAITING_USER_CONFIRMATION_STEP_ONE, AWAITING_USER_CONFIRMATION_STEP_TWO, WAITING_END_STOP, WALKING_TO_DESTINATION
	}

	public BusRideState(VoiceInterpreterActivity context, Point destination,
			BusRide ride, List<BusRide> otherRides) {
		super(context, NEEDED_ACCURACY);
		this.message = "";
		this.destination = destination;
		this.ride = ride;
		this.state = InternalState.INITIALIZING;
		this.otherRides = otherRides;
	}

	public BusRideState(VoiceInterpreterActivity context, Point destination,
			BusRide ride, TransshipmentState parent,
			InternalState initialState, List<BusRide> otherRides,
			boolean transhipmentIntermediaryStopSameForBothRoutes) {
		super(context, NEEDED_ACCURACY);
		this.message = "";
		this.destination = destination;
		this.ride = ride;
		this.parent = parent;
		this.state = initialState;
		this.otherRides = otherRides;
		this.transhipmentIntermediaryStopSameForBothRoutes = transhipmentIntermediaryStopSameForBothRoutes;
	}

	@Override
	protected void handleResults(List<String> results) {
		if (state.equals(InternalState.WAITING_END_STOP)) {
			if (stringPresent(results, "abajo")) {
				state = InternalState.WALKING_TO_DESTINATION;
				if (parent != null
						&& transhipmentIntermediaryStopSameForBothRoutes) {
					arrivedToDestination();
					return;
				}
			}
		}
		if (state.equals(InternalState.AWAITING_USER_CONFIRMATION_STEP_TWO)) {
			for (int i = 0; i < otherRides.size(); i++) {
				if (containsNumber(results, i + 1)) {
					ride = otherRides.get(i);
					state = InternalState.WAITING_END_STOP;
				}
			}
		}
		if (state.equals(InternalState.AWAITING_USER_CONFIRMATION_STEP_ONE)) {
			if (stringPresent(results, "si"))
				state = InternalState.WAITING_END_STOP;
			if (stringPresent(results, "no"))
				state = InternalState.AWAITING_USER_CONFIRMATION_STEP_TWO;
		}
		if (state.equals(InternalState.WAITING_BUS)) {
			if (stringPresent(results, "recalcular")) {
				if (busUpdateTask != null)
					busUpdateTask.cancel(true);
				state = InternalState.SEARCHING_BUS;
			}
			if (stringPresent(results, "arriba")) {
				if (busUpdateTask != null)
					busUpdateTask.cancel(true);
				state = InternalState.AWAITING_USER_CONFIRMATION_STEP_ONE;
			}
		}
		giveInstructions();
	}

	@Override
	protected void giveInstructions() {
		if (state.equals(InternalState.INITIALIZING))
			state = InternalState.WALKING_TO_START_STOP;
		if (state.equals(InternalState.WALKING_TO_DESTINATION)) {
			context.setState(new WalkingDirectionsState(context, destination,
					this));
		}
		if (state.equals(InternalState.WAITING_END_STOP)) {
			if (!enjoyMessageGiven) {
				enjoyMessageGiven = true;
				message = "Disfrute su viaje, le informaremos algunas paradas antes de que deba bajarse";
				context.speak(message);
			}
		}
		if (state.equals(InternalState.AWAITING_USER_CONFIRMATION_STEP_TWO)) {
			if (otherRides != null && otherRides.size() > 0) {
				message = "";
				for (int i = 0; i < otherRides.size(); i++)
					message += "diga " + getStringDigits(i + 1)
							+ " si se tomó un "
							+ otherRides.get(i).getLineName() + " "
							+ otherRides.get(i).getDestination() + ", ";
				message += ", si no se ha subido a ninguno de estos coches, baje del ómnibus cuando le sea posible ";
				context.speak(message);
			} else
				context.setState(new MainMenuState(context,
						"No se encontraron otras alternativas de bus que le sirvan"));
		}
		if (state.equals(InternalState.AWAITING_USER_CONFIRMATION_STEP_ONE)) {
			message = "Usted se subió a un " + ride.getLineName() + " "
					+ ride.getDestination() + "?";
			context.speak(message);
		}
		if (state.equals(InternalState.WAITING_BUS)) {
			if (bus == null) {
				message = "No se encontraron coches cercanos, ";
				if (ride != null) {
					message += "para el " + ride.getLineName() + " "
							+ ride.getDestination() + ", ";
				}
				appendSchedule();
			} else {
				if (ride != null) {
					message = "El " + ride.getLineName() + " "
							+ ride.getDestination() + " ";
				} else {
					message = "El coche ";
				}
				message += " más cercano está a "
						+ Double.valueOf(
								GPScoordinateHelper.getDistanceBetweenPoints(
										position.getLatitude(),
										bus.getLatitude(),
										position.getLongitude(),
										bus.getLongitude())).intValue()
						+ " metros, lo mantendremos actualizado";
				busUpdateTask = scheduledExecutorService.scheduleAtFixedRate(
						new UpdateBusTask(), 10, 20, TimeUnit.SECONDS);
			}
			message += ", diga arriba,, cuando aborde el coche, diga recalcular,, si desea buscar coches cercanos nuevamente";
			if (otherRides != null && otherRides.size() > 0) {
				if (ride != null) {
					message += ",, En esta parada, además del "
							+ ride.getLineName() + " " + ride.getDestination()
							+ ", también le sirve tomarse un ";
				} else {
					message += ",, En esta parada también le sirve tomarse un ";
				}
				for (BusRide otherRide : otherRides) {
					message += otherRide.getLineName() + " "
							+ otherRide.getDestination() + ", ";
				}
				message += "diga arriba,, cuando aborde el coche";
			}
			context.speak(message);
		}
		if (state.equals(InternalState.SEARCHING_BUS)) {
			message = "Buscando coche más cercano a su parada";
			context.speak(message);
			if (busFinderTask.getStatus() != AsyncTask.Status.RUNNING) {
				if (busFinderTask.getStatus() == AsyncTask.Status.PENDING) {
					busFinderTask.execute();
				} else {
					if (busFinderTask.getStatus() == AsyncTask.Status.FINISHED) {
						busFinderTask = new BusFinderTask();
						busFinderTask.execute();
					}
				}
			}
		}
		if (state.equals(InternalState.WALKING_TO_START_STOP)) {
			context.setState(new WalkingDirectionsState(context, ride
					.getStartStop().getPoint(), this));
		}
	}

	private void appendSchedule() {
		message += " las próximas pasadas están agendadas para las";
		int counter = 0;
		for (String time : schedule) {
			message += ",, " + MessageHelper.formatTime(time);
			counter++;
			if (counter == 3)
				break;
		}

	}

	@Override
	protected void cancel() {
		context.setState(new MainMenuState(context));
	}

	@Override
	public void setPosition(Location position) {
		changedPosition();
	}

	private void changedPosition() {
		if (state.equals(InternalState.WAITING_END_STOP)) {
			int distanceFromLastSpokenLocation;
			if (lastSpokenLocation != null)
				distanceFromLastSpokenLocation = Double.valueOf(
						GPScoordinateHelper.getDistanceBetweenPoints(
								position.getLatitude(),
								lastSpokenLocation.getLatitude(),
								position.getLongitude(),
								lastSpokenLocation.getLongitude())).intValue();
			else
				distanceFromLastSpokenLocation = 100000;
			if (!passedThirdLastStop && ride.getPreviousStops() != null
					&& ride.getPreviousStops().size() > 1) {
				int distance = Double.valueOf(
						GPScoordinateHelper.getDistanceBetweenPoints(
								position.getLatitude(), ride.getPreviousStops()
										.get(1).getPoint().getLatitude(),
								position.getLongitude(), ride
										.getPreviousStops().get(1).getPoint()
										.getLongitude())).intValue();
				if (distance < minimumAccuraccy
						&& distanceFromLastSpokenLocation > minimumAccuraccy / 2) {
					passedThirdLastStop = true;
					message = "Usted se encuentra a "
							+ distance
							+ " metros de su ante penúltima parada, diga,, abajo,, cuando se haya bajado del coche";
					context.speak(message);
					lastSpokenLocation = position;
				}
				return;
			}
			if (!passedSecondLastStop && ride.getPreviousStops() != null
					&& ride.getPreviousStops().size() > 0) {
				int distance = Double.valueOf(
						GPScoordinateHelper.getDistanceBetweenPoints(
								position.getLatitude(), ride.getPreviousStops()
										.get(0).getPoint().getLatitude(),
								position.getLongitude(), ride
										.getPreviousStops().get(0).getPoint()
										.getLongitude())).intValue();
				if (distance < minimumAccuraccy
						&& distanceFromLastSpokenLocation > minimumAccuraccy / 2) {
					passedSecondLastStop = true;
					message = "Usted se encuentra a "
							+ distance
							+ " metros de su penúltima parada, diga,, abajo,, cuando se haya bajado del coche";
					context.speak(message);
					lastSpokenLocation = position;
				}
				return;
			}
			int distanceToLastStop = Double.valueOf(
					GPScoordinateHelper.getDistanceBetweenPoints(
							position.getLatitude(), ride.getEndStop()
									.getPoint().getLatitude(),
							position.getLongitude(), ride.getEndStop()
									.getPoint().getLongitude())).intValue();
			if ((distanceToLastStop < 170 || distanceToLastStop < 20)
					&& distanceFromLastSpokenLocation > minimumAccuraccy / 2) {
				passedSecondLastStop = true;
				message = "Usted se encuentra a "
						+ distanceToLastStop
						+ " metros de su parada de descenso, diga,, abajo,, cuando se haya bajado del coche";
				context.speak(message);
				lastSpokenLocation = position;
			}
		}
	}

	public void arrivedToDestination() {
		if (state.equals(InternalState.WALKING_TO_START_STOP)) {
			state = InternalState.SEARCHING_BUS;
			giveInstructions();
		}
		if (state.equals(InternalState.WALKING_TO_DESTINATION)) {
			if (parent == null) {
				MainMenuState mainMenuState = new MainMenuState(context);
				context.setState(mainMenuState);
			} else {
				context.setState(parent, false);
				parent.arrivedToDestination();
			}
		}
	}

	private class BusFinderTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			ScheduleServiceAdapter scheduleServiceAdapter = new ScheduleServiceAdapterImpl();
			if (isCancelled())
				return null;
			bus = scheduleServiceAdapter.getClosestBus(context.getToken(), ride
					.getStartStop().getVariantCode(), ride.getSubLineCode(),
					ride.getStartStop().getOrdinal());
			if (bus == null) {
				DateTime now = new DateTime();
				schedule = scheduleServiceAdapter.getBusSchedule(context
						.getToken(), ride.getLineName(), ride
						.getSubLineDescription(), ride.getStartStop()
						.getBusStopLocationCode(), now.getMinuteOfDay());
			}
			state = InternalState.WAITING_BUS;
			if (isCancelled())
				return null;
			giveInstructions();
			return null;
		}
	}

	private class UpdateBusTask implements Runnable {

		@Override
		public void run() {
			BusReportingServiceAdapter busReportingServiceAdapter = new BusReportingServiceAdapterImpl();
			bus = busReportingServiceAdapter.getBus(context.getToken(),
					bus.getId());
			if (position != null && bus != null) {
				int distanceToBus = Double.valueOf(
						Math.ceil(GPScoordinateHelper.getDistanceBetweenPoints(
								position.getLatitude(), bus.getLatitude(),
								position.getLongitude(), bus.getLongitude())))
						.intValue();
				if (distanceToBus > 250)
					message = "El coche más cercano está a aproximadamente "
							+ distanceToBus
							+ " metros";
				else
					message = "Atención, el coche está próximo a usted, diga arriba, cuando aborde el coche";
				if (bus.getLastUpdated() != null) {
					if (bus.getLastUpdated().before(
							new DateTime().minusMinutes(5).toDate())) {
						message += " la última actualización del coche se recibió hace cinco minutos, "
								+ "es posible que el mismo haya dejado de enviar información, diga,, "
								+ "recalcular, para buscar otro coche ";
					}
				}
				context.speak(message);
			}
		}

	}

	@Override
	public void onAttach() {
		if (position != null) {
			if (!state.equals(InternalState.WALKING_TO_START_STOP)
					&& !state.equals(InternalState.WALKING_TO_DESTINATION)) {
				giveInstructions();
				instructionsGivenOnConstruct = true;
			}
			if (state.equals(InternalState.WALKING_TO_START_STOP)
					&& parent != null) {
				context.setState(new WalkingDirectionsState(context, ride
						.getStartStop().getPoint(), this));
				instructionsGivenOnConstruct = true;
			}
		}
	}

	/*
	 * @Override public void onAttach() { if
	 * (!state.equals(InternalState.WALKING_TO_START_STOP) &&
	 * !state.equals(InternalState.WALKING_TO_DESTINATION)) { if (position !=
	 * null) { giveInstructions(); instructionsGivenOnConstruct = true; } } }
	 */

	@Override
	protected void cancelAsyncTasks() {
		if (busFinderTask != null)
			busFinderTask.cancel(true);
		if (busUpdateTask != null)
			busUpdateTask.cancel(true);
	}

}
