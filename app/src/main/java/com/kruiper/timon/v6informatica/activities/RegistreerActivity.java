package com.kruiper.timon.v6informatica.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.callbacks.GetUserCallback;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.requests.ServerRequests;

public class RegistreerActivity extends AppCompatActivity implements View.OnClickListener {

	ActionProcessButton bRegistreer;
	EditText etNaam, etLeeftijd, etGebruikersnaam, etWachtwoord, etEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		//Terug knop op de ActionBar zetten
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Views koppelen aan xml id
		etNaam = (EditText) findViewById(R.id.etNaam);
		etLeeftijd = (EditText) findViewById(R.id.etLeeftijd);
		etGebruikersnaam = (EditText) findViewById(R.id.etGebruikersnaam);
		etWachtwoord = (EditText) findViewById(R.id.etWachtwoord);
		etEmail = (EditText) findViewById(R.id.etEmail);
		bRegistreer = (ActionProcessButton) findViewById(R.id.bRegistreer);

		//ActionProcessButton mode
		bRegistreer.setMode(ActionProcessButton.Mode.ENDLESS);

		//onClickListener button registreer
		bRegistreer.setOnClickListener(this);
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch(v.getId()){

			case R.id.bRegistreer:
				//Gegevens uit EditText halen en er een Gebruiker van maken
				String naam = etNaam.getText().toString();
				String gebruikersnaam = etGebruikersnaam.getText().toString();
				String wachtwoord = etWachtwoord.getText().toString();
				String email = etEmail.getText().toString();
				int leeftijd = Integer.parseInt(etLeeftijd.getText().toString());

				Gebruiker registreerData = new Gebruiker(naam,gebruikersnaam,wachtwoord,leeftijd,email);

				//Button en EditText zo maken dat je er niet meer op kunt klikken.
				etNaam.setEnabled(false);
				etWachtwoord.setEnabled(false);
				etGebruikersnaam.setEnabled(false);
				etLeeftijd.setEnabled(false);
				etEmail.setEnabled(false);
				bRegistreer.setEnabled(false);

				//De gebruiker doorsturen naar de functie registreerGebruiker
				registreerGebruiker(registreerData);
				break;
		}
	}

	private void registreerGebruiker(Gebruiker registreerData) {
		//Er wordt een request naar de server gedaan.
		ServerRequests serverRequests = new ServerRequests(bRegistreer);

		serverRequests.slaGebruikerDataOp(registreerData, new GetUserCallback() {
			@Override
			public void done(Gebruiker gebruiker) {}

			@Override
			public void donemsg(String s) {
				if(s.equals("SUCCES")){
					//'s' is hetgene wat de server terug stuurt, als dit SUCCES is dan is de gebruiker aangemaakt.
					bRegistreer.setProgress(100);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					},1000);

				} else {
					//Gebruiker is niet toegevoegd, bv omdat gebrnaam al bestaat
					errormsg();
				}

			}
		});
	}

	private void errormsg() {
		//ActionProcessButon veranderen in Error en Toast geven dat de gegevens niet goed zijn.
		bRegistreer.setProgress(-1);
		Toast.makeText(RegistreerActivity.this, "Gebruikersnaam bestaat al!", Toast.LENGTH_LONG).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//Buttons etc weer goed zetten zodat gebruiker opnieuw kan inloggen.
				bRegistreer.setProgress(0);
				etNaam.setEnabled(true);
				etWachtwoord.setEnabled(true);
				etGebruikersnaam.setEnabled(true);
				etEmail.setEnabled(true);
				etLeeftijd.setEnabled(true);
				bRegistreer.setEnabled(true);
			}
		},1000);
	}
}
