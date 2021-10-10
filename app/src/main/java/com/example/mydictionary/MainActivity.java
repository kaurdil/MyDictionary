package com.example.mydictionary;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SearchView search;
    static DatabaseHelper myDbHelper;
    static boolean DatabaseOpened=false;
    SimpleCursorAdapter suggestionAdapter;

    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        search=(SearchView)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
//                Intent intent =new Intent(MainActivity.this,WordMeaningActivity.class);
//                startActivity(intent);
            }
        });
        myDbHelper =new DatabaseHelper(this);
        if(myDbHelper.checkDataBase()){
            openDatabase();
        }else{
            LoadDatabaseAsync task=new LoadDatabaseAsync(this);
            task.execute();
        }

        final String[] from =new String[]{"en_word"};
        final int[] to=new int[]{R.id.suggestion_text};
        suggestionAdapter=new SimpleCursorAdapter(MainActivity.this,R.layout.suggestion_row,null,from,to,0){
            @Override
            public void changeCursor(Cursor cursor){
              super.swapCursor(cursor);
            }
        };
        search.setSuggestionsAdapter(suggestionAdapter);
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener(){

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter ca=search.getSuggestionsAdapter(); // get the selected word
                Cursor cursor=ca.getCursor();// get the cursor
                cursor.moveToPosition(position);// move the cursor to the position of the selcetde word
                String clicked_word=cursor.getString(cursor.getColumnIndex("en_word")); // initialize the column Index
                search.clearFocus();
                search.setFocusable(false);
                // Explicit Intent
                Intent intent=new Intent(MainActivity.this,WordMeaningActivity.class);
                Bundle bundle=new  Bundle();
                bundle.putString("en_word",clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                String text =  search.getQuery().toString();

                Cursor c = myDbHelper.getMeaning(text);
                if(c.getCount()==0)
                {
                    search.setQuery("",false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search again");

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // positive button logic
                                }
                            });

                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    search.clearFocus();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    // display dialog
                    dialog.show();
                }

                else
                {
                    //search.setQuery("",false);
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",text);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

                return false;
            }


            @Override
            public boolean onQueryTextChange(final String s) {

                search.setIconifiedByDefault(false);

                Cursor cursorSuggestion=myDbHelper.getSuggestions(s);
                android.widget.CursorAdapter simpleCursorAdapter;
                suggestionAdapter.changeCursor(cursorSuggestion);

                return false;
            }

        });
        emptyHistory = (RelativeLayout) findViewById(R.id.empty_history);

        //recycler View
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        fetch_history();

    }
    private void fetch_history() {
        historyList = new ArrayList<>();
        historyAdapter = new RecyclerViewAdapterHistory(this, historyList);
        recyclerView.setAdapter(historyAdapter);

        History h;
        if(DatabaseOpened)
        {
            cursorHistory=myDbHelper.getHistory();
            if (cursorHistory.moveToFirst()) {
                do {
                    h= new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")),cursorHistory.getString(cursorHistory.getColumnIndex("en_definition")));
                    historyList.add(h);
                }
                while (cursorHistory.moveToNext());
            }

            historyAdapter.notifyDataSetChanged();
        }


        if (historyAdapter.getItemCount() == 0)
        {
            emptyHistory.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyHistory.setVisibility(View.GONE);
        }

    }
    protected static void openDatabase(){
        try{
           myDbHelper.openDataBase();
            DatabaseOpened=true;
        }catch (SQLException e){
           e.printStackTrace();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.setting){
            Intent intent =new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.exit){
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
