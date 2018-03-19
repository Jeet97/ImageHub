package com.example.android.imagehub;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Create_account extends AppCompatActivity implements onasynccomplete {

   private  EditText name,pass,ea,ph;
    private ImageView photo;
  private  String username,password,email,phone;
    private boolean imageupload = false;
   private Button create;
    private String url;
    String selectedImagePath;
    private final static int SELECT_PICTURE = 1;
  private  AlertDialog.Builder builder1;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        name = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        ph = (EditText) findViewById(R.id.phone);
        ea = (EditText) findViewById(R.id.email);
        photo = (ImageView) findViewById(R.id.userimage);
        create = (Button) findViewById(R.id.create);
        pd  = new ProgressDialog(this);
        url = this.getString(R.string.ip)+"/new_user";

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent();

                in.setType("image/*");
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,SELECT_PICTURE);
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = name.getText().toString();
                password = pass.getText().toString();
                email = ea.getText().toString();
                phone = ph.getText().toString();

                if (username.equals("")||password.equals("")||email.equals("")||phone.equals(""))
                    opendialog("None of the Fields can be empty!!");
                else
                {

                    compressImage ci = new compressImage(Create_account.this);
                    String path = ci.compressImage(selectedImagePath);


                    List<NameValuePair> lst = new ArrayList<NameValuePair>();
                    lst.add(new BasicNameValuePair("username",username));
                    lst.add(new BasicNameValuePair("password",password));
                    lst.add(new BasicNameValuePair("email",email));
                    lst.add(new BasicNameValuePair("phone",phone));

                    if (imageupload && !path.equals(null))
                        lst.add(new BasicNameValuePair("user_image",path));

                    post_profile pp = new post_profile(Create_account.this,lst,Create_account.this,"user_image");
                    pp.execute(url);
                }


            }
        });

    }

    private void opendialog(String msg)
    {

        builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);
builder1.setMessage(msg);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
name.setText("");
                        pass.setText("");
                        ea.setText("");
                        ph.setText("");
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    @Override
    public void onTaskComplete(String response) {
        pd.dismiss();

switch (response)
{
    case Errors.ERROR_NONE:
        opendialog(response);
    break;

    case Errors.ERROR_DUPLICATE:
        opendialog(response);
        break;

    case Errors.ERROR_DATABASE:
        opendialog(response);
        break;

    default:
        Log.v("this is token",response);
        SharedPreferences shp = Create_account.this.getSharedPreferences("mytoken",MODE_PRIVATE);
        SharedPreferences.Editor edit  = shp.edit();
        edit.putString("token",response);
        edit.commit();
        Intent in = new Intent(Create_account.this,MainActivity.class);
        Toast.makeText(this,"Account successfully created",Toast.LENGTH_SHORT).show();
        startActivity(in);
        finish();
}

    }

    @Override
    public void onTaskStarted() {
        pd.setMessage("Setting account..");
        pd.show();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

                Log.v("reugugng6g",selectedImagePath);

                Glide.with(this).load(selectedImagePath).into(photo);
                imageupload  = true;
            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }
}
