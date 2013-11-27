package com.android.lazarus.state;

import java.util.List;

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

	public void handleResults(List<String> stringArrayList) {
		// TODO Auto-generated method stub
		
	}
}
