package com.kruiper.timon.v6informatica.activities.gegwijzigen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.requests.GebruikerDB;
import com.kruiper.timon.v6informatica.callbacks.GetUserCallback;
import com.kruiper.timon.v6informatica.requests.ServerRequests;

public class GegevensWijzigenActivity extends AppCompatActivity implements View.OnClickListener {

	Button bNaamwijzigen, bLeeftijdwijzigen, bWachtwoordwijzigen, bEmailWijzigen;
	ActionProcessButton bOpslaan, bDelGebr;
	EditText etNaamwijzigen, etLeeftijdwijzigen, etOudeWachtwoord, etNieuweWachtwoord, etEmailWijzigen;
	GebruikerDB gebruikerDB;
	Gebruiker gebruiker1;
	Gebruiker gebruiker = null;
	Boolean wijzigwachtwoord = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gegevens_wijzigen);

		//Terug knop op de ActionBar zetten
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Views koppelen aan xml id
		bNaamwijzigen = (Button) findViewById(R.id.bNaamwijzigen);
		bLeeftijdwijzigen = (Button) findViewById(R.id.bLeeftijdwijzigen);
		bWachtwoordwijzigen = (Button) findViewById(R.id.bWachtwoordwijzigen);
		bOpslaan = (ActionProcessButton) findViewById(R.id.bOpslaan);
		bDelGebr = (ActionProcessButton) findViewById(R.id.bDelGebr);
		etNaamwijzigen = (EditText) findViewById(R.id.etNaamWijzigen);
		etLeeftijdwijzigen = (EditText) findViewById(R.id.etLeeftijdWijzigen);
		etOudeWachtwoord = (EditText) findViewById(R.id.etOudeWachtwoord);
		etNieuweWachtwoord = (EditText) findViewById(R.id.etNieuweWachtwoord);
		bEmailWijzigen = (Button) findViewById(R.id.bEmailWijzigen);
		etEmailWijzigen = (EditText) findViewById(R.id.etEmailWijzigen);

		//ActionProcessButton mode
		bOpslaan.setMode(ActionProcessButton.Mode.ENDLESS);
		bDelGebr.setMode(ActionProcessButton.Mode.ENDLESS);

		//Lokale database ophalen en de gegevens van gebruiker in gebruiker1 zetten
		gebruikerDB = new GebruikerDB(this);
		gebruiker1 = gebruikerDB.gebruikerIngelogd();

		//EditText vullen met gegevens van gebruiker
		etNaamwijzigen.setText(gebruiker1.naam);
		etLeeftijdwijzigen.setText(gebruiker1.leeftijd+"");
		etEmailWijzigen.setText(gebruiker1.email);

		//onClickListeners buttons
		bNaamwijzigen.setOnClickListener(this);
		bLeeftijdwijzigen.setOnClickListener(this);
		bWachtwoordwijzigen.setOnClickListener(this);
		bOpslaan.setOnClickListener(this);
		bDelGebr.setOnClickListener(this);
		bEmailWijzigen.setOnClickListener(this);
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch (v.getId()){

			//Deze knoppen zorgen ervoor dat EditText wel of niet zichtbaar is
			case R.id.bNaamwijzigen:
				if(etNaamwijzigen.getVisibility() == View.VISIBLE){
					etNaamwijzigen.setVisibility(View.GONE);
				} else {
					etNaamwijzigen.setVisibility(View.VISIBLE);
				}
				break;

			case R.id.bLeeftijdwijzigen:
				if(etLeeftijdwijzigen.getVisibility() == View.VISIBLE){
					etLeeftijdwijzigen.setVisibility(View.GONE);
				} else {
					etLeeftijdwijzigen.setVisibility(View.VISIBLE);
				}
				break;

			case R.id.bWachtwoordwijzigen:
				if(etOudeWachtwoord.getVisibility() == View.VISIBLE){
					etNieuweWachtwoord.setVisibility(View.GONE);
					etOudeWachtwoord.setVisibility(View.GONE);
				} else {
					etNieuweWachtwoord.setVisibility(View.VISIBLE);
					etOudeWachtwoord.setVisibility(View.VISIBLE);
				}
				break;

			case R.id.bEmailWijzigen:
				if(etEmailWijzigen.getVisibility() == View.VISIBLE){
					etEmailWijzigen.setVisibility(View.GONE);
				} else {
					etEmailWijzigen.setVisibility(View.VISIBLE);
				}
				break;

			case R.id.bOpslaan:
				saveGebruiker();
				break;

			case R.id.bDelGebr:
				delGebruiker();
				break;
		}

	}

	//Deze wordt aangeroepen wanneer de gebruiker op de delete knop drukt
	private void delGebruiker() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("LET OP!");
		builder.setMessage("Weet je zeker dat je je account wilt verwijderen? Al uw gegevens worden gewist.");
		builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				etNaamwijzigen.setEnabled(false);
				etLeeftijdwijzigen.setEnabled(false);
				etNieuweWachtwoord.setEnabled(false);
				etOudeWachtwoord.setEnabled(false);
				etEmailWijzigen.setEnabled(false);
				deleteGebruiker(gebruiker1.gebrid);
			}
		});
		builder.setNegativeButton("Nee", null);
		builder.show();
	}

	private void deleteGebruiker(int gebrid) {
		//Er wordt een request naar de server gedaan.
		ServerRequests requests = new ServerRequests(bDelGebr);

		requests.deleteGebruiker(gebruiker1.gebrid, new GetUserCallback() {
			@Override
			public void done(Gebruiker gebruiker) {}

			@Override
			public void donemsg(String s) {
				if (s.equals("SUCCES")) {
					//Als verwijderen  gelukt is, gebruiker uitloggen en uit de lokale database halen
					bDelGebr.setProgress(100);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							gebruikerDB.verwijderGebruikerData();
							gebruikerDB.veranderGebruikerIngelogd(false);
							finish();
						}
					}, 1000);
				} else {
					bDelGebr.setProgress(-1);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							bDelGebr.setProgress(0);
							etNaamwijzigen.setEnabled(true);
							etLeeftijdwijzigen.setEnabled(true);
							etNieuweWachtwoord.setEnabled(true);
							etEmailWijzigen.setEnabled(true);
							etOudeWachtwoord.setEnabled(true);
						}
					}, 1000);
				}
			}
		});
	}

	//Deze wordt aangeroepen wanneer de gebruiker op de opslaan knop drukt
	private void saveGebruiker() {
		String naam = etNaamwijzigen.getText().toString();
		String leeftijd = etLeeftijdwijzigen.getText().toString();
		String oudewachtwoord = etOudeWachtwoord.getText().toString();
		String nieuwewachtwoord = etNieuweWachtwoord.getText().toString();
		String email = etEmailWijzigen.getText().toString();
		if(oudewachtwoord.equals("") && nieuwewachtwoord.equals("")){
			wijzigwachtwoord = false;
			gebruiker = new Gebruiker(gebruiker1.gebrid,naam,gebruiker1.gebruikersnaam,null,Integer.parseInt(leeftijd),email);
		} else {
			if(oudewachtwoord.equals(gebruiker1.wachtwoord)){
				wijzigwachtwoord = true;
				gebruiker = new Gebruiker(gebruiker1.gebrid,naam,gebruiker1.gebruikersnaam,nieuwewachtwoord,Integer.parseInt(leeftijd),email);
			} else {
				Toast.makeText(GegevensWijzigenActivity.this, "Oude wachtwoord klopt niet..", Toast.LENGTH_SHORT).show();
			}
		}
		if(wijzigwachtwoord != null){
			etNaamwijzigen.setEnabled(false);
			etLeeftijdwijzigen.setEnabled(false);
			etEmailWijzigen.setEnabled(false);
			etNieuweWachtwoord.setEnabled(false);
			etOudeWachtwoord.setEnabled(false);

			ServerRequests requests = new ServerRequests(bOpslaan);

			requests.updateGebruiker(wijzigwachtwoord, gebruiker, new GetUserCallback() {
				@Override
				public void done(Gebruiker gebruiker) {	}

				@Override
				public void donemsg(String s) {
					if(s.equals("SUCCES")){
						bOpslaan.setProgress(100);
						if(wijzigwachtwoord == true){
							gebruikerDB.slaGebruikerOp(gebruiker);
						} else {
							gebruiker.wachtwoord = gebruiker1.wachtwoord;
							gebruikerDB.slaGebruikerOp(gebruiker);
						}
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
							}
						},1000);
					} else {
						bOpslaan.setProgress(-1);
						Toast.makeText(GegevensWijzigenActivity.this, "Er is iets fout gegaan", Toast.LENGTH_LONG).show();

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								bOpslaan.setProgress(0);
								etNaamwijzigen.setEnabled(true);
								etLeeftijdwijzigen.setEnabled(true);
								etEmailWijzigen.setEnabled(true);
								etNieuweWachtwoord.setEnabled(true);
								etOudeWachtwoord.setEnabled(true);
							}
						}, 1000);
					}
				}
			});
		}
	}
}
