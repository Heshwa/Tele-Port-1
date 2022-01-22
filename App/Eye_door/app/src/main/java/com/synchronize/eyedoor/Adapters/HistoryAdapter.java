package com.synchronize.eyedoor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.synchronize.eyedoor.HomeActivity;
import com.synchronize.eyedoor.Models.History;
import com.synchronize.eyedoor.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<History> historyArrayList;
    private RequestQueue requestQueue;
    private DatabaseReference mUser;
    private String noPreviewURL="https://wbbme.org/wp-content/themes/eikra/assets/img/noimage-420x273.jpg";

    public HistoryAdapter() {
    }

    public HistoryAdapter(Context mContext, ArrayList<History> historyArrayList) {
        this.mContext = mContext;
        this.historyArrayList = historyArrayList;
        requestQueue = Volley.newRequestQueue(mContext);
        mUser = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = historyArrayList.get(position);

        if(item.getType()==0){
            holder.linearLayoutDoorItem.setVisibility(View.GONE);
            holder.linearLayoutDoor.setVisibility(View.VISIBLE);
            holder.txtStatus.setText(item.getStatus());
            Picasso.with(mContext).load(item.getImgUrl()).into(holder.imgPreview);
            final String door,changeDoor;
            if(item.getStatus().equals("Closed")) {
                holder.imgLock.setImageResource(R.drawable.ic_outline_lock_24);
                holder.btnOpenClose.setText("Open");
                door="opendoor";
                changeDoor = "Opened";
            }
            else {
                holder.imgLock.setImageResource(R.drawable.ic_baseline_lock_open_24);
                holder.btnOpenClose.setText("Close");
                door = "closedoor";
                changeDoor ="Closed";
            }
            holder.txtHistory.setVisibility(View.GONE);
            holder.btnOpenClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String BASEURL = "http://" + HomeActivity.DOORIP +":5000/"+door;
//                    Log.i("BASEURL",BASEURL);


//                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                    Date date = new Date();
//                    String time = dateFormat.format(date),name = item.getName();
//
//                    mUser.child("Doors").child(HomeActivity.DOORID).child("status").setValue(changeDoor);
//                    String imgURL="";
//                    if(HomeActivity.IMAGEURL.equals(""))
//                        imgURL = noPreviewURL;
//                    else
//                        imgURL = HomeActivity.IMAGEURL;
//
//                    History history = new History(imgURL,time,name,changeDoor,1);
//                    mUser.child("Doors").child(HomeActivity.DOORID).child("history").push().setValue(history);
//                    Toast.makeText(mContext,"obj.getStri",Toast.LENGTH_SHORT).show();




                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, BASEURL, null,
                            new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    JSONObject obj = (JSONObject)response;
                                    try {
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        Date date = new Date();
                                        String time = dateFormat.format(date),name = item.getName();

                                        mUser.child("Doors").child(HomeActivity.DOORID).child("status").setValue(changeDoor);
                                        String imgURL="";
                                        if(HomeActivity.IMAGEURL.equals(""))
                                            imgURL = noPreviewURL;
                                        else
                                            imgURL = HomeActivity.IMAGEURL;

                                        History history = new History(imgURL,time,name,changeDoor,1);
                                        mUser.child("Doors").child(HomeActivity.DOORID).child("history").push().setValue(history);
                                        Toast.makeText(mContext,obj.getString("output"),Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("error",error.getMessage()+" ");
                                    Toast.makeText(mContext,"Error :"+error.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            }) ;


                    requestQueue.add(jsonObjReq);

                }
            });
        }
        else
        {
            holder.txtHistory.setVisibility(View.VISIBLE);

            holder.linearLayoutDoorItem.setVisibility(View.VISIBLE);
            holder.linearLayoutDoor.setVisibility(View.GONE);
            Picasso.with(mContext).load(item.getImgUrl()).into(holder.imgHisPreview);
            holder.txtUserName.setText(item.getName());
            if(item.getStatus().equals("Closed"))
                holder.imgHisLock.setImageResource(R.drawable.ic_outline_lock_24);
            else
                holder.imgHisLock.setImageResource(R.drawable.ic_baseline_lock_open_24);
            holder.txtTime.setText(item.getTime());

        }

    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutDoor,linearLayoutDoorItem;
        Button btnAction,btnOpenClose;
        TextView txtStatus,txtUserName,txtTime,txtHistory;
        ImageView imgLock,imgHisPreview,imgHisLock,imgPreview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutDoor = itemView.findViewById(R.id.linearLayoutDoor);
            linearLayoutDoorItem = itemView.findViewById(R.id.linearLayoutDoorItem);
//            btnAction = itemView.findViewById(R.id.btnAction);
            btnOpenClose = itemView.findViewById(R.id.btnOpenClose);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgLock = itemView.findViewById(R.id.imgCloseOpen);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imgHisPreview = itemView.findViewById(R.id.imgHisPreview);
            imgHisLock = itemView.findViewById(R.id.imgHisOpenClose);
            imgPreview = itemView.findViewById(R.id.imgPreview);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtHistory = itemView.findViewById(R.id.txtHistroy);
        }
    }
}
