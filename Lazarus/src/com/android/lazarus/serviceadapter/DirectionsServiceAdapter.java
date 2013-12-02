package com.android.lazarus.serviceadapter;

import java.util.List;

import android.location.Location;

import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;

public interface DirectionsServiceAdapter {

	List<WalkingPosition> getWalkingDirections(Location origin,
			Point destination);

}
