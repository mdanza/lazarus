package com.android.lazarus.serviceadapter;

public class UserServiceAdapterStub implements UserServiceAdapter {

	@Override
	public boolean login(String username, String password) {
		if("hugo".equals(username) && "huguito".equals(password)){
			return true;
		}else{
			return false;
		}
	}

}
