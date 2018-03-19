package com.example.android.imagehub;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Jeet on 03-10-2017.
 */

public class post_profile extends AsyncTask<String,Void,String> {

    private Context cxt;
    private List<NameValuePair> lst;
    private  String realresponse;
    private onasynccomplete listener;
    private String filetoken;

    public post_profile(Context cxt,List<NameValuePair> lst,onasynccomplete listener,String filetoken)
    {
        this.cxt = cxt;
        this.lst  = lst;

        this.listener = listener;
        this.filetoken = filetoken;
    }



    @Override
    protected String doInBackground(String... strings) {


        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(strings[0]);

        try {
            org.apache.http.entity.mime.MultipartEntity entity = new org.apache.http.entity.mime.MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < lst.size(); index++) {
                if (lst.get(index).getName()
                        .equalsIgnoreCase(filetoken)) {
                    entity.addPart(
                            lst.get(index).getName(),
                            new FileBody(new File(lst.get(
                                    index).getValue()),"application/octet"));
                } else {
                    // Normal string data
                    entity.addPart(lst.get(index).getName(),
                            new StringBody(lst.get(index)
                                    .getValue()));
                }


            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            realresponse  = getrealstring(response.getEntity().getContent());


        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

     catch (ClientProtocolException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }










        return realresponse;

    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStarted();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onTaskComplete(s);

        super.onPostExecute(s);
    }



    private String getrealstring(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();

        if (is != null) {
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));

            BufferedReader bf = new BufferedReader(isr);

            String line = bf.readLine();
            while (line != null) {
                sb.append(line);
                line = bf.readLine();
            }
        }
        return sb.toString();
    }
}
