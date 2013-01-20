package com.mudounet.translatorgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mudounet.FlowLayout;
import com.mudounet.translatorgame.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

		if (curLangage != "RU") {
			Log.i("test", "Current langage : " + curLangage);
			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_LOCALE_SETTINGS), 0);
		}

		setContentView(R.layout.activity_test);
		addListeners();
		buildSentence();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

	private void buildSentence() {
		FlowLayout layout = (FlowLayout) findViewById(R.id.layout_proposal);
		// layout.setOrientation(LinearLayout.HORIZONTAL);
		// layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));

		List<View> list = new ArrayList<View>();
		for (int i = 1; i < 10; i++) {
			EditText btnTag = new EditText(this);
			// Button btnTag = new Button(this);
			// btnTag.setLayoutParams(new
			// LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT));
			btnTag.setText("");
			btnTag.setId(100 + i);
			btnTag.setInputType(android.text.InputType.TYPE_CLASS_TEXT
					| android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

			int maxLength = i;
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(maxLength);
			btnTag.setFilters(FilterArray);
			btnTag.setHint(generateHint(maxLength));

			// btnTag.setPadding(0, 0, 0, 0);
			layout.addView(btnTag);
		}
		// this.populateLinearLayout(layout, list);
	}

	private String generateHint(int maxNumbers) {
		String t = "";
		for (int i = 0; i < maxNumbers; i++) {
			t += "?";
		}
		return t;
	}

	private void addListeners() {
		proposal = (EditText) findViewById(R.id.proposal);
		validateButton = (Button) findViewById(R.id.validate);
		answer = (TextView) findViewById(R.id.answer);
		question = (TextView) findViewById(R.id.question);
		/*
		 * proposal.addTextChangedListener(new TextWatcher() { public void
		 * afterTextChanged(Editable s) { answer.setText(s.length() + " / "); }
		 * 
		 * public void beforeTextChanged(CharSequence s, int start, int count,
		 * int after) { }
		 * 
		 * public void onTextChanged(CharSequence s, int start, int before, int
		 * count) { Log.i("test", s.toString() + "/" + start + " / " + before);
		 * } });
		 */
		proposal.addTextChangedListener(new MaskedWatcher("(###) ###-##-##"));
	}

	private void populateLinearLayout(LinearLayout ll, List<View> collection) {

		Display display = getWindowManager().getDefaultDisplay();
		// int maxWidth = ll.getWidth();
		int maxWidth = display.getWidth();
		ll.removeAllViews();

		if (collection.size() > 0) {
			Log.i("test", "begin");
			LinearLayout llAlso = new LinearLayout(this);
			llAlso.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			llAlso.setOrientation(LinearLayout.HORIZONTAL);

			int widthSoFar = 0;
			for (View item : collection) {

				item.measure(0, 0);
				int widthOfElement = item.getMeasuredWidth();

				if (widthSoFar + widthOfElement >= maxWidth) {
					ll.addView(llAlso);
					widthSoFar = 0;

					llAlso = new LinearLayout(this);
					llAlso.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					llAlso.setOrientation(LinearLayout.HORIZONTAL);
					Log.i("test", "New line");
				}

				Log.i("test", item.toString());
				llAlso.addView(item);
				widthSoFar += widthOfElement;
			}

			ll.addView(llAlso);
		}
	}
}
