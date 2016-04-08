package com.kruiper.timon.v6informatica.activities.auto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kruiper.timon.v6informatica.R;
import com.kruiper.timon.v6informatica.callbacks.GetAutoCallback;
import com.kruiper.timon.v6informatica.objects.Auto;
import com.kruiper.timon.v6informatica.requests.ServerRequests;

@SuppressWarnings("unchecked")
public class ChangeAutoActivity extends AppCompatActivity implements View.OnClickListener {

	EditText etchMerk, etchType, etchKenteken;
	Spinner spchTypeBrandstof;
	ActionProcessButton bDelAuto, bChAuto;
	int autoid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_auto);

		//Terug knop op de ActionBar zetten
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Views koppelen aan xml id
		etchMerk = (EditText) findViewById(R.id.etchMerk);
		etchType = (EditText) findViewById(R.id.etchType);
		spchTypeBrandstof = (Spinner) findViewById(R.id.spchTypeBrandstof);
		etchKenteken = (EditText) findViewById(R.id.etchKenteken);
		bChAuto = (ActionProcessButton) findViewById(R.id.bChAuto);
		bDelAuto = (ActionProcessButton) findViewById(R.id.bDelAuto);

		//ActionProcessButton mode
		bChAuto.setMode(ActionProcessButton.Mode.ENDLESS);
		bDelAuto.setMode(ActionProcessButton.Mode.ENDLESS);

		//onClickListener buttons
		bChAuto.setOnClickListener(this);
		bDelAuto.setOnClickListener(this);

		//Get autoid die wordt meegestuurd met het aanmaken van deze Activity
		autoid = getIntent().getIntExtra("autoid", 0);

		//Laad gegevens van auto
		getAuto(autoid);

	}

	private void getAuto(int autoid) {
		//Er wordt een request naar de server gedaan.
		ServerRequests requests = new ServerRequests(this);

		requests.getAuto(autoid, new GetAutoCallback() {
			@Override
			public void done(Auto auto) {
				//Auto uit de database halen, als auto null is dan is er iets fout gegaan
				//anders de gegevens uit de database in de EditText zetten
				if (auto == null) {
					Toast.makeText(ChangeAutoActivity.this, "Er is iets niet goed gegaan...", Toast.LENGTH_SHORT).show();
					setResult(Activity.RESULT_CANCELED);
					finish();
				} else {
					etchMerk.setText(auto.merk);
					etchType.setText(auto.type);
					spchTypeBrandstof.setSelection(((ArrayAdapter) spchTypeBrandstof.getAdapter()).getPosition(auto.type_brandstof));
					etchKenteken.setText(auto.kenteken);
				}
			}

			@Override
			public void donemsg(String string) {}
		});
	}

	//Deze functie wordt aangeroepen wanneer een button wordt ingedrukt.
	//Met behulp van de switch/case wordt er gekeken welke button er is ingedrukt.
	@Override
	public void onClick(View v) {
		switch(v.getId()){

			case R.id.bChAuto:
				String merk = etchMerk.getText().toString();
				String type = etchType.getText().toString();
				String kenteken = etchKenteken.getText().toString();
				String typebrandstof = spchTypeBrandstof.getSelectedItem().toString();

				Auto auto = new Auto(merk,type,typebrandstof,kenteken,autoid);

				etchMerk.setEnabled(false);
				etchType.setEnabled(false);
				etchKenteken.setEnabled(false);
				spchTypeBrandstof.setEnabled(false);

				updateAuto(auto);
				break;

			case R.id.bDelAuto:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("LET OP!");
				builder.setMessage("Weet je zeker dat je de auto wilt verwijderen?");
				builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etchMerk.setEnabled(false);
						etchType.setEnabled(false);
						etchKenteken.setEnabled(false);
						spchTypeBrandstof.setEnabled(false);

						deleteAuto(autoid);
					}
				});
				builder.setNegativeButton("Nee", null);
				builder.show();
				break;
		}
	}

	private void updateAuto(Auto auto) {
		//Er wordt een request naar de server gedaan.
		ServerRequests requests = new ServerRequests(bChAuto);

		requests.updateAuto(auto, new GetAutoCallback() {
			@Override
			public void done(Auto auto) {
			}

			@Override
			public void donemsg(String s) {
				//Als de server SUCCES returned dan is de update goed gelukt en ga je weer terug naar AutoActivity
				if (s.equals("SUCCES")) {
					bChAuto.setProgress(100);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							setResult(Activity.RESULT_OK);
							finish();
						}
					}, 1000);
				} else {
					errormsg(bChAuto);
				}
			}
		});
	}

	private void deleteAuto(int autoid) {
		ServerRequests requests = new ServerRequests(bDelAuto);
		requests.delAuto(autoid, new GetAutoCallback() {
			@Override
			public void done(Auto auto) {
			}

			@Override
			public void donemsg(String s) {
				if (s.equals("SUCCES")) {
					bDelAuto.setProgress(100);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							setResult(Activity.RESULT_OK);
							finish();
						}
					}, 1000);
				} else {
					errormsg(bDelAuto);
				}
			}
		});
	}

	private void errormsg(ActionProcessButton btn) {
		btn.setProgress(-1);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				bDelAuto.setProgress(0);
				etchMerk.setEnabled(false);
				etchType.setEnabled(false);
				etchKenteken.setEnabled(false);
				spchTypeBrandstof.setEnabled(false);
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
