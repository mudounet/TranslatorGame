package com.mudounet.translatorgame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mudounet.FlowLayout;
import com.mudounet.core.AnswerFragment;
import com.mudounet.core.Lesson;
import com.mudounet.core.MalFormedSentence;
import com.mudounet.core.Sentence;

public class TestActivity extends Activity {

	Lesson lesson;
	Sentence sentence;
	Button validateButton;
	EditText proposal;
	int errorColor = Color.argb(50, 255, 0, 0);
	TextView question;
	private static final Logger Logger = LoggerFactory
			.getLogger(TestActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources res = getResources();
		String curLangage = res.getConfiguration().locale.getCountry();

		if (!curLangage.equalsIgnoreCase("RU")) {
			Logger.error("Current langage : " + curLangage);

			Toast toast = Toast.makeText(this,
					"Incorrect langage for this activity", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			setContentView(R.layout.activity_test);
			addListeners();
			try {
				initializeTestActivity();
			} catch (Exception e) {
				Logger.error("Error while loading activity : " + e.toString());
			}
			try {
				setNewTest();
			} catch (MalFormedSentence e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

	private void initializeTestActivity() throws Exception {
		Logger.debug("Trying to retrieve XML file");
		InputStream istr = getAssets().open("lessons.xml");

		Logger.debug("Loading file onto memory");
		lesson = new Lesson(istr);

		Logger.debug("Loading stats file");
		lesson.loadStats(loadStats("lessons.stats"));

		Logger.debug("Writing stats file");
		FileOutputStream outputStream = writeStats("lessons.stats");
		lesson.saveStats(outputStream);

		Logger.debug("Initialization finished");
	}

	private void setNewTest() throws MalFormedSentence {
		sentence = lesson.getNextSentence();

		buildQuestion(sentence);
		buildProposal(sentence);
		buildSolution(null);

	}

	private void buildQuestion(Sentence sentence) {
		question = (TextView) findViewById(R.id.question);
		question.setText(sentence.getTest().getQuestion());
	}

	/**
	 * Called when the user touches the button
	 * 
	 * @throws MalFormedSentence
	 */
	public void validateProposal(View view) throws MalFormedSentence {
		Logger.debug("Validating proposal");
		ArrayList<AnswerFragment> arrayList = sentence.getAnswerList();
		FlowLayout layout = (FlowLayout) findViewById(R.id.layout_proposal);

		int fragIdx = 0;
		for (AnswerFragment fragment : arrayList) {
			if (fragment.getFragmentType() == AnswerFragment.EDITABLE_FRAGMENT) {
				String enteredText = ((EditText) layout.getChildAt(fragIdx))
						.getText().toString();
				fragment.setAnswer(enteredText);
			}
			fragIdx++;
		}

		// Context context = getApplicationContext();
		CharSequence text;

		int result = sentence.getResults();
		if (result == 0) {
			text = "No errors found";
			setNewTest();
		} else {
			text = "You made " + result + " errors";
			buildSolution(sentence);
		}

		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	private void buildProposal(Sentence sentence) throws MalFormedSentence {
		ArrayList<AnswerFragment> arrayList = sentence.getAnswerList();
		FlowLayout layout = (FlowLayout) findViewById(R.id.layout_proposal);
		layout.removeAllViews();

		int i = 0;
		for (AnswerFragment fragment : arrayList) {
			if (fragment.getFragmentType() == AnswerFragment.CONSTANT_FRAGMENT) {
				TextView statText = new TextView(this);
				statText.setText(fragment.getQuestion());
				statText.setId(100 + i);
				statText.setTypeface(Typeface.DEFAULT);
				layout.addView(statText);
			} else {
				EditText btnTag = new EditText(this);
				btnTag.setText("");
				btnTag.setId(100 + i);
				btnTag.setInputType(android.text.InputType.TYPE_CLASS_TEXT
						| android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

				int maxLength = fragment.getQuestion().length();
				InputFilter[] FilterArray = new InputFilter[1];
				FilterArray[0] = new InputFilter.LengthFilter(maxLength);
				btnTag.setFilters(FilterArray);
				btnTag.setHint(generateHint(maxLength));
				btnTag.setTypeface(Typeface.DEFAULT);

				layout.addView(btnTag);
			}
			i++;
		}
	}

	private void buildSolution(Sentence sentence) throws MalFormedSentence {
		FlowLayout layout = (FlowLayout) findViewById(R.id.answer);
		layout.removeAllViews();

		if (sentence == null)
			return;

		ArrayList<AnswerFragment> arrayList = sentence.getAnswerList();
		int i = 0;
		for (AnswerFragment fragment : arrayList) {
			TextView statText = new TextView(this);
			statText.setText(fragment.getQuestion());
			statText.setId(2000 + i);
			statText.setTypeface(Typeface.DEFAULT);
			// statText.setHeight(70);
			statText.setPadding(1, 1, 1, 1);

			if (fragment.getFragmentType() == AnswerFragment.EDITABLE_FRAGMENT
					&& fragment.getResult() > 0) {
				statText.setBackgroundColor(this.errorColor);
			}
			layout.addView(statText);
			i++;
		}
	}

	private FileInputStream loadStats(String filename) throws IOException {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/TranslatorGame");
		dir.mkdirs();
		Logger.debug("Continue");
		File file = new File(dir, filename);
		if (file.exists() && !file.canWrite())
			throw new IOException("Cannot write to system : "
					+ file.getAbsolutePath());

		if (!file.isFile()) {
			Logger.debug("Continue 1");
			file.createNewFile(); // Exception here
			Logger.debug("Continue 2");
		}

		return new FileInputStream(file);
	}

	private FileOutputStream writeStats(String filename) throws IOException {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/TranslatorGame");
		dir.mkdirs();
		Logger.debug("Continue");
		File file = new File(dir, filename);
		if (file.exists() && !file.canWrite())
			throw new IOException("Cannot write to system : "
					+ file.getAbsolutePath());

		if (!file.isFile()) {
			Logger.debug("Continue 1");
			file.createNewFile(); // Exception here
			Logger.debug("Continue 2");
		}

		return new FileOutputStream(file);
	}

	private String generateHint(int maxNumbers) {
		String t = "";
		for (int i = 0; i < maxNumbers; i++) {
			t += "?";
		}
		return t;
	}

	private void addListeners() {
		// proposal = (EditText) findViewById(R.id.proposal);
		validateButton = (Button) findViewById(R.id.validate);
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
	}
}
