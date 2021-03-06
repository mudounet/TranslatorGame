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
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.mudounet.core.AnswerFragment;
import com.mudounet.core.Lesson;
import com.mudounet.core.MalFormedSentence;
import com.mudounet.core.Sentence;
import com.mudounet.xml.stats.TestStat;

public class TestActivity extends Activity {

    Lesson lesson;
    Sentence sentence;
    Button validateButton;
    TypeActivity typeActivity; // Category of this activity
    String fileNameWithOutExt; // Name of file to use for this activity (without extension)
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

        // These two data are sent through another activity
        typeActivity = (TypeActivity) getIntent().getExtras().get("typeActivity");
        fileNameWithOutExt = ((String)getIntent().getExtras().getString("filename")).replaceFirst("[.][^.]+$", "");

        askForPermissionIfRequired(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
        String curLanguage = ims.getLocale();

        if (!curLanguage.equalsIgnoreCase("RU")) {
            Logger.error("Current langage : " + curLanguage);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to switch to a russian keyboard to continue...")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        setContentView(R.layout.activity_test);
        try {
            initializeTestActivity();
            try {
                setNewTest();
            } catch (MalFormedSentence e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Logger.error("Error while loading activity : " + e.toString());
            Toast toast = Toast.makeText(this, "Error while loading activity : " + e.toString(),
                    Toast.LENGTH_LONG);
            toast.show();
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

    private void initializeTestActivity() throws Exception {

        if (!checkWriteExternalPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            throw new Exception("Please activate "+ Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        Logger.debug("Trying to retrieve XML file");
        InputStream istr;
        try {
            istr = loadFile(this.fileNameWithOutExt+".xml");
        }
        catch (Exception e) {
            Logger.warn("Loading default file");
            Toast toast = Toast.makeText(this, "Loading default file",
                    Toast.LENGTH_LONG);
            toast.show();
            istr = getApplicationContext().getAssets().open(this.fileNameWithOutExt+".xml");
        }

        Logger.debug("Loading Lesson on memory");
        lesson = new Lesson(istr);
        try {
            lesson.loadStats(loadFile(fileNameWithOutExt + ".stats"));
        } catch (Exception e) {
            Logger.error("Error when loading stats file");

        }

        Logger.debug("Initialization finished");
    }

    private boolean checkWriteExternalPermission(String permissionToSet) {
        // Seen at https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(TestActivity.this, permissionToSet);
        return (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void askForPermissionIfRequired(String permissionToSet) {
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;

        if (!checkWriteExternalPermission(permissionToSet)) {
            ActivityCompat.requestPermissions(TestActivity.this,
                    new String[]{permissionToSet},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
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
        FlexboxLayout layout = (FlexboxLayout) findViewById(R.id.layout_proposal);

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
            lesson.saveStats(writeFile(this.fileNameWithOutExt+".stats"));
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
        FlexboxLayout layout = (FlexboxLayout) findViewById(R.id.layout_proposal);
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
                btnTag.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

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
        FlexboxLayout layout = (FlexboxLayout) findViewById(R.id.answer);
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

    private InputStream loadFile(String filename)
            throws IOException {
        Logger.info("Trying to load file :" + filename);
        if(checkWriteExternalPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/TranslatorGame");
            dir.mkdirs();
            Logger.info("Loading file at : " + dir.getAbsolutePath());
            File file = new File(dir, filename);
            if (file.exists()) {
                return new FileInputStream(file);
            }
        }

        // When everything else has failed, we try to open it from application storage...
        return getBaseContext().openFileInput(filename);
    }

    private FileOutputStream writeFile(String filename) throws IOException {
        Logger.debug("Trying to save file :" + filename);
        if(checkWriteExternalPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

        // When everything else has failed, we try to write it from application storage...
        return getBaseContext().openFileOutput(filename, Context.MODE_PRIVATE);
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
