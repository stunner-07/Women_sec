package com.example.winter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements AccelerometerListener {
    Button policeSiren;
    MediaPlayer mp;
    Button dial;
    SmsManager smsManager;
    static FirebaseDatabase database;
    static DatabaseReference dref;
    int g=0;
    public void sendSMS(View view) {

                dref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String phoneNumber = ds.child("phoneNo").getValue(String.class);
                            smsManager.sendTextMessage(phoneNumber, null, "Alert!!! Help ME", null, null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });

        }



        public void closedGroup(View view)
    {
        Intent intent = new Intent(getApplicationContext(), closedGroup.class);
        startActivity(intent);
    }
//    public void closedGroup(View v)
//        {
//            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//            startActivityForResult(intent, PICK_CONTACT);
//        }
//    @Override public void onActivityResult(int reqCode, int resultCode, Intent data){ super.onActivityResult(reqCode, resultCode, data);
//
//        switch(reqCode)
//        {
//            case (1):
//                if (resultCode == Activity.RESULT_OK)
//                {
//                    Uri contactData = data.getData();
//                    Cursor c = managedQuery(contactData, null, null, null, null);
//                    if (c.moveToFirst())
//                    {
//                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//
//                        String hasPhone =
//                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
//                        if (hasPhone.equalsIgnoreCase("1"))
//                        {
//                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
//                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
//                            phones.moveToFirst();
//                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            // Toast.makeText(getApplicationContext(), cNumber, Toast.LENGTH_SHORT).show();
//                            setCn(cNumber);
//                        }
//                    }
//                }
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);

        }
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force) {
        //Toast.makeText(this, "Motion detected", Toast.LENGTH_SHORT).show();
        if (g == 0) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.police);

        mp.start();
        mp.setLooping(true);
        g = 1;
        //setWakeLock(wakeLock);
    }
    }

    @Override
    public void onStop() {
        super.onStop();
        //wakeLock.acquire();
        //Check device supported Accelerometer senssor or not
        //if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            //AccelerometerManager.stopListening();


            //Toast.makeText(this, "onStop Accelerometer Stopped", Toast.LENGTH_SHORT).show();
       // }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();

            //Toast.makeText(this, "onDestroy Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void siren(View view){
        if(g==0){
        mp = MediaPlayer.create(getApplicationContext(), R.raw.police);
        mp.start();
        mp.setLooping(true);
        g=1;}
        else {
            mp.stop();
            g=0;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        policeSiren=findViewById(R.id.mp3);
        smsManager = SmsManager.getDefault();
        dial=findViewById(R.id.dial100);
        database = FirebaseDatabase.getInstance();
        dref = database.getReference().child("Contacts");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
    }

}
