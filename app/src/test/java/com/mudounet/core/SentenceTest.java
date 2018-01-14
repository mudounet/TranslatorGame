package com.mudounet.core;

import com.mudounet.xml.stats.TestStat;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SentenceTest {

    @Before
    public void setup() {
    }

    /**
     * Test method for {@link com.mudounet.core.Question#getAnswerList()}.
     *
     * @throws Exception
     */
    @Test
    public void testGetAnswerList() throws Exception {
        testGetAnswerListValidation("Как по-французски \"конечно\"?", new String[]{"Как", " ", "по", "-", "французски", " \"", "конечно", "\"?"});
        testGetAnswerListValidation("#Вы замужем? - #Да, замужем.", new String[]{"Вы", " ", "замужем", "? - ", "Да", ", ", "замужем", "."});
        testGetAnswerListValidation("Из-за #праздников все", new String[]{"Из", "-", "за", " ", "праздников", " ", "все"});
        testGetAnswerListValidation("Из-за #пра́здников все", new String[]{"Из", "-", "за", " ", "пра́здников", " ", "все"}); // This sentence include some pronunciation marks.
        testGetAnswerListValidation("Из-за пра́здников все", new String[]{"Из", "-", "за", " ", "праздников", " ", "все"}); // This sentence include some pronunciation marks.
    }

    @Test
    public void testGetResults() throws Exception {
        testgetResultsValidation("Как по-французски \"конечно\"?", new String[]{"Как", " ", "по", "-", "французски", " \"", "конечно", "\"?"}, 0);
        testgetResultsValidation("Как по-французски \"конечно\"?", new String[]{"", " ", "по", "-", "французски", " \"", "коно", "\"?"}, 6);
        testgetResultsValidation("Как по-французски \"конечно\"?", new String[]{"Как", " ", "п", "-", "францзски", " \"", "кнено", "\"?"}, 4);
        testgetResultsValidation("Как по-французски \"конечно\"?", new String[]{"Кsz", " ", "пc", "-", "фрabcузски", " \"", "конеzно", "\"?"}, 7);
        testgetResultsValidation("#Вы замужем? - #Да, замужем.", new String[]{"В", " ", "заем", "? - ", "Да", ", ", "", "."}, 10);
    }

    private void testGetAnswerListValidation(String expectedAnswer, String[] answers) throws Exception {
        com.mudounet.xml.core.Test t = new com.mudounet.xml.core.Test();
        t.setAnswer(expectedAnswer);

        Sentence instance = new Sentence(t, null);
        ArrayList<AnswerFragment> elements = instance.getAnswerList();

        for (int idx = 0; idx < answers.length; idx++) {
            assertEquals(answers[idx], elements.get(idx).getQuestion());
        }
    }

    private void testgetResultsValidation(String expectedAnswer, String[] answers, int expResult) throws Exception {
        com.mudounet.xml.core.Test t = new com.mudounet.xml.core.Test();
        t.setAnswer(expectedAnswer);

        Sentence instance = new Sentence(t, null);
        ArrayList<AnswerFragment> elements = instance.getAnswerList();

        for (int idx = 0; idx < answers.length; idx++) {
            elements.get(idx).setAnswer(answers[idx]);
        }
        assertEquals(expResult, instance.getResults());
    }

    /**
     * Test of getStat method, of class Sentence.
     */
    @Test
    public void testGetStat() throws MalFormedSentence {
        System.out.println("TEST : getStat");
        TestStat expResult = new TestStat();
        Sentence instance = new Sentence(null, expResult);

        TestStat result = instance.getStat();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStat method, of class Sentence.
     */
    @Test
    public void testSetStat() throws MalFormedSentence {
        System.out.println("TEST : setStat");
        TestStat expResult = new TestStat();
        Sentence instance = new Sentence(null, expResult);

        TestStat result = instance.getStat();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTest method, of class Sentence.
     */
    @Test
    public void testSetTest() throws Exception {
        System.out.println("setTest");
        com.mudounet.xml.core.Test test = new com.mudounet.xml.core.Test();
        test.setAnswer("Как по-французски \"конечно\"?");

        Sentence instance = new Sentence(test, null);
        assertEquals(test, instance.getTest());
    }

    /**
     * Test of getTest method, of class Sentence.
     */
    @Test
    public void testGetTest() throws MalFormedSentence {
        System.out.println("setTest");
        com.mudounet.xml.core.Test test = new com.mudounet.xml.core.Test();
        test.setAnswer("Как по-французски \"конечно\"?");

        Sentence instance = new Sentence(test, null);
        assertEquals(test, instance.getTest());
    }

    /**
     * Test of addResult method, of class Sentence.
     */
    @Test
    public void testAddResult() throws MalFormedSentence {
        System.out.println("addResult");
        TestStat stat = new TestStat();
        Sentence instance = new Sentence(null, new TestStat());

        assertEquals(0, instance.getStat().mean(), 0.0001f);
        instance.addResult(100);
        assertEquals(100, instance.getStat().mean(), 0.0001f);
        instance.addResult(0);
        assertEquals(50, instance.getStat().mean(), 0.0001f);
        
    }
}
