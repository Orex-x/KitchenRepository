package com.example.kitchentest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater layoutInflater;

    public Adapter(Context context, List<String> list) {
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.item_layout, parent, false);
        }

        String str = getStr(position);
        TextView textView = (TextView) view.findViewById(R.id.TVItemLyout);
        textView.setText(str);
        return view;
    }

    private String getStr(int position){
        return (String) getItem(position);
    }
}
