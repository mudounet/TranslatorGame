package com.mudounet.xml.core;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Test {

	@Element
	private String question;
	
	@Element
	private String answer;

	@Element(required=false)
	private boolean reverseLogic = false;

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

    /**
     * @return the answer is in reverse logic
     */
    public boolean isReverseLogic() {
        return reverseLogic;
    }

    /**
     * @param reverseLogic Indicate if the answer is in reverse logic
     */
    public void setReverseLogic(boolean reverseLogic) {
        this.reverseLogic = reverseLogic;
    }
}
