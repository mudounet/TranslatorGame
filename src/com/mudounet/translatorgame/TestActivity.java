package com.mudounet.translatorgame;

import java.util.Locale;

import com.mudounet.translatorgame.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestActivity extends Activity {

	Button validateButton;
	EditText proposal;
	TextView answer;
	TextView question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Resources res = getResources();
        String curLangage = res.getConfiguration().locale.getCountry();
        
        if(curLangage != "RU") {
        	Log.i("test", "Current langage : " +curLangage);
        	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS), 0);
        }
		
		setContentView(R.layout.activity_test);
		addListeners();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

	private void addListeners() {
		proposal = (EditText) findViewById(R.id.proposal);
		validateButton = (Button) findViewById(R.id.validate);
		answer = (TextView) findViewById(R.id.answer);
		question = (TextView) findViewById(R.id.question);
		proposal.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				answer.setText(s.length() + " / ");
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}
}
