package com.kruiper.timon.v6informatica.requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kruiper.timon.v6informatica.callbacks.GetAutoCallback;
import com.kruiper.timon.v6informatica.callbacks.GetStringCallback;
import com.kruiper.timon.v6informatica.callbacks.GetUserCallback;
import com.kruiper.timon.v6informatica.objects.Auto;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.objects.Locatie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timon on 14-3-2016.
 */
public class ServerRequests {

	//Deze class zorgt er met verschillende functies voor dat er een post wordt verzonden
	//naar de server. In de verschillende functies worden post-parameters toegevoegd en wordt
	//hetgene wat de server terug stuurt, doorgestuurt. In samenwerking met de HttpRequest class
	//maakt deze een verbinding.

	ProgressDialog progressDialog;
	ActionProcessButton actionProcessButton;
	public static final String SERVER_ADDRESS = "http://informaticav6.16mb.com/";

	public ServerRequests(Context context){
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Laden");
		progressDialog.setMessage("Even geduld...");
	}

	public ServerRequests(ActionProcessButton actionProcessButton) {
		this.actionProcessButton = actionProcessButton;
	}

	//INLOGGEN
	public void getGebruikerData(Gebruiker gebruiker, GetUserCallback getUserCallback){
		actionProcessButton.setProgress(1);
		new getGebruikerDataAsyncTask(gebruiker,getUserCallback).execute(SERVER_ADDRESS + "Login.php");
	}
	private class getGebruikerDataAsyncTask extends AsyncTask<String, Void, Gebruiker>{
		Gebruiker gebruiker;
		GetUserCallback getUserCallback;

		public getGebruikerDataAsyncTask(Gebruiker gebruiker, GetUserCallback  userCallback){
			this.gebruiker = gebruiker;
			this.getUserCallback = userCallback;
		}

		@Override
		protected Gebruiker doInBackground(String... params) {
			Gebruiker returnedgebruiker = null;
			Map<String,String> data = new HashMap<>();
			data.put("gebruikersnaam", gebruiker.gebruikersnaam);
			data.put("wachtwoord", gebruiker.wachtwoord);
			String hoi = HttpRequest.post(params[0]).form(data).body();
			try {
				JSONObject jsonObject = new JSONObject(hoi);
				if(jsonObject.length() == 0){
					returnedgebruiker = null;
				} else {
					String naam = jsonObject.getString("naam");
					int age = jsonObject.getInt("leeftijd");
					int gebrid = jsonObject.getInt("gebrid");
					String email = jsonObject.getString("email");

					returnedgebruiker = new Gebruiker(gebrid,naam,gebruiker.gebruikersnaam,gebruiker.wachtwoord,age,email);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return returnedgebruiker;
		}

		@Override
		protected void onPostExecute(Gebruiker gebruiker) {
			getUserCallback.done(gebruiker);
			getUserCallback.donemsg(null);
			super.onPostExecute(gebruiker);
		}
	}

	//REGISTREREN
	public void slaGebruikerDataOp(Gebruiker gebruiker, GetUserCallback getUserCallback){
		actionProcessButton.setProgress(1);
		new slaGebruikerDataOpAsyncTask(gebruiker, getUserCallback).execute(SERVER_ADDRESS + "Registreer.php");
	}
	private class slaGebruikerDataOpAsyncTask extends AsyncTask<String, Void, String> {

		Gebruiker gebruiker;
		GetUserCallback getUserCallback;

		public slaGebruikerDataOpAsyncTask(Gebruiker gebruiker, GetUserCallback userCallback) {
			this.gebruiker = gebruiker;
			this.getUserCallback = userCallback;
		}

		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("naam",gebruiker.naam);
				data.put("gebruikersnaam", gebruiker.gebruikersnaam);
				data.put("wachtwoord", gebruiker.wachtwoord);
				data.put("leeftijd", gebruiker.leeftijd + "");
				data.put("email",gebruiker.email);
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");
				Log.d("marrie", hoi);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}

		@Override
		protected void onPostExecute(String s) {
			getUserCallback.donemsg(s);
			getUserCallback.done(null);
			super.onPostExecute(s);
		}
	}

	//WACHTWOORD VERGETEN
	public void Wachtwoordvergeten(String gebruikersnaam, GetUserCallback getUserCallback){
		progressDialog.show();
		new WachtwoordvergetenAsyncTask(gebruikersnaam,getUserCallback).execute(SERVER_ADDRESS + "Wachtwoord.php");
	}
	private class WachtwoordvergetenAsyncTask extends AsyncTask<String, Void, String>{

		String gebruikersnaam;
		GetUserCallback getUserCallback;

		public WachtwoordvergetenAsyncTask(String gebruikersnaam, GetUserCallback getUserCallback) {
			this.gebruikersnaam = gebruikersnaam;
			this.getUserCallback = getUserCallback;
		}
		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("gebruikersnaam",gebruikersnaam);
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}

