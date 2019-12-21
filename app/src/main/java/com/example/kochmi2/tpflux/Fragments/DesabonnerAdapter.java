package com.example.kochmi2.tpflux.Fragments;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.kochmi2.tpflux.R;

import java.util.ArrayList;


/*Gere les differents elements, composer d'un checkbox et d'un
  String, qui vont dans la ListView du fragment abonner */


public class DesabonnerAdapter extends BaseAdapter
{

    private Context context;
    public static ArrayList<DeleteUrlData> deleteUrlArrayList;


    public DesabonnerAdapter(Context context, ArrayList<DeleteUrlData> objects){
        this.context = context;
        this.deleteUrlArrayList = objects;
    }

    @Override
    public int getViewTypeCount(){

        return 1;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @Override
    public int getCount() {
        if(deleteUrlArrayList.size()>0) {
            return deleteUrlArrayList.size();
        }
        else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return deleteUrlArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;


        if(convertView == null){
            viewHolder = new ViewHolder();
            context = parent.getContext();
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.desabonner_item, null, true);

            viewHolder.checkBox =(CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkBox.setText(deleteUrlArrayList.get(position).getUrl());

        viewHolder.checkBox.setChecked(deleteUrlArrayList.get(position).getSelected());

        viewHolder.checkBox.setTag(R.integer.plusView, convertView);
        viewHolder.checkBox.setTag(position);


        //Gere un clique sur le checkbox:
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) viewHolder.checkBox.getTag();

                if(deleteUrlArrayList.get(pos).getSelected()){
                    deleteUrlArrayList.get(pos).setSelected(false);

                } else {
                    deleteUrlArrayList.get(pos).setSelected(true);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder{
        protected CheckBox checkBox;
    }

}
