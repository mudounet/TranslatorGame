package com.mudounet.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerFragment {

    public static final int CONSTANT_FRAGMENT = 2;
    public static final int EDITABLE_FRAGMENT = 1;
    private static final Logger Logger = LoggerFactory
            .getLogger(AnswerFragment.class);

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
    private String answer;
    private int fragmentType;
    private String question;
    private int levenshteinResult = -1;
    private int qtyOfEditableFields = -1;

    public AnswerFragment() {
    }

    public AnswerFragment(String text, boolean reverseLogic) throws MalFormedSentence {
        setQuestion(text, reverseLogic);
        setAnswer("");
    }

    private int computeLevenshteinDistance() {
        if (fragmentType == CONSTANT_FRAGMENT) {
            return 0;
        }

        CharSequence str1 = this.question.toLowerCase();
        CharSequence str2 = this.answer.toLowerCase();
        Logger.debug("Compute \"" + str1 + "\" with \"" + str2 + "\"");

        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 1; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1]
                        + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                        : 1));
            }
        }

        return distance[str1.length()][str2.length()];
    }

    public String getAnswer() {
        return answer;
    }

    /**
     * @return the fragmentType
     */
    public int getFragmentType() {
        return fragmentType;
    }

    /**
     * @return the text
     */
    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answerText) {
        if (this.fragmentType == EDITABLE_FRAGMENT) {
            this.answer = answerText;
            this.levenshteinResult = -1;
        } else {
            this.answer = this.question;
            this.levenshteinResult = 0;
        }
    }

    /**
     * @param text the text to set
     * @throws Exception
     */
    public void setQuestion(String text, boolean reverseLogic) throws MalFormedSentence {

        this.fragmentType = 0;
        Pattern pattern = Pattern.compile("^#([\\p{L}\\p{M}]+)$"); // Unicode character classes are described here : http://www.regular-expressions.info/unicode.html#prop
        Matcher m = pattern.matcher(text);

        if (text.matches("^[\\p{L}\\p{M}]+$")) { // Unicode character classes are described here : http://www.regular-expressions.info/unicode.html#prop
            this.fragmentType = (reverseLogic) ? CONSTANT_FRAGMENT : EDITABLE_FRAGMENT;
            this.question = text;
            setAnswer("");
        } else if (m.matches()) {
            this.fragmentType = (reverseLogic) ? EDITABLE_FRAGMENT : CONSTANT_FRAGMENT;
            this.question = m.group(1);
        } else if (text.matches("^\\P{L}+$")) {
            this.fragmentType = CONSTANT_FRAGMENT;
            this.question = text;
        } else {
            Logger.error("\"" + text + "\" is not valid!");
            throw new MalFormedSentence("\"" + text + "\" is not valid!");
        }

        if(this.fragmentType == EDITABLE_FRAGMENT) {
            this.question = this.question.replaceAll("\\p{M}", "");
            setAnswer("");
        }
        else {
            setAnswer(this.question);
        }
    }

    public int getResult() {
    	if(this.levenshteinResult == -1)
    		this.levenshteinResult = computeLevenshteinDistance();
        return this.levenshteinResult;
    }
}
