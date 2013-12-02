package com.android.lazarus.serviceadapter.stubs;

import java.util.List;

import com.android.lazarus.model.Favourite;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.User;
import com.android.lazarus.serviceadapter.UserServiceAdapter;

public class UserServiceAdapterStub implements UserServiceAdapter {

	@Override
	public boolean login(String username, String password) {
		if("hugo".equals(username) && "huguito".equals(password)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Favourite getFavourite(String string) {
		if("casa".equals(string)){
			return new Favourite(new Point(), "casa", new User());
		}else{
			return null;
		}
	}

}
