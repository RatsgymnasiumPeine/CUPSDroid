package de.dlaube.cupsdroid;

import android.app.Service;
import android.content.SharedPreferences;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Printer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CupsDroidPrintService extends PrintService {
    private class CupsDroidPrinterDiscoverySession extends PrinterDiscoverySession{

        @Override
        public void onStartPrinterDiscovery(@NonNull List<PrinterId> list) {
            Log.i("cupsdroid", "onStartPrinterDiscovery");
            ArrayList<PrinterInfo> printers = new ArrayList<>();

            SharedPreferences prefs = getSharedPreferences(getString(R.string.printer_prefs_file), MODE_PRIVATE);
            int printerNum = prefs.getInt("printerNum", 0);

            for(int i = 0; i < printerNum; i++){
                if(prefs.contains("printer." + i + ".name")) {
                    PrinterInfo printer = new PrinterInfo.Builder(generatePrinterId("cupsdroid-" + i), prefs.getString("printer." + i + ".name", "Not Found"), PrinterInfo.STATUS_IDLE)
                            .setName( prefs.getString("printer." + i + ".name", "Not Found"))
                            .setDescription(prefs.getString("printer." + i + ".url", "Not Found")).build();


                    printers.add(printer);
                }
            }
            this.addPrinters(printers);
        }

        @Override
        public void onStopPrinterDiscovery() {

        }

        @Override
        public void onValidatePrinters(@NonNull List<PrinterId> list) {
            Log.i("cupsdroid", "onValidatePrinters");

        }

        @Override
        public void onStartPrinterStateTracking(@NonNull PrinterId printerId) {
            Log.i("cupsdroid", "onStartPrinterStateTracking");
        }

        @Override
        public void onStopPrinterStateTracking(@NonNull PrinterId printerId) {
            Log.i("cupsdroid", "onStopPrinterStateTracking");
        }

        @Override
        public void onDestroy() {

        }
    }


    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        return new CupsDroidPrinterDiscoverySession();
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {

    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {

    }
}
