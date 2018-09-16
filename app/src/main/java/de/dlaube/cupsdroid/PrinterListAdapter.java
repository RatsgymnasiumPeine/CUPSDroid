package de.dlaube.cupsdroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PrinterListAdapter extends ArrayAdapter<CupsDroidPrinter> {
    public PrinterListAdapter(@NonNull Context context, ArrayList<CupsDroidPrinter> values) {
        super(context, 0, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CupsDroidPrinter printer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_printer, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.printerName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.printerUrl);
        // Populate the data into the template view using the data object
        tvName.setText(printer.getName());
        tvHome.setText(printer.getUrl());
        // Return the completed view to render on screen

        convertView.setTag(printer.getId());
        return convertView;
    }

    public void setData(ArrayList<CupsDroidPrinter> values){
        this.clear();
        this.addAll(values);
        this.notifyDataSetChanged();
    }
}
