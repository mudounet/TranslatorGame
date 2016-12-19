package com.mudounet.translatorgame;

import android.app.ListActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SelectActivity extends ListActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = new String[] { "Lesson #1", "Lesson #2", "Lesson #3",
                "Lesson #4", "Lesson #5", "Lesson #6" };

        MainActivityAdapter adapter = new MainActivityAdapter(this, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Context context = this;
        String item = (String) getListAdapter().getItem(position);
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra("typeActivity", TypeActivity.TestActivity);
        intent.putExtra("filename", "lessons.xml");
        startActivity(intent);
    }
}
