package com.android.lazarus.state;

import java.util.ArrayList;

import com.android.lazarus.VoiceInterpreterActivity;

public class MoreMainMenuState extends AbstractState {

	MoreMainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = "Para llamar a un taxi diga uno, para etc";
		
	}

	@Override
	public void handleResults(ArrayList<String> stringArrayList) {
		// TODO Auto-generated method stub

	}

}
