package com.example.android.imagehub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Login_activity extends AppCompatActivity {

    private EditText usernameed, passworded;
    private String username, password;
    private Button signin,signup;
    String url;
    private ProgressDialog pd;
    String resstring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        usernameed = (EditText) findViewById(R.id.username);
        passworded = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.signin);
        signup = (Button) findViewById(R.id.signup);
        url = this.getString(R.string.ip)+"/login";
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Login_activity.this,Create_account.class);
                startActivity(in);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameed.getText().toString();
                password = passworded.getText().toString();
                usernameed.setText("");
                passworded.setText("");
                if (!(username.equals("") || password.equals(""))) {
                    asyncit as = new asyncit();
                    as.execute(url);
                }

            }
        });
        pd = new ProgressDialog(this);
    }


    class asyncit extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(strings[0]);



            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("username", username));
            nameValuePair.add(new BasicNameValuePair("password", "" + password));

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

        @Override
        protected void onPreExecute() {
            pd.setMessage("Verifying");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();

            if (!s.equals("Not Found"))
            {
                Log.v("this is token",s);
                SharedPreferences shp = Login_activity.this.getSharedPreferences("mytoken",MODE_PRIVATE);
                SharedPreferences.Editor edit  = shp.edit();
                edit.putString("token",s);
                edit.commit();
                Intent in = new Intent(Login_activity.this,MainActivity.class);

                startActivity(in);
                finish();
            }
            else if (s.equals("Not Found")){
                runOnUiThread(rn);
                Log.v("hbvwfilwhlfihw",s);

            }
            super.onPostExecute(s);
        }

        private Runnable rn = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Login_activity.this, "Not Found", Toast.LENGTH_SHORT).show();
            }
        };

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
}
