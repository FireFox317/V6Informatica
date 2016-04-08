package com.kruiper.timon.v6informatica.activities.auto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.helper.AutosList;
import com.kruiper.timon.v6informatica.helper.ParseJSON;
import com.kruiper.timon.v6informatica.objects.Auto;
import com.kruiper.timon.v6informatica.objects.Gebruiker;
import com.kruiper.timon.v6informatica.requests.GebruikerDB;
import com.kruiper.timon.v6informatica.callbacks.GetAutoCallback;
import com.kruiper.timon.v6informatica.requests.ServerRequests;

import java.util.ArrayList;

public class AutoActivity extends AppCompatActivity {

	EditText etFilterAuto;
	ListView lvAutos;
	GebruikerDB gebruikerDB;
	int gebrid;
	AutosList cl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto);

		//Terug knop op de ActionBar zetten
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Views koppelen aan xml id
		lvAutos = (ListView) findViewById(R.id.lvAutos);
		etFilterAuto = (EditText) findViewById(R.id.etFilterAuto);

		//Lokale database ophalen
		gebruikerDB = new GebruikerDB(this);

		//Gebruiker uit de lokale database halen en gebrid gebruiken
		Gebruiker gebruiker = gebruikerDB.gebruikerIngelogd();
		gebrid = gebruiker.gebrid;

		//Data uit de database halen en in ListView zetten
		getData(gebrid);

		//Onderstaande zorgt ervoor dat je de EditText kunt gebruiken om de lijst te filteren
		etFilterAuto.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				cl.getFilter().filter(s);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		//Als je op een item in een lijst klikt, stuur gebruiker door naar ChangeAutoActivity
		lvAutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(AutoActivity.this, ChangeAutoActivity.class);
				i.putExtra("autoid", (int) id);
				Log.d("marrie", id + "");
				startActivityForResult(i, 1);
			}
		});
	}

	private void getData(int gebrid) {
		//Er wordt een request naar de server gedaan.
		ServerRequests requests = new ServerRequests(this);

		requests.getAutos(gebrid, new GetAutoCallback() {
			@Override
			public void done(Auto auto) {}

			@Override
			public void donemsg(String s) {
				//Response van de server doorsturen naar showJson()
				showJSON(s);
			}
		});
	}

	private void showJSON(String response) {
		//Als response gelijk is aan [] zijn er nog geen auto's toegevoegd, anders auto's laten zien
		if(response.equals("[]")){
			Toast.makeText(AutoActivity.this, "U heeft nog geen auto's toegevoegd!", Toast.LENGTH_SHORT).show();
			if(cl != null){
				cl.clear();
				cl.notifyDataSetChanged();
			}
		} else {
			ParseJSON pj = new ParseJSON(response);
			ArrayList<Auto> arrayList = pj.parseJSON();

			cl = new AutosList(this, arrayList);
			lvAutos.setAdapter(cl);
		}
	}

	//Deze functie wordt gebruikt om de xml file menu_auto toe tewijzen naar het menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_auto, menu);
		return true;
	}

	//Deze functie wordt aangeroepen wanneer er op een menuitem wordt gedrukt
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){

			case R.id.addauto:
				//Als er op het plusje wordt gedrukt, gebruiker doorsturen naar AddAutoActivity
				startActivityForResult(new Intent(AutoActivity.this, AddAutoActivity.class),1);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode  == 1){
			if(resultCode == Activity.RESULT_OK){
				getData(gebrid);
			} else if(resultCode == Activity.RESULT_CANCELED){}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
