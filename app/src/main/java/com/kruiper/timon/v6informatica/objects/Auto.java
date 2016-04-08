package com.kruiper.timon.v6informatica.objects;

import java.io.Serializable;

/**
 * Created by timon on 14-3-2016.
 */
public class Auto implements Serializable {
	public String merk, type, type_brandstof, kenteken;
	public int gebrid, autoid;

	public Auto(int gebrid, String merk, String type, String type_brandstof, String kenteken) {
		this.gebrid = gebrid;
		this.merk = merk;
		this.type = type;
		this.type_brandstof = type_brandstof;
		this.kenteken = kenteken;
	}

	public Auto(String merk, String type, String type_brandstof, String kenteken, int autoid) {
		this.kenteken = kenteken;
		this.merk = merk;
		this.type = type;
		this.type_brandstof = type_brandstof;
		this.autoid = autoid;
	}

	@Override
	public String toString() {
		return merk + " " + type + " " + type_brandstof + " " + kenteken;
	}
}
