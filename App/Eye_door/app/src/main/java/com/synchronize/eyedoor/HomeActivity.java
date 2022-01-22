package com.synchronize.eyedoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.synchronize.eyedoor.Adapters.HistoryAdapter;
import com.synchronize.eyedoor.Models.Door;
import com.synchronize.eyedoor.Models.History;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<History> historyArrayList;
    private HistoryAdapter historyAdapter;
    private ImageView imgAddDoor;
    private Spinner spinnerDoor;
    private ArrayAdapter doorAdapter;
    private ArrayList<String> doorLists;
    private DatabaseReference mDoors,mUsers;
    private FirebaseUser fUsers;
    private ArrayList<String> doorIdLists;
    public static String DOORIP="",DOORID="",IMAGEURL="";
    private Toolbar mToolbar;
    private AppCompatButton btnSos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        historyArrayList = new ArrayList<>();
        historyAdapter= new HistoryAdapter(getApplicationContext(),historyArrayList);
        recyclerView.setAdapter(historyAdapter);
        imgAddDoor = findViewById(R.id.imgAddDoor);
        spinnerDoor = findViewById(R.id.spinnerDoors);
        mToolbar = findViewById(R.id.toolBar);
        btnSos = findViewById(R.id.btn_sos);

        doorLists = new ArrayList<>();
        doorIdLists = new ArrayList<>();
        doorAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,doorLists);
        doorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoor.setAdapter(doorAdapter);
        fUsers = FirebaseAuth.getInstance().getCurrentUser();
        mUsers = FirebaseDatabase.getInstance().getReference("Users").child(fUsers.getUid());
        mDoors = FirebaseDatabase.getInstance().getReference("Users").child(fUsers.getUid())
                .child("Doors");
        mToolbar.inflateMenu(R.menu.home_menu);
        btnSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            if(userName.equals("") || emergency.equals("") || address.equals(""))
                            {
                                Toast.makeText(getApplicationContext(),"Please update your profile , in order to activate SOS feature",Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else
                            {
                                String subject="Hi, "+userName+"’s home is on theft alert.\n" +
                                        "\n" +
                                        "Check it out soon at \n" +
                                        address;
                                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                smsIntent.setData(Uri.parse("sms:"));
                                smsIntent.putExtra("address", emergency);
                                smsIntent.putExtra("sms_body",subject);
                                startActivity(smsIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.itemLogout)
                {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(),SignInHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId()==R.id.itemProfile)
                {
                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        mDoors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doorLists.clear();
                doorIdLists.clear();
                if(snapshot.exists()){
                    for(DataSnapshot item:snapshot.getChildren()){
                        Door doorItem = item.getValue(Door.class);
                        doorLists.add(doorItem.getName());
                        doorIdLists.add(doorItem.getId());
                    }

                }
                else
                    doorLists.add("No Door present");
                doorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgAddDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,AddDoorActivity.class);
                startActivity(intent);
            }
        });

        spinnerDoor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!doorIdLists.isEmpty()){
                    DOORID = doorIdLists.get(position);
                    mDoors.child(doorIdLists.get(position)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            historyArrayList.clear();
                            if(snapshot.exists())
                            {
                                History item = new History();
                                item.setStatus(snapshot.child("status").getValue().toString());
                                item.setName("Default user");
                                DOORIP = snapshot.child("ip").getValue().toString();
                                item.setType(0);
                                if(snapshot.hasChild("image")) {
                                    item.setImgUrl(snapshot.child("image").getValue().toString());
                                    IMAGEURL= snapshot.child("image").getValue().toString();
                                }
                                else{
                                    item.setImgUrl("https://wbbme.org/wp-content/themes/eikra/assets/img/noimage-420x273.jpg");
                                    IMAGEURL="";
                                }
                                ArrayList<History> dummy = new ArrayList<>();
                                if(snapshot.hasChild("history"))
                                {
                                    int i=0;
                                    for(DataSnapshot itemSnap : snapshot.child("history").getChildren())
                                    {
                                        History x = itemSnap.getValue(History.class);
                                        dummy.add(x);
//                                        if(i>=9)
//                                            break;
                                        i++;


                                    }
                                }
                                dummy.add(item);
                                Collections.reverse(dummy);

                                for(int h=0;h<dummy.size();h++)
                                {
                                    historyArrayList.add(dummy.get(h));
                                    if(h>=9)
                                        break;
                                }


                                historyAdapter.notifyDataSetChanged();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}