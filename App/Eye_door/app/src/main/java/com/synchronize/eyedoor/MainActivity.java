package com.synchronize.eyedoor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnOpen,btnClose;
    private EditText edtGetIp;
    private RequestQueue requestQueue;
    private String BASEURL ="http://";
    private ImageView imageView;
    private DatabaseReference activityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnClose = findViewById(R.id.btnClose);
        btnOpen = findViewById(R.id.btnOpen);
        edtGetIp = findViewById(R.id.edtGetIp);
        imageView = findViewById(R.id.img);
        activityRef = FirebaseDatabase.getInstance().getReference("image");


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        activityRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    String url = snapshot.getValue().toString();
                    Log.i("dsdsfd",url);
                    Picasso.with(getApplicationContext()).load(url).into(imageView);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {
                if(task.isSuccessful())
                {
                    String refreshedToken = task.getResult();
                    FirebaseMessaging.getInstance().subscribeToTopic("all");
                    Log.i("FCM Token", refreshedToken);
                    Toast.makeText(getApplicationContext(),refreshedToken,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.i("FCM ",task.getException().getMessage());
                }
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtGetIp.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Please provide Door Address",Toast.LENGTH_SHORT).show();
                else
                {
                    BASEURL = "http://" +edtGetIp.getText().toString()+":5000/opendoor";
                    Log.i("url",BASEURL);

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, BASEURL, null,
                            new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    JSONObject obj = (JSONObject)response;
                                    try {

                                        Toast.makeText(getApplicationContext(),obj.getString("output"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //String responseBody = new String(error.networkResponse.data, "utf-8");
                                    Log.i("error",error.getMessage()+" ");
                                    Toast.makeText(getApplicationContext(),"Error :"+error.getMessage(),Toast.LENGTH_LONG).show();


                                }
                            }) ;


                    requestQueue.add(jsonObjReq);
                }





            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(edtGetIp.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Please provide Door Address",Toast.LENGTH_SHORT).show();
                else
                {
                    BASEURL = "http://" +edtGetIp.getText().toString()+":5000/closedoor";
                    Log.i("url",BASEURL);

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, BASEURL, null,
                            new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    JSONObject obj = (JSONObject)response;
                                    try {

                                        Toast.makeText(getApplicationContext(),obj.getString("output"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(),"Error : "+error.getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }) ;


                    requestQueue.add(jsonObjReq);
                }

            }
        });


    }
}