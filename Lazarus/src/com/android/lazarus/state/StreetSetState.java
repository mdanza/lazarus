package com.android.lazarus.state;

import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Point;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.stubs.AddressServiceAdapterStub;

public class StreetSetState extends AbstractState {

	String firstStreet;
	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterStub();
	String defaultMessage = "Diga el nombre de otra calle o el n�mero de puerta, ";
	int position = 0;
	List<String> streets = null;
	List<String> firstResults;
	private boolean firstTime = true;
	private boolean toConfirmDoorNumber = false;
	private boolean toChooseStreet = false;
	private boolean toConfirmSecondStreet = false;
	private String secondStreet = null;
	private String addressNumber = null;
	private boolean passedFirstTime = false;
	
	StreetSetState(VoiceInterpreterActivity context) {
		super(context);
	}

	public StreetSetState(VoiceInterpreterActivity context, String street) {
		super(context);
		firstStreet = street;
		this.message = defaultMessage;
	}

	@Override
	protected void handleResults(List<String> results) {
		if(firstTime){
			firstTime = false;
			firstResults = results;
			checkForNumberOrCorner();
			return;
		}
		passedFirstTime = true;
		if(!toChooseStreet && !firstTime && !toConfirmDoorNumber && !toConfirmSecondStreet){
			if(stringPresent(results, "mas")){
				goToNextPosition();
				return;
			}
		}
		if(toConfirmDoorNumber){
			if(stringPresent(results, "si")){
				goToDestinationSetState();
				return;
			}
			if(stringPresent(results, "no")){
				goToNextPosition();
				return;
			}
		}
		if(toChooseStreet){
			for (int i = 1; i < streets.size() + 1; i++) {
				if (containsNumber(results, i)) {
					secondStreet =  streets.get(i - 1);
					toConfirmSecondStreet = true;
					toChooseStreet = false;
					this.message = "�Desea ir a "+firstStreet+" esquina "+secondStreet+"?";
					return;
				}
			}
			if(stringPresent(results, "mas")){
				goToNextPosition();
			}
		}
		if(toChooseStreet && stringPresent(results, "mas")){
			goToNextPosition();
			return;
		}
		if(toConfirmSecondStreet){
			if(stringPresent(results, "si")){
				goToDestinationSetState();
				return;
			}
			if(stringPresent(results, "no")){
				checkForNumberOrCorner();
			}
		}
		

	}
	
	private void goToDestinationSetState() {
		Point destination = null;
		if(secondStreet!=null){
			destination = addressServiceAdapter.getCorner(firstStreet,secondStreet);
		}
		if(addressNumber!=null){
			destination = addressServiceAdapter.getByDoorNumber(firstStreet,Integer.getInteger(getAddressNumberString(firstResults.get(position)).get(0)),getAddressNumberString(firstResults.get(position)).get(1));
		}
		DestinationSetState destinationSetState = new DestinationSetState(context, destination);
		this.context.setState(destinationSetState);
	}

	private void checkForNumberOrCorner() {
		if(position==firstResults.size()){
			if(passedFirstTime){
				this.message = "No se han encontrado otros resultados posibles, ";	
			}else{
				this.message = "No se han encontrado resultados, ";
			}
			this.message = message+defaultMessage;
			resetData();
			return;
		}
		if(isAddressNumber(firstResults.get(position))){
			toConfirmDoorNumber = true;
			this.message = "�Desea ir a "+firstStreet+" "+getAddressNumberString(firstResults.get(position)).get(0)+" "+getAddressNumberString(firstResults.get(position)).get(1)+"?";
			addressNumber = firstResults.get(position);
			return;
		}
		streets = addressServiceAdapter.getPossibleStreets(firstResults
				.get(position));
		if(streets!=null && !streets.isEmpty()){
			toChooseStreet = true;
			this.message = "";
			for (int i = 1; i < streets.size() + 1; i++) {
				this.message = message + "Si desea dirigirse a "
						+ streets.get(i - 1) + " esquina "+firstStreet+" diga "
						+ getStringDigits(i) + ",";
			}
			String finalMessage = " para obtener otros resultados posibles diga m�s";
			this.message = message + finalMessage;
			return;
		}
		if(position<firstResults.size()){
			goToNextPosition();
		}
		return;
	}

	private void goToNextPosition() {
		position++;
		streets = null;
		secondStreet = null;
		toConfirmDoorNumber = false;
		toChooseStreet = false;
		toConfirmSecondStreet = false;
		addressNumber = null;
		checkForNumberOrCorner();
	}
	
	private void resetData(){
		position=0;
		streets = null;
		secondStreet = null;
		toConfirmDoorNumber = false;
		toChooseStreet = false;
		toConfirmSecondStreet = false;
		addressNumber = null;
		firstTime=true;
		passedFirstTime=false;
	}

}
