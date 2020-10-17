package com.example.kitchentest;

import android.widget.EditText;
import android.widget.TextView;

public class ItemSetting {
   private String textViewStr, editTextStr;

    public ItemSetting(String textViewStr, String editTextStr) {
        this.textViewStr = textViewStr;
        this.editTextStr = editTextStr;
    }

    public String getTextViewStr() {
        return textViewStr;
    }

    public void setTextViewStr(String textViewStr) {
        this.textViewStr = textViewStr;
    }

    public String getEditTextStr() {
        return editTextStr;
    }

    public void setEditTextStr(String editTextStr) {
        this.editTextStr = editTextStr;
    }


//    private String textViewStr;
//    private EditText editTextStr;
//
//    public ItemSetting(String textViewStr, EditText editTextStr) {
//        this.textViewStr = textViewStr;
//        this.editTextStr = editTextStr;
//    }
//
//    public String getTextViewStr() {
//        return textViewStr;
//    }
//
//    public void setTextViewStr(String textViewStr) {
//        this.textViewStr = textViewStr;
//    }
//
//    public EditText getEditTextStr() {
//        return editTextStr;
//    }
//
//    public void setEditTextStr(EditText editTextStr) {
//        this.editTextStr = editTextStr;
//    }
}
