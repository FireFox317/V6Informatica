package com.kruiper.timon.v6informatica.requests;

import android.content.Context;
import android.content.SharedPreferences;

import com.kruiper.timon.v6informatica.objects.Gebruiker;

/**
 * Created by timon on 14-3-2016.
 */
public class GebruikerDB {

	//Dit bestandje zorgt ervoor dat de Gebruiker die is ingelogd op de lokale database
	//van de Gebruiker wordt opgeslagen, er zijn verschillende functies die kunnen worden
	//aangeroepen.

	public static final String SP_NAME = "gegevensGebruiker";
	SharedPreferences gebruikerSP;

	public GebruikerDB(Context context){
		gebruikerSP = context.getSharedPreferences(SP_NAME,0);
	}

	public void slaGebruikerOp(Gebruiker gebruiker){
		SharedPreferences.Editor editor = gebruikerSP.edit();
		editor.putInt("gebrid",gebruiker.gebrid);
		editor.putString("naam",gebruiker.naam);
		editor.putString("gebruikersnaam",gebruiker.gebruikersnaam);
		editor.putString("wachtwoord",gebruiker.wachtwoord);
		editor.putInt("leeftijd", gebruiker.leeftijd);
		editor.putString("email",gebruiker.email);
		editor.commit();
	}

	public Gebruiker gebruikerIngelogd(){
		int id = gebruikerSP.getInt("gebrid",0);
		String naam = gebruikerSP.getString("naam", "");
		String gebruikersnaam = gebruikerSP.getString("gebruikersnaam","");
		String wachtwoord = gebruikerSP.getString("wachtwoord","");
		int leeftijd = gebruikerSP.getInt("leeftijd", -1);
		String email = gebruikerSP.getString("email","");

		Gebruiker opgeslagenGebruiker = new Gebruiker(id,naam,gebruikersnaam,wachtwoord,leeftijd,email);
		return opgeslagenGebruiker;
	}

	public void veranderGebruikerIngelogd(boolean ingelogd){
		SharedPreferences.Editor editor = gebruikerSP.edit();
		editor.putBoolean("ingelogd",ingelogd);
		editor.commit();
	}

	public boolean isGebruikerIngelogd(){
		if(gebruikerSP.getBoolean("ingelogd",false) == true){
			return true;
		} else {
			return false;
		}
	}

	public void verwijderGebruikerData(){
		SharedPreferences.Editor editor = gebruikerSP.edit();
		editor.clear();
		editor.commit();
	}
}

