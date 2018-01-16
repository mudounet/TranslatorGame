package com.mudounet.translatorgame;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by guillaume on 16/01/2018.
 */

public class MainActivityViewHolder extends RecyclerView.ViewHolder{

    private CardView cv;
    private TextView title;
    private TextView subtitle;
    private ImageView image;

    //itemView est la vue correspondante à 1 cellule
    public MainActivityViewHolder(View itemView) {
        super(itemView);

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
}