package com.android.lazarus.serviceadapter.stubs;

import java.util.List;

import android.location.Location;

import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapter;

public class DirectionsAdapterStub implements DirectionsServiceAdapter {

	@Override
	public List<WalkingPosition> getWalkingDirections(String token,
			Location origin, Point destination) {
		// TODO Auto-generated method stub
		return null;
	}

}
