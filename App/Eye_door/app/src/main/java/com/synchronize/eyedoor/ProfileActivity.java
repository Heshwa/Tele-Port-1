package com.synchronize.eyedoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private AppCompatButton btnSave;
    private EditText edtUserName,edtAddress,edtEmergency;
    private DatabaseReference dUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnSave = findViewById(R.id.btnSave);
        edtUserName = findViewById(R.id.edtUserName);
        edtAddress = findViewById(R.id.edtAdress);
        edtEmergency = findViewById(R.id.edtEmergency);

        dUsers = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=edtUserName.getText().toString(),
                        emergency=edtEmergency.getText().toString(),
                        address = edtAddress.getText().toString();
                dUsers.child("Name").setValue(username);
                dUsers.child("Emergency").setValue(emergency);
                dUsers.child("Address").setValue(address);
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);

            }
        });

        dUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String userName="",emergency ="",address="";
                    if(snapshot.hasChild("Name"))
                        userName = snapshot.child("Name").getValue().toString();
                    if(snapshot.hasChild("Emergency"))
                        emergency = snapshot.child("Emergency").getValue().toString();
                    if(snapshot.hasChild("Address"))
                        address = snapshot.child("Address").getValue().toString();

                    edtUserName.setText(userName);
                    edtAddress.setText(address);
                    edtEmergency.setText(emergency);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}