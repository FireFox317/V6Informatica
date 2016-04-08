package com.kruiper.timon.v6informatica.helper;

import com.kruiper.timon.v6informatica.objects.Auto;
import com.kruiper.timon.v6informatica.objects.Locatie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by timon on 15-3-2016.
 */
public class ParseJSON {

	public static final String JSON_ARRAY = "autos";
	public static final String KEY_ID = "autoid";
	public static final String KEY_MERK = "merk";
	public static final String KEY_TYPE = "type";
	public static final String KEY_TYPEBRANDSTOF = "typebrandstof";
	public static final String KEY_KENTEKEN = "kenteken";

	private JSONArray autos = null;
	private JSONArray locaties = null;
	private String json;

	public ParseJSON(String json){
		this.json = json;
	}

	public ArrayList<Locatie> parseJSONloc(){
		JSONObject jsonObject=null;
		ArrayList<Locatie> locatieArrayList = null;
		try {
			jsonObject = new JSONObject(json);
			locaties = jsonObject.getJSONArray("locaties");

			locatieArrayList = new ArrayList<>();

			for(int i=0;i<locaties.length();i++){
				JSONObject jo = locaties.getJSONObject(i);
				Locatie locatie = new Locatie(jo.getString("naam"),jo.getDouble("lat"),jo.getDouble("lng"));

				locatieArrayList.add(locatie);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return locatieArrayList;
	}

	public ArrayList<Auto> parseJSON(){
		JSONObject jsonObject=null;
		ArrayList<Auto> autoArrayList = null;
		try {
			jsonObject = new JSONObject(json);
			autos = jsonObject.getJSONArray(JSON_ARRAY);

			autoArrayList = new ArrayList<Auto>();


			for(int i=0;i<autos.length();i++){
				JSONObject jo = autos.getJSONObject(i);
				Auto auto = new Auto(jo.getString(KEY_MERK),jo.getString(KEY_TYPE),jo.getString(KEY_TYPEBRANDSTOF),jo.getString(KEY_KENTEKEN),jo.getInt(KEY_ID));

				autoArrayList.add(auto);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return autoArrayList;
	}
}
