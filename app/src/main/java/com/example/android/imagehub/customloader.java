package com.example.android.imagehub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeet on 04-09-2017.
 */

public class customloader extends AsyncTaskLoader<ArrayList<getsetclass>> {



    ArrayList<getsetclass> arl;
    Activity act;
    String url,imageurl;
    ImageView userimage;

    public customloader(Activity act,String url) {
        super(act);
        arl = new ArrayList<getsetclass>();
        this.act = act;
this.url = url;
        userimage = (ImageView) act.findViewById(R.id.user_profile);
    }

    @Override
    public ArrayList<getsetclass> loadInBackground() {
        String response = "";
        try {
            URL realurl = new URL(url);
             response = makehttpconnection(realurl);
            Log.v("efkbwliyhefbiw", response);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (response.equals("Not Logged In"))
        {
            Intent in = new Intent(act, Login_activity.class);
            act.startActivity(in);
            ((AppCompatActivity) act).finish();
            return arl;

        }

    else
        {

            try


            {





                JSONArray jaa = new JSONArray(response);
                JSONObject joo  = jaa.optJSONObject(0);
                imageurl =  joo.optString("userpic");
                if (!imageurl.equals("null"))
                    act.runOnUiThread(rn);


                JSONArray ja = jaa.optJSONArray(1);

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jao = ja.getJSONObject(i);

                    String name = jao.optString("name");
                    if (name.equals("null"))
                        name = "";
                    String url = jao.optString("url");
                    String picuserpic = jao.optString("pic_userpic");
                    String user = jao.optString("user");
                    String time = jao.optString("time");
                    String imgname = jao.optString("imgname");
                    String likes = jao.optString("likes");


                    arl.add(new getsetclass(name, url,picuserpic,user,time,likes,imgname));
                }

            }  catch (JSONException e) {
                e.printStackTrace();
            }


            return arl;
        }
    }

    private Runnable rn = new Runnable() {
        @Override
        public void run() {
            Glide.with(act).load(imageurl).into(userimage);
        }
    };

    private String makehttpconnection(URL url) throws IOException
    {

        String resstring = "";

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(act.getString(R.string.ip)+"/uploads");

        SharedPreferences shp = act.getSharedPreferences("mytoken",Context.MODE_PRIVATE);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("token", shp.getString("token",null)));
      //  nameValuePair.add(new BasicNameValuePair("token", "hello"));

           /* String authorizationString = "Basic " + Base64.encodeToString(
                    (username + ":" + password).getBytes(),
                    Base64.NO_WRAP); //Base64.NO_WRAP flag
*/

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            //   httpPost.setHeader("Authorization", authorizationString);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }


        HttpResponse response = null;
        int status = 0;
        try {
            response = httpClient.execute(httpPost);
            resstring  = getrealstring(response.getEntity().getContent());


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return resstring;
    }


    private String getrealstring(InputStream is) throws IOException
    {
        StringBuilder sb = new StringBuilder();

        if (is!=null)
        {
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));

            BufferedReader bf = new BufferedReader(isr);

            String line = bf.readLine();
            while (line!=null)
            {
                sb.append(line);
                line = bf.readLine();
            }
        }
        return sb.toString();
    }



}
