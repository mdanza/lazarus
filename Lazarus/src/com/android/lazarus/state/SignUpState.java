package com.android.lazarus.state;

import java.util.ArrayList;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterStub;

public class SignUpState extends AbstractState{
	
	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterStub();

	public SignUpState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = "Por faver diga el nombre de usuario que desea tener";
	}

	public void setResults(ArrayList<String> stringArrayList) {
		// TODO Auto-generated method stub
		
	}
}
