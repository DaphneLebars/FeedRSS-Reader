package com.example.kochmi2.tpflux.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kochmi2.tpflux.R;

import java.util.ArrayList;

/**
 Affiche les adresse urls enregistrées dans une listView de checkboxes avec trois boutons:
 un pour cocher toutes les adresses, un pour décocher toutes les adresses et
 un pour supprimer les adresses cochées
 */


public class Desabonner extends Fragment {


    //ListView dans lesquelles seront les checkboxes pour chaque adresse
    private ListView lvDesabonner;

    //Arraylist comportant les checkboxes et adresses urls à envoyer à l'adapter.
    private ArrayList<DeleteUrlData> urlDataArrayList;

    //ArrayAdapter
    private DesabonnerAdapter desabonnerAdapter;

    //Boutons
    private Button btSelect, btDeselect, btSupprimer;

    //ArrayList de String comportant les adresses urls enregistrées a envoyer a l'adapter
    ArrayList<String> adressesUrl = new ArrayList<String>();

    deletedInterface callback;

     //TextView
    private TextView tvInstructions;
    Context context =(Activity) getContext();


    public void setBtnDelete(deletedInterface callback){
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.fragment_desabonner, container, false);

        lvDesabonner = (ListView)view.findViewById(R.id.desabonnerListView);
        TextView listeVide = (TextView)view.findViewById(R.id.listeVide);
        lvDesabonner.setEmptyView(listeVide);

        //Vérifie si la liste d'argument n'est pas null
        if(getArguments().getStringArrayList("key")!=null) {
            adressesUrl = getArguments().getStringArrayList("key");
        }

        btSelect = (Button) view.findViewById(R.id.bt_selectTout);
        btDeselect =(Button)view.findViewById(R.id.bt_deselectTout);
        btSupprimer = (Button)view.findViewById(R.id.bouton_supprimer);


        //Ajoute à l'ArrayList<DeleteUrlData> urlDataArrayList les adresses url
        //a afficher dans la listView du fragment desabonner, et les définit comme
        //unchecked.
        urlDataArrayList = getDeleteUrlData(false);


        //Envoie urlDataArrayList completé a l'arrayAdapteur
        desabonnerAdapter = new DesabonnerAdapter(context, urlDataArrayList);
        lvDesabonner.setAdapter(desabonnerAdapter);
        lvDesabonner.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        //Bouton qui coche tous les elements de la liste
        btSelect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                urlDataArrayList = getDeleteUrlData(true);
                desabonnerAdapter = new DesabonnerAdapter(context, urlDataArrayList );
                lvDesabonner.setAdapter(desabonnerAdapter);
            }
        });

        //Bouton qui décoche tous les elements de la liste
        btDeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlDataArrayList = getDeleteUrlData(false);
                desabonnerAdapter = new DesabonnerAdapter(context, urlDataArrayList);
                lvDesabonner.setAdapter(desabonnerAdapter);
            }
        });

        //Bouton qui supprime de la liste les elements cochés
        btSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleleData();
                callback.afterDeleteUrls(adressesUrl);
                getActivity().onBackPressed();
            }
        });

        return view;
    }


   /* Creer une arraylist d'objets de la classe DeleteUrlData
      qui est envoyé à l'adapter pour afficher les données dans la listView
   */
    private ArrayList<DeleteUrlData> getDeleteUrlData(boolean isSelect){

        ArrayList<DeleteUrlData> list = new ArrayList<>();

        for(int i=0; i<adressesUrl.size(); i++){
            DeleteUrlData deleteUrlData = new DeleteUrlData();
            deleteUrlData.setSelected((isSelect));
            deleteUrlData.setUrl(adressesUrl.get(i));
            list.add(deleteUrlData);
        }

        return list;
    }

    /*Prends l'index des urls cochés et retirent les entrées
    * correspondantes de la liste d'urls à télécharger
    * */
    public void deleleData(){

        ArrayList<Integer> aSupprimer = new ArrayList<>();

        for(int i=0; i<urlDataArrayList.size(); i++) {
            if (urlDataArrayList.get(i).getSelected()){
                aSupprimer.add(i);
            }
        }

        for(int i=0; i<aSupprimer.size(); i++ ){
            if(i==aSupprimer.get(i)) {
                urlDataArrayList.remove(i);
            }
        }

        desabonnerAdapter.notifyDataSetChanged();

        desabonnerAdapter = new DesabonnerAdapter(context, urlDataArrayList);
        lvDesabonner.setAdapter(desabonnerAdapter);

        adressesUrl = new ArrayList<>();
        for(int i=0; i<urlDataArrayList.size(); i++){
            adressesUrl.add(urlDataArrayList.get(i).getUrl());
        }
    }

    /*Interface implementee dans mainActivity pour passer
      la liste actualisée
    */
    public interface deletedInterface {
        void afterDeleteUrls(ArrayList<String> url);
    }
}
