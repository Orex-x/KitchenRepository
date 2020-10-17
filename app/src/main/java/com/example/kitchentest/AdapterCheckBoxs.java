package com.example.kitchentest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdapterCheckBoxs extends BaseAdapter {
    private List<String> list;
    private LayoutInflater layoutInflater;

    public AdapterCheckBoxs(Context context, List<String> list) {
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
            view = layoutInflater.inflate(R.layout.item_duties_layout, parent, false);
        }

        CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                boolean est = false;
                for(String d : InfoAboutAcceptWork.listDutiesUndone){
                    if(d.equals(buttonView.getText().toString())){
                        est = true;
                    }
                }
                if(!est){
                    InfoAboutAcceptWork.listDutiesUndone.add(buttonView.getText().toString());
                }else InfoAboutAcceptWork.listDutiesUndone.remove(buttonView.getText().toString());
            }
        };
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(myCheckChangeList);
        String str = getStr(position);

        checkBox.setText(str);
        return view;
    }

    private String getStr(int position){
        return (String) getItem(position);
    }


//    public ArrayList<String> getBox() {
//        ArrayList<String> box = new ArrayList<String>();
//        for (String str : list) {
//            // если в корзине
//            if (str.box)
//                box.add(str);
//        }
//        return box;
//    }
}
