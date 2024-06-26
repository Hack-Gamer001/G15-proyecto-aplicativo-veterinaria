package com.example.bd2_veterinaria.ui.reportes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.bd2_veterinaria.R;


import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<MyDataModel> dataList;

    public CustomAdapter(Context context, List<MyDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_reporte, parent, false);
        }

        //TextView textView1 = convertView.findViewById(R.id.textView1);
        //TextView textView2 = convertView.findViewById(R.id.textView2);

        MyDataModel data = dataList.get(position);

        //textView1.setText(data.getColumn1());
        //textView2.setText(data.getColumn2());

        return convertView;
    }
}
