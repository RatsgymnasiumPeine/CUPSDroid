package de.dlaube.cupsdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PrinterManagementActivity extends AppCompatActivity {

    public ArrayList<CupsDroidPrinter> getPrinters(){
        ArrayList<CupsDroidPrinter> printers = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences(getString(R.string._prefs_file), MODE_PRIVATE);
        int printerNum = prefs.getInt("printerNum", 0);

        for(int i = 0; i < printerNum; i++){
            if(prefs.contains("printer." + i + ".name")) {
                CupsDroidPrinter printer = new CupsDroidPrinter(
                        i,
                        prefs.getString("printer." + i + ".name", "Not Found"),
                        prefs.getString("printer." + i + ".url", "Not Found")
                );

                printers.add(printer);
            }
        }

        return printers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Activity self = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(self, AddPrinterActivity.class));
            }
        });
    }
    public void refreshList(){
        final ListView list = findViewById(R.id.printer_list);
        list.setAdapter(new PrinterListAdapter(this, getPrinters()));
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int printerNum = (int) view.getTag();

                SharedPreferences prefs =  getSharedPreferences(getString(R.string._prefs_file), MODE_PRIVATE);

                SharedPreferences.Editor edit = prefs.edit();
                edit.remove("printer." + printerNum + ".name");
                edit.remove("printer." + printerNum + ".url");
                edit.apply();

                ((PrinterListAdapter)list.getAdapter()).setData(getPrinters());
                return true;
            }
        });
    }
    @Override
    public void onResume() {
        final Activity self = this;
        this.refreshList();


        super.onResume();
    }
}
