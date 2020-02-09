package com.example.winter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.winter.MainActivity.dref;

public class closedGroup extends AppCompatActivity {
    ArrayList<String> contactList = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    EditText name;
    EditText phone;
    ListView listView;
    Button addb;
    Button bbutton;
    Contacts user;
    Contacts user1;
    boolean is_added;
    public void setValues() {
        user.setName(name.getText().toString());
        user.setPhoneNo(phone.getText().toString());
    }
    public void back(View view){
        viewVisible();

    }
    public void viewVisible(){
        name.setVisibility(View.INVISIBLE);
        phone.setVisibility(View.INVISIBLE);
        addb.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        bbutton.setVisibility(View.INVISIBLE);
    }

    public void add(View view) {

        user = new Contacts();
        setValues();
        is_added=false;
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(addb.getWindowToken(), 0);
        arrayAdapter.clear();
//        if(name.getText().toString().equals("")|| phone.getText().toString().equals(""))
//        {
//            viewVisible();
//            return;
//        }
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!is_added) {

                        dref.push().setValue(user);
                        contactList.add(user.getName() + " : " + user.getPhoneNo());
                        contactList.set(0, "enter new contact");
                        arrayAdapter.notifyDataSetChanged();

                    is_added=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewVisible();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_group);
        Intent intent = getIntent();
        listView = findViewById(R.id.contacts);
        bbutton=findViewById(R.id.button2);
        name = findViewById(R.id.editText3);
        phone = findViewById(R.id.editText4);
        addb = findViewById(R.id.button);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList);

        contactList.clear();
        contactList.add("enter new contact");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user1 = ds.getValue(Contacts.class);
                    if (user1.getName() != null && user1.getPhoneNo() != null) {
                        contactList.add(user1.getName() + " : " + user1.getPhoneNo());
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    name.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    addb.setVisibility(View.VISIBLE);
                    name.getText().clear();
                    phone.getText().clear();
                    bbutton.setVisibility(View.VISIBLE);

                }
            }
        });

    }
}


