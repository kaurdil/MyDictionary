package com.example.mydictionary;

import android.app.AlertDialog;
import android.content.Context;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import java.io.IOException;

public class LoadDatabaseAsync extends AsyncTask<Void,Void,Boolean> {
    private Context context;
    private AlertDialog alertDialog;
    private DatabaseHelper myDbHelper;

    public LoadDatabaseAsync(Context context){
        this.context=context;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        alertDialog.dismiss();
        MainActivity.openDatabase();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        myDbHelper=new DatabaseHelper(context);
        try{
          myDbHelper.createDataBase();
        }catch(IOException e){

         throw new Error("database was not created");
        }
        myDbHelper.close();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder d =new AlertDialog.Builder(context,R.style.MyDialogTheme);
        LayoutInflater inflater=LayoutInflater.from(context);
        View dialogView=inflater.inflate(R.layout.alert_dialog_database_copying,null);
        d.setTitle("Loading database...");
        d.setView(dialogView);
        alertDialog =d.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
