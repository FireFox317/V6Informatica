package com.kruiper.timon.v6informatica.activities.locatie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.callbacks.GetStringCallback;
import com.kruiper.timon.v6informatica.helper.ParseJSON;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.objects.Locatie;
import com.kruiper.timon.v6informatica.requests.GebruikerDB;
import com.kruiper.timon.v6informatica.requests.ServerRequests;

import java.util.ArrayList;

public class LocatieActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

	ActionProcessButton bGPS, bNetwerk;
	LocationManager locationManager;
	LocationListener locationListener;
	Gebruiker gebruiker;
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locatie);

		//Terug knop op de ActionBar zetten
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Google maps map setup
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		//Deze zorgt ervoor dat je de locatie van de gebruiker kunt gebruiken
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		//Lokale database ophalen en ingelogde gebruiker ophalen
		GebruikerDB gebruikerDB = new GebruikerDB(this);
		gebruiker = gebruikerDB.gebruikerIngelogd();

		//setup button GPS
		bGPS = (ActionProcessButton) findViewById(R.id.bGPS);
		bGPS.setMode(ActionProcessButton.Mode.ENDLESS);
		bGPS.setOnClickListener(this);

		//setup button Netwerk
		bNetwerk = (ActionProcessButton) findViewById(R.id.bNetwork);
		bNetwerk.setMode(ActionProcessButton.Mode.ENDLESS);
		bNetwerk.setOnClickListener(this);
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch(v.getId()){

			case R.id.bGPS:
				bGPS.setProgress(1);
				//Hier checkt die of GPS aanstaat, zo niet dan geeft die een error
				if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					bGPS.setProgress(-1);
					alertDialog(bGPS,"GPS");
				} else {
					locationListener = new MijnLocatie(bGPS);
					try {
						//Vraag locatie via Gps op
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
					} catch(SecurityException e){
						e.printStackTrace();
					}
				}
				break;

			case R.id.bNetwork:
				bNetwerk.setProgress(1);
				//Checken of de locatie via netwerk kan worden op gehaald
				if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					bNetwerk.setProgress(-1);
					alertDialog(bNetwerk,"Netwerk");
				} else {
					locationListener = new MijnLocatie(bNetwerk);
					try {
						//Vraag locatie via netwerk op
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
					} catch(SecurityException e){
						e.printStackTrace();
					}
				}
				break;
		}
	}

	private void alertDialog(final ActionProcessButton btn, String soort) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Je " + soort + " staat uit, wil je hem aanzetten?");
		builder.setCancelable(false);
		builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				btn.setProgress(0);
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		}).setNegativeButton("Nee", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				btn.setProgress(0);
				dialog.dismiss();
			}
		});
		builder.show();
	}

	//Deze functie wordt aangeroepen wanneer de Google Map klaar is met laden.
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		//Hier haalt die alle locaties van alle gebruikers van de server en toont ze met een marker
		ServerRequests requests = new ServerRequests(this);
		requests.getLocaties(new GetStringCallback() {
			@Override
			public void done(String s) {
				if (s.equals("[]")) {
					//Geen locaties, map op Nederland zetten
					Toast.makeText(LocatieActivity.this, "Er zijn geen locaties beschikbaar om te tonen...", Toast.LENGTH_SHORT).show();
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.391076, 6.266896), 5));
				} else {
					ParseJSON pj = new ParseJSON(s);
					ArrayList<Locatie> locaties = pj.parseJSONloc();

					//Voor elke locatie die gevonden is een marker plaatsen
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for(int i=0; i < locaties.size(); i++){
						LatLng marker = new LatLng(locaties.get(i).lat,locaties.get(i).lng);
						mMap.addMarker(new MarkerOptions().position(marker).title(locaties.get(i).naam));
						builder.include(marker);
					}
					LatLngBounds bounds = builder.build();

					//De camera zo plaatsen dat elke marker te zien is
					mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
				}
			}
		});

	}

	//Deze class wordt gebruikt bij het ophalen van de locatie van de gebruiker
	private class MijnLocatie implements LocationListener {

		ActionProcessButton btn;

		public MijnLocatie(ActionProcessButton btn) {
			this.btn = btn;
		}

		@Override
		public void onLocationChanged(Location l) {
			Toast.makeText(LocatieActivity.this, "Longitude: " + l.getLongitude() + "\r\nLatitude: " + l.getLatitude(), Toast.LENGTH_SHORT).show();
			Locatie locatie = new Locatie(gebruiker.gebrid,l.getLatitude(),l.getLongitude());

			//Deze locatie naar de database op de server posten
			ServerRequests requests = new ServerRequests(btn);

			requests.setLocatie(locatie, new GetStringCallback() {
				@Override
				public void done(String s) {
					if (s.equals("SUCCES")) {
						Toast.makeText(LocatieActivity.this, "Uw locatie is opgeslagen in de database!", Toast.LENGTH_SHORT).show();
						btn.setProgress(100);

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								btn.setProgress(0);
							}
						}, 1000);
					} else {
						Toast.makeText(LocatieActivity.this, "Opslaan van gegevens in database niet gelukt!", Toast.LENGTH_SHORT).show();
						btn.setProgress(-1);

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								btn.setProgress(0);
							}
						}, 1000);
					}
				}
			});

			try{
				locationManager.removeUpdates(locationListener);
			} catch(SecurityException e){
				e.printStackTrace();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
	}

}
