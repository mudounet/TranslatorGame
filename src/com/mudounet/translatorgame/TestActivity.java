package com.mudounet.translatorgame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mudounet.FlowLayout;
import com.mudounet.core.AnswerFragment;
import com.mudounet.core.Lesson;
import com.mudounet.core.MalFormedSentence;
import com.mudounet.core.Sentence;
import com.mudounet.xml.stats.TestStat;

public class TestActivity extends Activity {

	Lesson lesson;
	Sentence sentence;
	Button validateButton;
	EditText proposal;
	int errorColor = Color.argb(50, 255, 0, 0);
	boolean statIsInserted = false;
	TextView question;
	float globalStatIn = 0;
	private static final Logger Logger = LoggerFactory
			.getLogger(TestActivity.class);

	// The gesture threshold expressed in dp
	private static final float GESTURE_THRESHOLD_DP = 20.0f;
	ArrayList<EditText> listOfView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources res = getResources();
		String curLangage = res.getConfiguration().locale.getCountry();

		if (!curLangage.equalsIgnoreCase("RU")) {
			Logger.error("Current langage : " + curLangage);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Don't forget to switch to russion keyboard!")
					.create().show();
		}

		setContentView(R.layout.activity_test);
		try {
			initializeTestActivity();
		} catch (Exception e) {
			Logger.error("Error while loading activity : " + e.toString());
		}
		try {
			setNewTest();
		} catch (MalFormedSentence e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Logger.debug("Trying to exit activity");
		CharSequence message;

		float curStat = lesson.getStat();
		if (lesson.getInitialStat() > curStat) {
			message = "Lesson's stat has decreased\nfrom "
					+ lesson.getInitialStat() + "% to " + curStat + "%";
		} else if (lesson.getInitialStat() < curStat) {
			message = "Lesson's stat has increased\nfrom "
					+ lesson.getInitialStat() + "% to " + curStat + "%";
		} else {
			message = "Lesson's stat has not changed\n(" + curStat + "%)";
		}

		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.show();

		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

	private void initializeTestActivity() throws Exception {
		Logger.debug("Trying to retrieve XML file");
		InputStream istr;
		istr = loadFile("lessons.xml", false);
		if (istr == null) {
			Logger.warn("Loading default file");
			Toast toast = Toast.makeText(this, "Loading default file",
					Toast.LENGTH_LONG);
			toast.show();
			istr = getAssets().open("lessons.xml");
		}

		Logger.debug("Loading Lesson on memory");
		lesson = new Lesson(istr, loadFile("lessons.stats", true));

		Logger.debug("Initialization finished");
	}

	private void setNewTest() throws MalFormedSentence {
		sentence = lesson.getNextSentence();

		statIsInserted = false;
		buildQuestion(sentence);
		buildProposal(sentence);
		buildSolution(null);
		displayStats(sentence);
	}

	private void displayStats(Sentence sentence) {
		TextView sentenceStats = (TextView) findViewById(R.id.sentence_stats);
		TextView lastFail = (TextView) findViewById(R.id.last_fail);

		TestStat stat = sentence.getStat();
		if (stat.getTotal() == 0) {
			sentenceStats.setText("N / A");
			lastFail.setText("N / A");
		} else {
			sentenceStats.setText(stat.getLastResults().mean() + "%");
			lastFail.setText(stat.getLastFailed().toLocaleString());
		}

		stat.getLastFailed();
	}

	private void buildQuestion(Sentence sentence) {
		question = (TextView) findViewById(R.id.question);
		question.setText(sentence.getTest().getQuestion());
	}

	/**
	 * Called when the user touches the button
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	public void validateProposal(View view) throws IOException, Exception {
		Logger.debug("Validating proposal");
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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

		CharSequence text;

		int result = sentence.getResults();
		float resultAsPerc = sentence.getResultAsPerc();

		if (!statIsInserted) {
			sentence.addResult(resultAsPerc);
			lesson.saveStats(writeStats("lessons.stats"));
			statIsInserted = true;
			displayStats(sentence);
		}

		if (result == 0) {
			text = "No errors found";
			setNewTest();
		} else {
			text = "Success percentage : " + resultAsPerc + " %";
			buildSolution(sentence);
		}

		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.show();

	}

	private void buildProposal(Sentence sentence) throws MalFormedSentence {
		ArrayList<AnswerFragment> arrayList = sentence.getAnswerList();
		listOfView = new ArrayList<EditText>();
		FlowLayout layout = (FlowLayout) findViewById(R.id.layout_proposal);
		layout.removeAllViews();

		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		int mGestureThreshold = 2 * (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
		Logger.debug("Height is : " + mGestureThreshold);

		int i = 0;
		EditText lastEditText = null;
		int previousMaxLength = 0;
		for (AnswerFragment fragment : arrayList) {
			if (fragment.getFragmentType() == AnswerFragment.CONSTANT_FRAGMENT) {
				TextView statText = new TextView(this);
				statText.setText(fragment.getQuestion());
				statText.setId(100 + i);
				statText.setTextSize(GESTURE_THRESHOLD_DP);
				statText.setHeight(mGestureThreshold);
				statText.setTypeface(Typeface.MONOSPACE);
				statText.setGravity(Gravity.CENTER_VERTICAL);
				statText.setPadding(0, 0, 0, 0);
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
				btnTag.setTextSize(GESTURE_THRESHOLD_DP);
				btnTag.setBackgroundResource(0);
				btnTag.setHeight(mGestureThreshold);
				btnTag.setPadding(0, 0, 0, 0);
				btnTag.setGravity(Gravity.CENTER_VERTICAL);
				btnTag.setTypeface(Typeface.MONOSPACE);
				this.listOfView.add(btnTag);
				layout.addView(btnTag);

				addListeners(lastEditText, btnTag, previousMaxLength);
				lastEditText = btnTag;
				previousMaxLength = maxLength;
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
			statText.setTextSize(GESTURE_THRESHOLD_DP);
			statText.setPadding(1, 1, 1, 1);

			if (fragment.getFragmentType() == AnswerFragment.EDITABLE_FRAGMENT
					&& fragment.getResult() > 0) {
				statText.setBackgroundColor(this.errorColor);
			}
			layout.addView(statText);
			i++;
		}
	}

	private FileInputStream loadFile(String filename, boolean createFile)
			throws IOException {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/TranslatorGame");
		dir.mkdirs();
		Logger.debug("Continue");
		File file = new File(dir, filename);
		if (file.exists() && !file.canWrite())
			throw new IOException("Cannot write to system : "
					+ file.getAbsolutePath());

		if (!file.isFile()) {
			if (createFile) {
				Logger.debug("Creating File" + filename);
				file.createNewFile(); // Exception here
			} else {
				return null;
			}
		}

		return new FileInputStream(file);
	}

	private FileOutputStream writeStats(String filename) throws IOException {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/TranslatorGame");
		dir.mkdirs();
		File file = new File(dir, filename);
		if (file.exists() && !file.canWrite())
			throw new IOException("Cannot write to system : "
					+ file.getAbsolutePath());

		if (!file.isFile()) {
			file.createNewFile(); // Exception here
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

	private void addListeners(final EditText previousEd,
			final EditText currentEd, final int previousMaxLength) {
		if (previousEd == null)
			return;

		previousEd.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (previousEd.length() == previousMaxLength) {
					currentEd.requestFocus();
				}

			}
		});

	}
}
