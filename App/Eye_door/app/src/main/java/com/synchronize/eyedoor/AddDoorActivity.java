package com.synchronize.eyedoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.synchronize.eyedoor.Models.Door;

import java.util.ArrayList;

public class AddDoorActivity extends AppCompatActivity {
    private Spinner spinnerStatus;
    private ArrayAdapter statusAdapter;
    private AppCompatButton btnAdd;
    private EditText edtIp,edtDoorName;
    private DatabaseReference dUser;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_door);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnAdd = findViewById(R.id.btnAdd);
        edtIp = findViewById(R.id.edtIp);
        edtDoorName = findViewById(R.id.edtDoorName);


        ArrayList<String> statusList = new ArrayList<>();
        statusList.add("Opened");
        statusList.add("Closed");
        statusAdapter = new ArrayAdapter(AddDoorActivity.this,android.R.layout.simple_spinner_item,statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dUser = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip=edtIp.getText().toString(),
                        doorName= edtDoorName.getText().toString();
                if(ip.equals("") || doorName.equals(""))
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                else{
                    Door item = new Door(ip,doorName,spinnerStatus.getSelectedItem().toString());
                    DatabaseReference ref = dUser.child("Doors").push();
                    String doorId = ref.getKey();
                    item.setId(doorId);
                    ref.setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                Log.i("APPDOOR1","1");
//                                FirebaseMessaging.getInstance().subscribeToTopic("hello").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Log.i("APPDOOR1","cxvcxvcvx");
//                                        if(task.isSuccessful())
//                                        {
//                                            Log.i("APPDOOR1",task.isSuccessful()+"");
//                                            Toast.makeText(getApplicationContext(),"APP"+task.getResult().toString(),Toast.LENGTH_LONG).show();
//                                        }
//                                        else {
//                                            Log.i("APPDOOR1",task.getException().getMessage());
//                                            Toast.makeText(getApplicationContext(), "APP" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                        }
//
//                                    }
//                                });
                                FirebaseMessaging.getInstance().subscribeToTopic(doorId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i("APPDOOR","cxvcxvcvx");
                                        if(task.isSuccessful())
                                        {
                                            Log.i("APPDOOR",task.isSuccessful()+"");
//                                            Toast.makeText(getApplicationContext(),"APP"+task.getResult().toString(),Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Log.i("APPDOOR",task.getException().getMessage());
                                            Toast.makeText(getApplicationContext(), "APP" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }


                                    }
                                });
                                Log.i("APPDOOR","23d");
                                Toast.makeText(getApplicationContext(), "Door added successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddDoorActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });


                }



            }
        });


    }
}