package com.example.winter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class closedGroup extends AppCompatActivity {
    ArrayList<String> contacts = new ArrayList<>();
    ArrayAdapter contact;
    EditText name;
    EditText phone;
    ListView listView;
    Button addb;
    public void add(View view){
        name.setVisibility(View.INVISIBLE);
        phone.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        addb.setVisibility(View.INVISIBLE);
        contacts.add(name.getText()+" : "+phone.getText());
        contact.notifyDataSetChanged();
        InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(addb.getWindowToken(),0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_group);
        listView=findViewById(R.id.contacts);
        name=findViewById(R.id.editText3);
        phone=findViewById(R.id.editText4);
        addb=findViewById(R.id.button);
        contact=new ArrayAdapter(this ,android.R.layout.simple_list_item_1,contacts);
        listView.setAdapter(contact);
        contacts.add("enter new contact");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    name.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    addb.setVisibility(View.VISIBLE);
                    name.getText().clear();
                    phone.getText().clear();

                }
            }
        });

    }
}
