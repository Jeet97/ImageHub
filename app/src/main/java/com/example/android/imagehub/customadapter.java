package com.example.android.imagehub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeet on 04-09-2017.
 */

public class customadapter extends RecyclerView.Adapter<customadapter.myviewholder> implements onasynccomplete{

    public ArrayList<getsetclass> arl;
    public  boolean arr[];
    Context act;
    String likeurl,dislikeurl;
    private myviewholder hld;

    public customadapter(Context act,ArrayList<getsetclass> arl)
    {
        this.act = act;
        this.arl =arl;

        arr = new boolean[arl.size()];

        for (int i=0;i<arl.size();i++)
            arr[i] = false;

likeurl = act.getString(R.string.ip)+"/like";
dislikeurl = act.getString(R.string.ip)+"/unlike";
    }

    @Override
    public myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vw = LayoutInflater.from(act).inflate(R.layout.custom_layout,parent,false);


        return new myviewholder(vw);
    }




    @Override
    public void onBindViewHolder(final myviewholder holder, final int position) {

        final getsetclass gt = arl.get(position);



        holder.vname.setText(gt.getArtname());
        Log.v("hyuvlyib",gt.getUrl());
        Glide.with(act).load(gt.getUrl())
                .into(holder.vthumb);
        Glide.with(act).load(gt.getPic_userpic())
                .into(holder.uspic);
        holder.us.setText(gt.getUser());
        holder.ustime.setText(gt.getTime());
        holder.tlikes.setText(gt.getLikes());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NameValuePair> lst = new ArrayList<NameValuePair>();
                lst.add(new BasicNameValuePair("imagename",gt.getImgname()));
                hld = holder;
               posttext pt = new posttext(act,lst,customadapter.this);

                if (!arr[position]) {
                    pt.execute(likeurl);
                    arr[position] = true;
                }
                else {
                    pt.execute(dislikeurl);
                    arr[position] = false;
                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return arl.size();
    }

    @Override
    public void onTaskComplete(String response) {
        Log.v("heyyyyyyy",response);
        hld.tlikes.setText(response);
    }

    @Override
    public void onTaskStarted() {

    }


    public class myviewholder extends RecyclerView.ViewHolder
    {
        TextView vname,us,ustime;
        ImageView vthumb,uspic;
        ImageButton like;
        TextView tlikes;
        public myviewholder(View vw)
        {
            super(vw);
            vname =   (TextView)vw.findViewById(R.id.custvname);
            vthumb =(ImageView) vw.findViewById(R.id.custimage);
            uspic =(ImageView) vw.findViewById(R.id.pic_userpic);
            us =(TextView) vw.findViewById(R.id.pic_username);
            ustime =(TextView) vw.findViewById(R.id.pic_time);
            like = (ImageButton) vw.findViewById(R.id.likedis);
            tlikes = (TextView) vw.findViewById(R.id.tlike);



        }



    }



}
