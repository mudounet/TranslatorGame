package com.mudounet.translatorgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivityFiller {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("lessons.xml");
        cricket.add("Declensions.xml");

        List<String> football = new ArrayList<String>();
        football.add("lessons.xml");
        football.add("Declensions.xml");

        expandableListDetail.put("Group #1", cricket);
        expandableListDetail.put("Group #2", football);
        return expandableListDetail;
    }
}