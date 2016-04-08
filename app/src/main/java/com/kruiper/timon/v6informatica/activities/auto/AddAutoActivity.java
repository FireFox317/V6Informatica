package com.kruiper.timon.v6informatica.activities.auto;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.callbacks.GetAutoCallback;
import com.kruiper.timon.v6informatica.objects.Auto;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.requests.GebruikerDB;
import com.kruiper.timon.v6informatica.requests.ServerRequests;


public class AddAutoActivity extends AppCompatActivity implements View.OnClickListener {

	EditText etMerk, etType, etKenteken;
	Spinner spTypeBrandstof;
	ActionProcessButton bAddAuto;
	GebruikerDB gebruikerDB;
	int gebrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_auto);

		//Terug knop op de ActionBar zetten
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Views koppelen aan xml id
		etMerk = (EditText) findViewById(R.id.etMerk);
		etType = (EditText) findViewById(R.id.etType);
		spTypeBrandstof = (Spinner) findViewById(R.id.spTypeBrandstof);
		etKenteken = (EditText) findViewById(R.id.etKenteken);
		bAddAuto = (ActionProcessButton) findViewById(R.id.bAddAuto);

		//ActionProcessButton mode
		bAddAuto.setMode(ActionProcessButton.Mode.ENDLESS);

		//Lokale database ophalen
		gebruikerDB = new GebruikerDB(this);

		//Gebruiker uit de lokale database halen en gebrid gebruiken
		Gebruiker gebruiker = gebruikerDB.gebruikerIngelogd();
		gebrid = gebruiker.gebrid;

		//onClickListener button addAuto
		bAddAuto.setOnClickListener(this);
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch(v.getId()){

			case R.id.bAddAuto:
				String merk = etMerk.getText().toString();
				String type = etType.getText().toString();
				String typebrandstof = spTypeBrandstof.getSelectedItem().toString();
				String kenteken = etKenteken.getText().toString();

				Auto addAuto = new Auto(gebrid,merk,type,typebrandstof,kenteken);

				etMerk.setEnabled(false);
				etType.setEnabled(false);
				spTypeBrandstof.setEnabled(false);
				etKenteken.setEnabled(false);

				voegAutotoe(addAuto);
				break;
		}
	}

	private void voegAutotoe(Auto auto) {
		//Er wordt een request naar de server gedaan.
		ServerRequests serverRequests = new ServerRequests(bAddAuto);

		serverRequests.slaAutoOp(auto, new GetAutoCallback() {
			@Override
			public void done(Auto auto) {}

			@Override
			public void donemsg(String s) {
				//Als auto goed is opgeslagen, weer terug gaan 'finish()' anders errormsg()
				if(s.equals("SUCCES")){
					bAddAuto.setProgress(100);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							setResult(Activity.RESULT_OK);
							finish();
						}
					}, 1000);
				} else {
					errormsg();
				}
			}
		});
	}

	private void errormsg() {
		//Knop error laten weergeven en naar 1 seconde alles weer terug zetten
		bAddAuto.setProgress(-1);
		Toast.makeText(AddAutoActivity.this, "Er is iets fout gegaan..", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				etMerk.setEnabled(true);
				etType.setEnabled(true);
				spTypeBrandstof.setEnabled(true);
				etKenteken.setEnabled(true);
				bAddAuto.setProgress(0);
			}
		}, 1000);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				setResult(Activity.RESULT_CANCELED);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			setResult(Activity.RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
