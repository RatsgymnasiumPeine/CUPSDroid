package de.dlaube.cupsdroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddPrinterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_printer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Activity act = this;

        Button saveButton = findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs =  act.getSharedPreferences(getString(R.string.printer_prefs_file), MODE_PRIVATE);
                int printerNum = prefs.getInt("printerNum", 0);

                String name = ((EditText)findViewById(R.id.printer_name_input)).getText().toString();
                String url = ((EditText)findViewById(R.id.printer_url_input)).getText().toString();

                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("printer." + printerNum + ".name", name);
                edit.putString("printer." + printerNum + ".url", url);
                edit.putInt("printerNum", printerNum+1);
                edit.apply();

                act.onBackPressed();
            }
        });
    }
}
