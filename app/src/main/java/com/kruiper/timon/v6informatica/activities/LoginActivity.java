package com.kruiper.timon.v6informatica.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.callbacks.GetUserCallback;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.requests.GebruikerDB;
import com.kruiper.timon.v6informatica.requests.ServerRequests;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

	ActionProcessButton bLogin;
	EditText etGebruikersnaam, etWachtwoord;
	TextView tvRegistreerLink, tvWachtwoordvergeten;
	GebruikerDB gebruikerDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//Views koppelen aan xml id
		bLogin = (ActionProcessButton) findViewById(R.id.bLogin);
		etGebruikersnaam = (EditText) findViewById(R.id.etGebruikersnaam);
		etWachtwoord = (EditText) findViewById(R.id.etWachtwoord);
		tvRegistreerLink = (TextView) findViewById(R.id.tvRegistreerLink);
		tvWachtwoordvergeten = (TextView) findViewById(R.id.tvWachtwoordvergeten);

		//Lokale database ophalen
		gebruikerDB = new GebruikerDB(this);

		//ActionProcessButton mode
		bLogin.setMode(ActionProcessButton.Mode.ENDLESS);

		//onClickListeners buttons
		bLogin.setOnClickListener(this);
		tvRegistreerLink.setOnClickListener(this);
		tvWachtwoordvergeten.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//Als gebruiker is ingelogd doorsturen naar MainActivity
		if(gebruikerDB.isGebruikerIngelogd()){
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch(v.getId()){

			case R.id.bLogin:
				//Gegevens uit EditText halen en er een Gebruiker van maken
				String gebruikersnaam = etGebruikersnaam.getText().toString();
				String wachtwoord = etWachtwoord.getText().toString();
				Gebruiker gebruiker = new Gebruiker(gebruikersnaam,wachtwoord);

				//Button en EditText zo maken dat je er niet meer op kunt klikken.
				bLogin.setEnabled(false);
				etGebruikersnaam.setEnabled(false);
				etWachtwoord.setEnabled(false);

				//De gebruiker doorsturen naar de functie authentiseer
				authentiseer(gebruiker);
				break;

			case R.id.tvRegistreerLink:
				//RegistreerActivity starten
				startActivity(new Intent(this, RegistreerActivity.class));
				break;

			case R.id.tvWachtwoordvergeten:
				//Hier wordt een AlertDialog gemaakt, die de gebruiker vraagt om zijn gebruikersnaam in te vullen.
				//Als deze wordt gevonden, wordt er een email naar de gebruiker gestuurd.
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setTitle("Gebruikersnaam");
				builder.setMessage("Voer gebruikersnaam in");

				final EditText input = new EditText(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				builder.setView(input);

				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Als er op OK wordt gedrukt, de gebruikersnaam uit de EditText halen
						//en doorsturen naar de methode 'wachtwoordvergeten'
						String gebrnaam = input.getText().toString();
						wachtwoordvergeten(gebrnaam);
					}
				});
				builder.setNegativeButton("Annuleren",null);

				builder.show();
				break;
		}
	}

	private void authentiseer(Gebruiker gebruiker) {
		//Er wordt een request naar de server gedaan.
		ServerRequests serverRequest = new ServerRequests(bLogin);

		serverRequest.getGebruikerData(gebruiker, new GetUserCallback() {
			@Override
			public void done(final Gebruiker gebruiker) {
				//Als gebruiker null is dan bestaat de gebruiker niet en mag deze dus niet inloggen.
				if(gebruiker == null){
					errormsg();
				} else {
					//Gebruiker inloggen
					bLogin.setProgress(100);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							logGebruikerIn(gebruiker);
						}
					},1000);
				}
			}

			@Override
			public void donemsg(String string) {}
		});
	}

	private void logGebruikerIn(Gebruiker gebruiker) {
		//Gebruiker opslaan in de lokale database van de gebruiker, zodat we deze later weer kunnen gebruiken.
		gebruikerDB.slaGebruikerOp(gebruiker);
		gebruikerDB.veranderGebruikerIngelogd(true);

		//Gebruiker doorsturen naar de MainActivity
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	private void errormsg() {
		//ActionProcessButon veranderen in Error en Toast geven dat de gegevens niet goed zijn.
		bLogin.setProgress(-1);
		Toast.makeText(LoginActivity.this, "Gebruikersnaam en wachtwoord zijn niet gelijk aan elkaar!", Toast.LENGTH_LONG).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//Buttons etc weer goed zetten zodat gebruiker opnieuw kan inloggen.
				bLogin.setProgress(0);
				bLogin.setEnabled(true);
				etGebruikersnaam.setEnabled(true);
				etWachtwoord.setEnabled(true);
			}
		}, 1000);
	}

	private void wachtwoordvergeten(String gebrnaam) {
		//Request naar de server sturen met de gebrnaam en kijken of mail wel of niet is verzonden.
		ServerRequests serverRequests = new ServerRequests(this);

		serverRequests.Wachtwoordvergeten(gebrnaam, new GetUserCallback() {
			@Override
			public void done(Gebruiker gebruiker) {}

			@Override
			public void donemsg(String s) {
				if (s.equals("SUCCES")) {
					Toast.makeText(LoginActivity.this, "Mail verzonden!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this, "Mail niet verzonden!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
