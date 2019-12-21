package com.example.kochmi2.tpflux.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kochmi2.tpflux.R;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Fragment avec un editText et un bouton pour que l'utilisateur rentre un lien rss
 * et que celui ci soit envoyé à MainActivity
 */

public class Abonner extends Fragment {

    EditText etAbonner;
    Button btEnvoie;
    String urlEnvoye;


    BoutonClick callback;

    public void setBtEnvoie(BoutonClick callback){
        this.callback =callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_abonner, container, false);

        etAbonner = (EditText)view.findViewById(R.id.edit_abonner);
        btEnvoie = (Button)view.findViewById(R.id.bouton_envoi);

        btEnvoie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Quand clic sur le bouton envoie le contenu du editText à MainActivity
                urlEnvoye = getUrl();
                callback.setUrl(urlEnvoye);
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    //Interface implementee dans mainActivity pour envoyer le lien entrer
    public interface BoutonClick {
        void setUrl(String url);

    }
    public String getUrl(){
        return etAbonner.getText().toString();
    }
}
