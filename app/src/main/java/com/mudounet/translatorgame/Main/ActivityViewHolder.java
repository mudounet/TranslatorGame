package com.mudounet.translatorgame.Main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mudounet.translatorgame.R;
import com.mudounet.translatorgame.TestActivity;
import com.mudounet.translatorgame.TypeActivity;

/**
 * Created by guillaume on 16/01/2018.
 */

public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private CardView cv;
    private TextView title;
    private TextView subtitle;
    private ImageView image;
    private final Context context;

    //itemView est la vue correspondante à 1 cellule
    public ActivityViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();
        itemView.setOnClickListener(this);
        //c'est ici que l'on fait nos findView
        cv = (CardView)itemView.findViewById(R.id.cv);
        title = (TextView) itemView.findViewById(R.id.title);
        subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    public void bind(MyObject myObject){
        title.setText(myObject.getTitle());
        subtitle.setText(myObject.getSubtitle());
        //image.setImageResource(R.drawable.ic_menu_slideshow);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra("typeActivity", TypeActivity.TestActivity);
        intent.putExtra("filename", subtitle.getText());
        context.startActivity(intent);
    }
}