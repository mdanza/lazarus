package com.android.lazarus.serviceadapter;

import com.android.lazarus.model.Favourite;

public interface UserServiceAdapter {

	public String login(String username, String password);

	public Favourite getFavourite(String token, String string);

}
