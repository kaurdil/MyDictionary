package com.example.mydictionary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class SettingActivity extends AppCompatActivity {
    DatabaseHelper myDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        TextView clearHistory = findViewById(R.id.textView_clear);

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                myDbHelper = new DatabaseHelper(SettingActivity.this);
                try {
                    myDbHelper.openDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                showAlertDialog();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.setting){
            return true;
        }
        if(id==R.id.exit){
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, R.style.MyDialogTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("All the history will be deleted");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper.deleteHistory();
                    }
                });

        String negativeText = "No";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
