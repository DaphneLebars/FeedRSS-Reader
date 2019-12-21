package com.example.kochmi2.tpflux.Fragments;

/**
 * Created by Daph on 2019-04-12.
 */

/*Valu object pour chaque vue de ma listView contenant les urls pour se desabonner*/

public class DeleteUrlData {

    private boolean isSelected;
    public String url;


    public boolean getSelected(){
        return  isSelected;
    }

    public void setSelected(boolean selected){
        isSelected = selected;
    }

    /*
    J'ai l'impression de ne pas avoir besoin de celles là:
    */
    public String getUrl(){
        return url;
    }

    public  void setUrl(String url){
        this.url = url;
    }




}
