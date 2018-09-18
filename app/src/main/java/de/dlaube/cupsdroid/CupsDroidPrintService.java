package de.dlaube.cupsdroid;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hp.jipp.encoding.IppPacket;
import com.hp.jipp.model.Operation;
import com.hp.jipp.trans.IppPacketData;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.hp.jipp.encoding.AttributeGroup.groupOf;
import static com.hp.jipp.encoding.Tag.*;
import static com.hp.jipp.model.Types.*;

public class CupsDroidPrintService extends PrintService {
    private class CupsDroidPrinterDiscoverySession extends PrinterDiscoverySession{

        @Override
        public void onStartPrinterDiscovery(@NonNull List<PrinterId> list) {
            Log.i("cupsdroid", "onStartPrinterDiscovery");
            ArrayList<PrinterInfo> printers = new ArrayList<>();

            SharedPreferences prefs = getSharedPreferences(getString(R.string._prefs_file), MODE_PRIVATE);
            int printerNum = prefs.getInt("printerNum", 0);

            for(int i = 0; i < printerNum; i++){
                if(prefs.contains("printer." + i + ".name")) {
                    PrinterInfo printer = new PrinterInfo.Builder(generatePrinterId(Integer.toString(i)), prefs.getString("printer." + i + ".name", "Not Found"), PrinterInfo.STATUS_IDLE)
                            .setName( prefs.getString("printer." + i + ".name", "Not Found"))
                            .setDescription(prefs.getString("printer." + i + ".url", "Not Found"))
                            .setCapabilities(new PrinterCapabilitiesInfo.Builder(generatePrinterId("cupsdroid-" + i))
                                    .addMediaSize(PrintAttributes.MediaSize.ISO_A4, true)
                                    .addResolution(new PrintAttributes.Resolution("300dpi", "300dpi", 300, 300), true)
                                    .setColorModes(PrintAttributes.COLOR_MODE_COLOR, PrintAttributes.COLOR_MODE_COLOR)
                                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                                    .build()).build();


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
        printJob.cancel();
    }

    @Override
    protected void onPrintJobQueued(final PrintJob printJob) {
        printJob.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            printJob.setStatus(R.string.message_reading);
            printJob.setProgress(0.2f);
        }

        SharedPreferences prefs = getSharedPreferences(getString(R.string._prefs_file), MODE_PRIVATE);
        int printerid = Integer.parseInt(printJob.getInfo().getPrinterId().getLocalId());

        String name = prefs.getString("printer." + printerid + ".name", "invalid");
        String url = prefs.getString("printer." + printerid + ".url", "invalid");
        boolean auth = prefs.getBoolean("printer." + printerid + ".auth.enabled", false);
        String username = prefs.getString("printer." + printerid + ".auth.username", "invalid");
        String password = prefs.getString("printer." + printerid + ".auth.password", "invalid");

        final HttpIppClientTransport transport = new HttpIppClientTransport();

        if(auth) {
            transport.setAuthenticationData(username, password);
        }


        final URI uri = URI.create(url);
        final IppPacket printRequest = new IppPacket(Operation.printJob, 123,
                groupOf(operationAttributes,
                        attributesCharset.of("utf-8"),
                        attributesNaturalLanguage.of("en"),
                        printerUri.of(uri),
                        requestingUserName.of("jprint"),
                        documentFormat.of("application/octet-stream")));

        final FileInputStream file = new FileInputStream(printJob.getDocument().getData().getFileDescriptor());
        final long dataLength = printJob.getDocument().getInfo().getDataSize();

        new Thread() {
            public void run() {
                try {
                    Handler h = new Handler(getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                printJob.setStatus(R.string.message_uploading);
                                printJob.setProgress(0.5f);
                            }
                        }
                    });

                    transport.sendData(uri, new IppPacketData(printRequest, file));

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                printJob.setProgress(1f);
                                printJob.complete();
                        }
                    });
                } catch (IOException e) {
                    Log.e("responseCode", "Code: " + transport.getResponseCode());
                    e.printStackTrace();
                }
            }
        }.start();




    }
}
