package com.kruiper.timon.v6informatica.objects;

/**
 * Created by timon on 3-4-2016.
 */
public class Locatie {
	public int gebrid;
	public double lat, lng;
	public String naam;

	public Locatie(int gebrid, double lat, double lng) {
		this.gebrid = gebrid;
		this.lat = lat;
		this.lng = lng;
	}

	public Locatie(String naam, double lat, double lng) {
		this.naam = naam;
		this.lng = lng;
		this.lat = lat;
	}
}
