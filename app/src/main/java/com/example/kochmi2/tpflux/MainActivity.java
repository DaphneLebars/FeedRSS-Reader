package com.example.kochmi2.tpflux;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kochmi2.tpflux.DataBase.FluxsDataSource;
import com.example.kochmi2.tpflux.FeedInterface.FeedData;
import com.example.kochmi2.tpflux.FeedInterface.FeedItemAdapter;
import com.example.kochmi2.tpflux.Fragments.Abonner;
import com.example.kochmi2.tpflux.Fragments.Desabonner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements Abonner.BoutonClick, Desabonner.deletedInterface {

    private ArrayList<FeedData> listDonnees;
            FeedItemAdapter feedAdapter;

    private ArrayList<String> urls;
            ListView listView;


    private FluxsDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedlist);

        //Ouvre la bd et transfere les adresses qu'elle contient dans le ArrayList<String> urls
        dataSource = new FluxsDataSource(this);
        dataSource.open();


        //1) recoit liste de string de DB et la met dans urls
        urls = new ArrayList<>();
        urls = dataSource.getData();
        dataSource.close();


        // la listView et le ArrayList listDonnees sont instanciés
        listView = (ListView)this.findViewById(R.id.feedListView);
        listDonnees = new ArrayList<>();


        //Instancie le ArrayAdapter pour traiter les donnees de listDonnees
        feedAdapter = new FeedItemAdapter(this, R.layout.feed_item,
                listDonnees);


     /* Verifie si le ArrayList contenant les URLs des feeds est vide
        après y avoir inseré les donnees de la DB.
        Si oui affiche un toast invitant a s'abonner, sinon
        télécharge les adresses contenues dans la liste*/

        if(urls.isEmpty() ){
            Toast.makeText(MainActivity.this, "Pour ajouter un feed allez dans le menu abonner", Toast.LENGTH_LONG).show();
        } else {
            urlAdownloader(urls);
        }

        listView.setAdapter(feedAdapter);


        //Gère un clique sur un item de la liste
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id ){
                //Log.i("Click:", "Un item a ete clique" );
                //Cherche la position de l'item de la liste cliqué
                FeedData donnee = listDonnees.get(position );
                String url = donnee.feedLien;
                openWebPage(url);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    String key = "key";

    //Menu: lorsqu'on appuie sur un des items ouvre un fragment
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment = null;
        FragmentTransaction ft;

        switch (item.getItemId()) {
            case R.id.abonner:
                Toast.makeText(MainActivity.this, "S'abonner", Toast.LENGTH_SHORT).show();
                fragment = new Abonner();
                FragmentManager fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.content, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case R.id.desabonner:
                Toast.makeText(MainActivity.this, "Se desabonner", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(key, urls);
                //Log.i("adresses", urls.get(0));
                fragment = new Desabonner();
                fragment.setArguments(bundle);
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.content, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }

        return false;
    }

    //Ouvre la page web lorsqu'on clique sur un élément de la ListView
    public void openWebPage(String url){
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }


    //Recoit une liste contenant les urls a telecharger
    public void urlAdownloader(ArrayList<String> urls){
        for(int i =0; i<urls.size(); i++){
            String url = urls.get(i);

            new DownloadParseXml().execute(url);
        }
    }


    protected void onResume() {
        super.onResume();
        dataSource.open();
    }


    /* Sauvegarde les nouvelles urls ajoutées à la base de données si l'app
       perd le focus, et ferme la DB. */
    protected void onPause() {
        super.onPause();

        String urlAbaseDonnee;
        ArrayList<String> urlsDB = dataSource.getData();

        for(int i=0; i < urls.size(); i++){
            if(!urlsDB.contains(urls.get(i))){
                urlAbaseDonnee = urls.get(i);
                dataSource.createFluxs(urlAbaseDonnee);
            }
        }
        dataSource.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("listUrl", urls);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        urls = savedInstanceState.getStringArrayList("listUrl");
        urlAdownloader(urls);
    }


    @Override
    public void onAttachFragment(Fragment fragment){
        if(fragment instanceof Abonner){
            Abonner fragmentAbonner = (Abonner) fragment;
            fragmentAbonner.setBtEnvoie(this);
        }

        if(fragment instanceof Desabonner){
            Desabonner fragmentDesabonner = (Desabonner) fragment;
            fragmentDesabonner.setBtnDelete(this);
        }

    }

    /* Reçoit l'url envoyé par le fragment Abonner, vérifie s'il est déja dans le ArrayList
       contenant les adresses, sinon l'y ajoute et retélécharge la liste
     */

    static String urlRecu;

    @Override
    public void setUrl(String url) {

        urlRecu = url;

       if(urls.contains(urlRecu)) {

           Toast.makeText(getApplicationContext(), "Feed RSS déja ajouté : " + urlRecu, Toast.LENGTH_SHORT).show();

       } else {

           urls.add(urlRecu);

           feedAdapter.clear();
           urlAdownloader(urls);

           feedAdapter.notifyDataSetChanged();
       }
    }

    /*Remplace la liste d'urls courante par la nouvelle après que l'utilisateur
    ai supprimer des éléments.*/

    public void afterDeleteUrls(ArrayList<String> urlsRestants){


        dataSource.open();
        dataSource.deleteFlux();

        urls = new ArrayList<>();
        urls = urlsRestants;
        for(int i=0; i < urls.size(); i++){
            dataSource.createFluxs(urls.get(i));
        }

        dataSource.close();

        feedAdapter.clear();
        urlAdownloader(urls);
        feedAdapter.notifyDataSetChanged();

    }

    /*Classe interne qui prend le flux entrant, le parse et l'envoi au ArrayAdapter*/

    class DownloadParseXml extends AsyncTask<String,Integer, ArrayList<FeedData>> {

        private RSSXMLTag currentTag;
        InputStream is = null;

        Boolean lienOk = false;

        //Ouvre la connection avec une adresse url et parse le xml recu
        //Recoit un String (l'url du feed) et retourne une ArrayList de FeedData
        @Override
        protected ArrayList<FeedData> doInBackground(String... params) {

            String urlStr = params[0];
            ArrayList<FeedData> feedDataList = new ArrayList<FeedData>();

            try {
                //Essaye d'ouvrir la connection
                is = openHttpConnection(urlStr);
                if (is != null) {
                    // Si reussi ouvre et parse les donnees du fichier xml
                    XmlPullParserFactory factory = XmlPullParserFactory
                            .newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(is, null);

                    int eventType = xpp.getEventType();
                    FeedData pdData = null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                          "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {

                        } else if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equals("item")) {
                                pdData = new FeedData();
                                currentTag = RSSXMLTag.IGNORETAG;
                            } else if (xpp.getName().equals("title")) {
                                currentTag = RSSXMLTag.TITLE;
                            } else if (xpp.getName().equals("link")) {
                                currentTag = RSSXMLTag.LINK;
                            } else if (xpp.getName().equals("pubDate")) {
                                currentTag = RSSXMLTag.DATE;
                            } else if (xpp.getName().equals("description")) {
                                currentTag = RSSXMLTag.DESCRIPTION;
                            }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            if (xpp.getName().equals("item")) {
                                // format the data here, otherwise format data in
                                // Adapter

                                Date feedDate = dateFormat.parse(pdData.feedDate);
                               pdData.feedDate = dateFormat.format(feedDate);

                                feedDataList.add(pdData);
                            } else {
                                currentTag = RSSXMLTag.IGNORETAG;
                            }
                        } else if (eventType == XmlPullParser.TEXT) {
                            String content = xpp.getText();
                            content = content.trim();
                            Log.d("debug", content);
                            if (pdData != null) {
                                switch (currentTag) {
                                    case TITLE:
                                        if (content.length() != 0) {
                                            if (pdData.feedTitre != null) {
                                                pdData.feedTitre += content;
                                            } else {
                                                pdData.feedTitre = content;
                                            }
                                        }
                                        break;
                                    case LINK:
                                        if (content.length() != 0) {
                                            if (pdData.feedLien != null) {
                                                pdData.feedLien += content;
                                            } else {
                                                pdData.feedLien = content;
                                            }
                                        }
                                        break;
                                    case DATE:
                                        if (content.length() != 0) {
                                            if (pdData.feedDate != null) {
                                                pdData.feedDate += content;
                                            } else {
                                                pdData.feedDate = content;
                                            }
                                        }
                                        break;

                                    case DESCRIPTION:
                                        if (content.length() != 0) {
                                            if (pdData.feedResume != null) {
                                                pdData.feedResume += content;
                                            } else {
                                                pdData.feedResume = content;
                                            }
                                        }
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }

                        eventType = xpp.next();
                    }

                    lienOk = true;

                }else {
                    lienOk = false;

                }

                } catch(MalformedURLException e){
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                } catch(XmlPullParserException e){
                    e.printStackTrace();
                } //catch(ParseException e){
            catch (ParseException e) {
                e.printStackTrace();
            }

            Log.i("ordre:", lienOk.toString());

                return feedDataList;
            }

        //Ajoute les info reçues et les envoies au ArrayAdapter
        @Override
        protected void onPostExecute(ArrayList<FeedData> result){

          if(lienOk) {

              if (result.size() > 0) {
                  for (int i = 0; i < result.size(); i++) {
                      listDonnees.add(result.get(i));
                  }
              }

              feedAdapter.notifyDataSetChanged();

          } else{

              Toast.makeText(getApplicationContext(), "Lien url invalide",  Toast.LENGTH_LONG).show();

              urls.remove(urls.size()-1);

          }
        }

        //Fonction pour ouvrir la connection a partir d'une adresse url
        private InputStream openHttpConnection(String urlStr) {
            InputStream in = null;
            int resCode;

            try {
                URL url = new URL(urlStr);
                URLConnection urlConn = url.openConnection();

                if (!(urlConn instanceof HttpURLConnection)) {
                    throw new IOException("URL is not an Http URL");
                }

                HttpURLConnection httpConn = (HttpURLConnection)urlConn;
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                resCode = httpConn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                } else {
                    Log.i("exception", "URL is not an http url");
                }
            } catch (IOException e) {
                // capture aussi MalformedURLException
                e.printStackTrace();
                Log.i("exception", "URL is not an http url");

            }
            return in;
        }

    }//Fin de DownloadParseXml

}//Fin de MainActivity








