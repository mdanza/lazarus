package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.Favourite;

public interface FavouritesReportingServiceAdapter {

	/**
	 * @return true if successful, false otherwise
	 */
	public boolean addToFavourite(String token, String coordinates, String name);

	/**
	 * @return true if successful, false otherwise
	 */
	public boolean removeFavourite(String token, String name);

	/**
	 * 
	 * 
	 * @return List of Favourite for user that matches token, null if no
	 *         favorites are present or if bad token
	 */
	public List<Favourite> getFavourites(String token);
}