		@Override
		protected void onPostExecute(String s) {
			progressDialog.dismiss();
			super.onPostExecute(s);
			getUserCallback.done(null);
			getUserCallback.donemsg(s);
		}
	}

	//GEGEVENS WIJZIGEN
	public void updateGebruiker(boolean wachtwoordwijzig, Gebruiker gebruiker, GetUserCallback getUserCallback){
		actionProcessButton.setProgress(1);
		new updateGebruikerAsyncTask(gebruiker,getUserCallback,wachtwoordwijzig).execute(SERVER_ADDRESS + "UpdateGebr.php");
	}
	private class updateGebruikerAsyncTask extends AsyncTask<String, Void, String>{

		Gebruiker gebruiker;
		GetUserCallback getUserCallback;
		boolean wachtwoordwijzig;

		public updateGebruikerAsyncTask(Gebruiker gebruiker, GetUserCallback getUserCallback, boolean wachtwoordwijzig) {
			this.gebruiker = gebruiker;
			this.getUserCallback = getUserCallback;
			this.wachtwoordwijzig = wachtwoordwijzig;
		}

		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("gebrid",gebruiker.gebrid+"");
				data.put("naam",gebruiker.naam);
				data.put("wachtwoord", gebruiker.wachtwoord+"");
				data.put("leeftijd", gebruiker.leeftijd + "");
				data.put("wijzigwachtwoord", wachtwoordwijzig+"");
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");
				Log.d("marrie", hoi);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;

		}
		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			getUserCallback.donemsg(s);
			getUserCallback.done(null);
		}
	}

	//DELETE GEBRUIKER
	public void deleteGebruiker(int gebrid, GetUserCallback getUserCallback){
		actionProcessButton.setProgress(1);
		new deleteGebruikerAsyncTask(gebrid, getUserCallback).execute(SERVER_ADDRESS + "DelGebr.php");
	}
	private class deleteGebruikerAsyncTask extends AsyncTask<String, Void, String>{

		int gebrid;
		GetUserCallback getUserCallback;

		public deleteGebruikerAsyncTask(int gebrid, GetUserCallback getUserCallback) {
			this.gebrid = gebrid;
			this.getUserCallback = getUserCallback;
		}

		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("gebrid",gebrid+"");
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}
		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			getUserCallback.done(null);
			getUserCallback.donemsg(s);
		}
	}

	//AUTO OPSLAAN
	public void slaAutoOp(Auto auto, GetAutoCallback getAutoCallback){
		actionProcessButton.setProgress(1);
		new slaAutoOpAsyncTask(auto, getAutoCallback).execute(SERVER_ADDRESS + "AddAuto.php");
	}
	private class slaAutoOpAsyncTask extends AsyncTask<String, Void, String>{

		Auto auto;
		GetAutoCallback getAutoCallback;

		public slaAutoOpAsyncTask(Auto auto, GetAutoCallback autoCallback){
			this.auto = auto;
			this.getAutoCallback = autoCallback;
		}

		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("gebrid",auto.gebrid + "");
				data.put("merk",auto.merk);
				data.put("type", auto.type);
				data.put("type_brandstof", auto.type_brandstof);
				data.put("kenteken", auto.kenteken);
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");
				Log.d("marrie", hoi);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}
		@Override
		protected void onPostExecute(String s) {
			getAutoCallback.donemsg(s);
			getAutoCallback.done(null);
			super.onPostExecute(s);
		}
	}

	//AUTO UPDATEN
	public void updateAuto(Auto auto, GetAutoCallback getAutoCallback){
		actionProcessButton.setProgress(1);
		new updateAutoAsyncTask(auto,getAutoCallback).execute(SERVER_ADDRESS + "UpdateAuto.php");
	}
	private class updateAutoAsyncTask extends AsyncTask<String, Void, String>{
		Auto auto;

		GetAutoCallback getAutoCallback;

		public updateAutoAsyncTask(Auto auto, GetAutoCallback getAutoCallback) {
			this.auto = auto;
			this.getAutoCallback = getAutoCallback;
		}

		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("autoid",auto.autoid + "");
				data.put("merk",auto.merk + "");
				data.put("type",auto.type + "");
				data.put("typebrandstof",auto.type_brandstof + "");
				data.put("kenteken",auto.kenteken + "");
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}
		@Override
		protected void onPostExecute(String s) {
			getAutoCallback.done(null);
			getAutoCallback.donemsg(s);
			super.onPostExecute(s);
		}

	}

	//AUTO DELETEN
	public void delAuto(int autoid, GetAutoCallback getAutoCallback){
		actionProcessButton.setProgress(1);
		new delAutoAsyncTask(autoid, getAutoCallback).execute(SERVER_ADDRESS + "DelAuto.php");
	}
	private class delAutoAsyncTask extends AsyncTask<String, Void, String>{

		int autoid;
		GetAutoCallback getAutoCallback;

		public delAutoAsyncTask(int autoid, GetAutoCallback getAutoCallback) {
			this.autoid = autoid;
			this.getAutoCallback = getAutoCallback;
		}

		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("autoid",autoid+"");
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}
		@Override
		protected void onPostExecute(String s) {
			getAutoCallback.done(null);
			getAutoCallback.donemsg(s);
			super.onPostExecute(s);
		}
	}

	//GEGEVENS JOU AUTOS
	public void getAutos(int gebrid, GetAutoCallback getAutoCallback){
		progressDialog.show();
		new getAutosAsyncTask(gebrid, getAutoCallback).execute(SERVER_ADDRESS + "GetAutos.php");
	}
	private class getAutosAsyncTask extends AsyncTask<String, Void, String>{

		int gebrid;
		GetAutoCallback getAutoCallback;

		public getAutosAsyncTask(int gebrid, GetAutoCallback getAutoCallback) {
			this.gebrid = gebrid;
			this.getAutoCallback = getAutoCallback;
		}

		@Override
		protected String doInBackground(String... params) {
			String hoi = null;
			Map<String,String> data = new HashMap<>();
			data.put("gebrid",gebrid+"");
			hoi = HttpRequest.post(params[0]).form(data).body();
			return hoi;
		}

		@Override
		protected void onPostExecute(String s) {
			progressDialog.dismiss();
			super.onPostExecute(s);
			getAutoCallback.donemsg(s);
			getAutoCallback.done(null);
		}
	}

	//GEGEVENS EEN AUTO
	public void getAuto(int autoid, GetAutoCallback getAutoCallback){
		progressDialog.show();
		new getAutoAsyncTask(autoid, getAutoCallback).execute(SERVER_ADDRESS + "GetAuto.php");
	}
	private class getAutoAsyncTask extends AsyncTask<String, Void, Auto>{

		int autoid;
		GetAutoCallback getAutoCallback;

		public getAutoAsyncTask(int autoid, GetAutoCallback getAutoCallback) {
			this.autoid = autoid;
			this.getAutoCallback = getAutoCallback;
		}

		@Override
		protected Auto doInBackground(String... params) {
			Auto auto = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("autoid",autoid + "");
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				if(hoi.equals("[]")){
					auto = null;
				} else {
					int gebrid = jsonObject.getInt("gebrid");
					String merk = jsonObject.getString("merk");
					String type = jsonObject.getString("type");
					String typebrandstof = jsonObject.getString("typebrandstof");
					String kenteken = jsonObject.getString("kenteken");

					auto = new Auto(gebrid,merk,type,typebrandstof,kenteken);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return auto;
		}
		@Override
		protected void onPostExecute(Auto auto) {
			progressDialog.dismiss();
			getAutoCallback.done(auto);
			getAutoCallback.donemsg(null);
			super.onPostExecute(auto);
		}
	}

	//LOCATIES OPHALEN
	public void getLocaties(GetStringCallback getStringCallback){
		progressDialog.show();
		new getLocatiesAsyncTask(getStringCallback).execute(SERVER_ADDRESS + "getLocaties.php");
	}
	private class getLocatiesAsyncTask extends AsyncTask<String, Void, String>{

		GetStringCallback getStringCallback;

		public getLocatiesAsyncTask(GetStringCallback getStringCallback) {
			this.getStringCallback = getStringCallback;
		}
		@Override
		protected String doInBackground(String... params) {
			String hoi = null;
			hoi = HttpRequest.post(params[0]).body();
			return hoi;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			progressDialog.dismiss();
			getStringCallback.done(s);
		}
	}

	//SET LOCATIE
	public void setLocatie(Locatie locatie, GetStringCallback getStringCallback){
		new setLocatieAsyncTask(locatie,getStringCallback).execute(SERVER_ADDRESS + "SaveLocatie.php");
	}
	private class setLocatieAsyncTask extends AsyncTask<String, Void, String>{

		Locatie locatie;
		GetStringCallback getStringCallback;

		public setLocatieAsyncTask(Locatie locatie, GetStringCallback getStringCallback) {
			this.locatie = locatie;
			this.getStringCallback = getStringCallback;
		}
		@Override
		protected String doInBackground(String... params) {
			String s = null;
			try{
				Map<String,String> data = new HashMap<>();
				data.put("gebrid",locatie.gebrid+"");
				data.put("lat",locatie.lat+"");
				data.put("lng",locatie.lng+"");
				String hoi = HttpRequest.post(params[0]).form(data).body();
				JSONObject jsonObject = new JSONObject(hoi);
				s = jsonObject.getString("query_result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			getStringCallback.done(s);
		}
	}

	}
