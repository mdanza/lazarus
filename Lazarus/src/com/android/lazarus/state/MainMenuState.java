package com.android.lazarus.state;

import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Favourite;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterStub;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterStub;

public class MainMenuState extends AbstractState {
	
	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterStub();
	UserServiceAdapter userServiceAdapter = new UserServiceAdapterStub();
	String defaultMessage = "Diga el nombre de la calle a la que quiere dirigirse, o nombre favorito de destino, para más opciones diga más";
	int position = 0;

	public MainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = defaultMessage;
	}

	public MainMenuState(VoiceInterpreterActivity context,
			String initialText) {
		super(context);
		this.context = context;
			this.message = initialText+defaultMessage;
	}

	public void handleResults(List<String> results) {
		if(stringPresent(results,"mas")){
			MoreMainMenuState moreMainMenuState = new MoreMainMenuState(this.context);
			context.setState(moreMainMenuState);	
		}
		//for each position checks if there is an available street or favourite place
		//if there is none or user asks for more results, go to next position
		if(position<results.size()){
			List<String> streets = addressServiceAdapter.getPossibleStreets(results.get(position));
			Favourite favourite = userServiceAdapter.getFavourite(results.get(position));
			if(stringPresent(results,"no") || stringPresent(results,"otro") || ((streets==null || streets.isEmpty()) && favourite==null)){
				position++;
				setResults(results);
			}else{
				if(favourite!=null){
					this.message = "¿Desea dirigirse a "+favourite.getName()+"?";
					return;
				}
				if(streets!=null && !streets.isEmpty()){
					this.message = "";
					for(int i=0;i<streets.size();i++){
						this.message = message+"Si desea dirigirse a "+streets.get(i)+" diga "+i+","; 
					}
					String finalMessage = " para obtener otros resultados posibles diga otro";
					this.message = message + finalMessage;
				}
				
			}
		}
		if(position==results.size()){
			if(position!=0)
			this.message = "No se han encontrado resultados."+defaultMessage;
			position = 0;
		}
	}

}
