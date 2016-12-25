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
		testFragmentValidation("testElement", 11, false, new String[] {"TESTElennt", "testemnet", "etlmnet", "testElement"}, new int[] {2, 4, 6, 0});
		testFragmentValidation("Познакомьтесь", 13, false, new String[] {"познrtомьтес", "поerнакьтесь", "поerнакьтеre", "Познакомьтесь"}, new int[] {3, 4, 6, 0});
		testFragmentValidation("#testElement", 11, true, new String[] {"TESTElennt", "testemnet", "etlmnet", "testElement"}, new int[] {2, 4, 6, 0});
		testFragmentValidation("#Познакомьтесь", 13, true, new String[] {"познrtомьтес", "поerнакьтесь", "поerнакьтеre", "Познакомьтесь"}, new int[] {3, 4, 6, 0});
	}
	
	private void testFragmentValidation(String origString, int expStrLength, boolean reverseLogic, String[] answers, int[] results) throws MalFormedSentence {
		QFTested = new AnswerFragment(origString, reverseLogic);
		assertEquals(expStrLength, QFTested.getResult());
		
		for(int idx = 0; idx < answers.length; idx++) {
			QFTested.setAnswer(answers[idx]);
			assertEquals(results[idx], QFTested.getResult());
		}
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

		QFTested.setQuestion("#пра́здников", false);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("пра́здников", QFTested.getQuestion());

		QFTested.setQuestion("пра́здников", true);
		assertEquals(AnswerFragment.CONSTANT_FRAGMENT, QFTested.getFragmentType());
		assertEquals("пра́здников", QFTested.getQuestion());

		QFTested.setQuestion("пра́здников", false);
		assertEquals(AnswerFragment.EDITABLE_FRAGMENT, QFTested.getFragmentType());
		assertEquals("праздников", QFTested.getQuestion());

		QFTested.setQuestion("#пра́здников", true);
		assertEquals(AnswerFragment.EDITABLE_FRAGMENT, QFTested.getFragmentType());
		assertEquals("праздников", QFTested.getQuestion());
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
