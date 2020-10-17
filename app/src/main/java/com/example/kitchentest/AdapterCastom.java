package com.example.kitchentest;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterCastom extends BaseAdapter {

        private Context context;
        public static ArrayList<ItemSetting> editModelArrayList;

        public AdapterCastom(Context context, ArrayList<ItemSetting> editModelArrayList) {

            this.context = context;
            this.editModelArrayList = editModelArrayList;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public int getCount() {
            return editModelArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return editModelArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_settings_layout, null, true);

                holder.editText = (EditText) convertView.findViewById(R.id.editTextTextPersonName2);
                holder.textView = (TextView) convertView.findViewById(R.id.textView2);

                convertView.setTag(holder);
            }else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = (ViewHolder)convertView.getTag();
            }

            holder.editText.setText(editModelArrayList.get(position).getEditTextStr());
            holder.textView.setText(editModelArrayList.get(position).getTextViewStr());

                    holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    editModelArrayList.get(position).setEditTextStr(holder.editText.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            return convertView;
        }

        private class ViewHolder {

            protected EditText editText;
            protected TextView textView;

        }

    }

