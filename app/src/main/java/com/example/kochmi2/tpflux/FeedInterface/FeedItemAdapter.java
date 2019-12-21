package com.example.kochmi2.tpflux.FeedInterface;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kochmi2.tpflux.FeedInterface.FeedData;
import com.example.kochmi2.tpflux.R;

import java.util.ArrayList;

/**
 * Created by Daph on 2019-03-29.
 */
/*Classe qui implemente un ArrayAdapter pour configurer chaque cellule de la listView */

public class FeedItemAdapter extends ArrayAdapter<FeedData> {

    private Activity mContext;
    private ArrayList<FeedData> donnees;

    //Constructeur
    public FeedItemAdapter(Context context, int textViewResourceId,
                           ArrayList<FeedData> objects) {


        super(context, textViewResourceId, objects);

        mContext = (Activity) context;
        donnees = objects;
    }//Fin constructeur

    //classe qui permet d'aller chercher chaque objet de la cellule par reference au lieu
    // d'utiliser findViewById a chaque fois qu'on creer ou recycle une cellule.
    class ViewHolder {
        TextView feedTitreView;
        TextView feedDateView;
        //ImageView feedImageView;
        TextView feedResumeView;
    }


    //Fonction qui genere les View a charger dans chaque viewHolder
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        FeedData feedData = getItem(position);

        /*Cree les objets  View d'apres le layout feed_item.xml

          Verifie si une view a disparu de l'ecran et peut etre recycle
          si convertView == null alors il n'y a pas de View reutilisable
          et il faut creer un nouveau viewHolder*/

        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.feed_item, null);

            viewHolder = new ViewHolder();
           // viewHolder.feedImageView = (ImageView) convertView.findViewById(R.id.feedImage);
            viewHolder.feedTitreView = (TextView) convertView.findViewById(R.id.feedTitreLab);
            viewHolder.feedDateView = (TextView) convertView.findViewById(R.id.feedDateLab);
            viewHolder.feedResumeView = (TextView) convertView.findViewById(R.id.feedResumeLab);
            convertView.setTag(viewHolder);
        } else {
            //Sinon on reutilise le viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }



        //Charge le titre, la date et le resume dans le View
        viewHolder.feedTitreView.setText(feedData.feedTitre);

        viewHolder.feedDateView.setText(feedData.feedDate);

        viewHolder.feedResumeView.setText(feedData.feedResume);

        //Retourne le View completé pour être utilisé dans la listView
        return convertView;
    }

}