package com.android.lazarus.model;

import java.util.List;

public class BusRouteMaximal {

	private long maximalVariantCode;

	private long variantCode;

	private long subLineCode;

	private String lineName;

	private String subLineDescription;

	private List<Point> trajectory;

	public String getLineName() {
		return lineName;
	}

	public long getMaximalVariantCode() {
		return maximalVariantCode;
	}

	public void setMaximalVariantCode(long maximalVariantCode) {
		this.maximalVariantCode = maximalVariantCode;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public List<Point> getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(List<Point> trajectory) {
		this.trajectory = trajectory;
	}

	public long getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(long variantCode) {
		this.variantCode = variantCode;
	}

	public String getSubLineDescription() {
		return subLineDescription;
	}

	public void setSubLineDescription(String subLineDescription) {
		this.subLineDescription = subLineDescription;
	}

	public long getSubLineCode() {
		return subLineCode;
	}

	public void setSubLineCode(long subLineCode) {
		this.subLineCode = subLineCode;
	}

}
