package com.kruiper.timon.v6informatica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.activities.auto.AutoActivity;
import com.kruiper.timon.v6informatica.activities.gegwijzigen.GegevensWijzigenActivity;
import com.kruiper.timon.v6informatica.activities.locatie.LocatieActivity;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.requests.GebruikerDB;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	TextView tvNaam, tvLeeftijd, tvGebruikersnaam;
	Button bAuto, bGegevenswijzigen, bLocatie;
	GebruikerDB gebruikerDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Views koppelen aan xml id
		tvNaam = (TextView) findViewById(R.id.tvNaam);
		tvLeeftijd = (TextView) findViewById(R.id.tvLeeftijd);
		tvGebruikersnaam = (TextView) findViewById(R.id.tvGebruikersnaam);
		bGegevenswijzigen = (Button) findViewById(R.id.bGegevenswijzigen);
		bAuto = (Button) findViewById(R.id.bAuto);
		bLocatie = (Button) findViewById(R.id.bLocatie);

		//Lokale database ophalen
		gebruikerDB = new GebruikerDB(this);

		//onClickListeners buttons
		bAuto.setOnClickListener(this);
		bGegevenswijzigen.setOnClickListener(this);
		bLocatie.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//Als gebruiker is ingelogd data laden en anders doorsturen naar LoginActivity
		if (gebruikerDB.isGebruikerIngelogd()) {
			laatGebruikerDataZien();
		} else {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}

	private void laatGebruikerDataZien() {
		//Gebruiker ophalen uit lokale database
		Gebruiker gebruiker = gebruikerDB.gebruikerIngelogd();

		//Gegevens van de gebruiker in de verschillende textviews zetten
		tvGebruikersnaam.setText(gebruiker.gebruikersnaam);
		tvNaam.setText(gebruiker.naam);
		tvLeeftijd.setText(gebruiker.leeftijd + "");
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.bAuto:
				startActivity(new Intent(this, AutoActivity.class));
				break;

			case R.id.bGegevenswijzigen:
				startActivity(new Intent(this, GegevensWijzigenActivity.class));
				break;

			case R.id.bLocatie:
				startActivity(new Intent(this, LocatieActivity.class));
				break;
		}
	}

	//Deze functie wordt gebruikt om de xml file menu_main toe tewijzen naar het menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	//Deze functie wordt aangeroepen wanneer er op een menuitem wordt gedrukt
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.logout:
				//Als de gebruiker op de uitlog-knop drukt, de gegevens van de gebruiker uit de lokale database verwijderen
				gebruikerDB.verwijderGebruikerData();
				gebruikerDB.veranderGebruikerIngelogd(false);

				//Gebruiker doorsturen naar LoginActivity
				startActivity(new Intent(this, LoginActivity.class));
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

}
