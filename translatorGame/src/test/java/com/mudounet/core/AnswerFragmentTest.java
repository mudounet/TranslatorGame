/**
 * 
 */
package com.mudounet.core;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mudounet
 *
 */
public class AnswerFragmentTest {
	
	AnswerFragment QFTested;
	
	@Before
	public void setup() {
		QFTested = new AnswerFragment();   
	}

	/**
	 * Test method for {@link com.mudounet.core.AnswerFragment#validate()}.
	 * @throws MalFormedSentence
	 */
	@Test
	public void testValidate() throws MalFormedSentence {
		testFragmentValidation("testElement", false, new String[] {"TESTElennt", "testemnet", "etlmnet"}, new int[] {2, 4, 6});
		testFragmentValidation("Познакомьтесь", false, new String[] {"познrtомьтес", "поerнакьтесь", "поerнакьтеre"}, new int[] {3, 4, 6});
		testFragmentValidation("#testElement", true, new String[] {"TESTElennt", "testemnet", "etlmnet"}, new int[] {2, 4, 6});
		testFragmentValidation("#Познакомьтесь", true, new String[] {"познrtомьтес", "поerнакьтесь", "поerнакьтеre"}, new int[] {3, 4, 6});
	}
	
	private void testFragmentValidation(String origString, boolean reverseLogic, String[] answers, int[] results) throws MalFormedSentence {
		QFTested = new AnswerFragment(origString, reverseLogic);
		assertEquals(origString.length(), QFTested.getResult());
		
		for(int idx = 0; idx < answers.length; idx++) {
			QFTested.setAnswer(answers[idx]);
			assertEquals(results[idx], QFTested.getResult());
		}
		
		QFTested.setAnswer(origString);
		assertEquals(0, QFTested.getResult());
	}

	/**
	 * Test method for {@link com.mudounet.core.AnswerFragment#getFragmentType()}.
	 * @throws Exception 
	 */
	@Test
	public void testGetFragmentType() throws MalFormedSentence {
		QFTested.setQuestion("Познакомьтесь", false);
		assertEquals(AnswerFragment.EDITABLE_FRAGMENT, QFTested.getFragmentType());
		assertEquals("Познакомьтесь", QFTested.getQuestion());

		
		QFTested.setQuestion("#Познакомьтесь", false);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("Познакомьтесь", QFTested.getQuestion());

		QFTested.setQuestion(" : ", false);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals(" : ", QFTested.getQuestion());
		
		QFTested.setQuestion(", ", false);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals(", ", QFTested.getQuestion());

		QFTested.setQuestion("! ", false);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("! ", QFTested.getQuestion());
		
		QFTested.setQuestion("?", false);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("?", QFTested.getQuestion());

		QFTested.setQuestion("#Познакомьтесь", true);
		assertEquals(AnswerFragment.EDITABLE_FRAGMENT, QFTested.getFragmentType());
		assertEquals("Познакомьтесь", QFTested.getQuestion());

		QFTested.setQuestion("Познакомьтесь", true);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("Познакомьтесь", QFTested.getQuestion());

		QFTested.setQuestion(" : ", true);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals(" : ", QFTested.getQuestion());

		QFTested.setQuestion(", ", true);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals(", ", QFTested.getQuestion());

		QFTested.setQuestion("! ", true);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("! ", QFTested.getQuestion());

		QFTested.setQuestion("?", true);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("?", QFTested.getQuestion());
	}
	
	@Test(expected=MalFormedSentence.class)
	public void testGetFragmentType1False() throws MalFormedSentence {
		QFTested.setQuestion("", false);
	}

	@Test(expected=MalFormedSentence.class)
	public void testGetFragmentType1True() throws MalFormedSentence {
		QFTested.setQuestion("", true);
	}
	
	@Test(expected=MalFormedSentence.class)
	public void testGetFragmentType2False() throws MalFormedSentence {
		QFTested.setQuestion(" Познакомьтесь  ", false);
	}

	@Test(expected=MalFormedSentence.class)
	public void testGetFragmentType2True() throws MalFormedSentence {
		QFTested.setQuestion(" Познакомьтесь  ", true);
	}
	
	@Test(expected=MalFormedSentence.class)
	public void testGetFragmentType3False() throws MalFormedSentence {
		QFTested.setQuestion(" #Познакомьтесь  ", false);
	}

	@Test(expected=MalFormedSentence.class)
	public void testGetFragmentType3True() throws MalFormedSentence {
		QFTested.setQuestion(" #Познакомьтесь  ", true);
	}
}
