package com.example.android.imagehub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class uploadactivity extends AppCompatActivity implements onasynccomplete{
FloatingActionButton choose, upload;

    ImageView image;
    String selectedImagePath;
 private final static int SELECT_PICTURE = 1;
    Bitmap imageBitmap;
   private static final int REQUEST_IMAGE_CAPTURE = 2;
    EditText ed;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_a_image);

        choose = (FloatingActionButton) findViewById(R.id.Gallery);
        upload  =(FloatingActionButton) findViewById(R.id.upload);
        image = (ImageView) findViewById(R.id.upimage);
        ed  = (EditText) findViewById(R.id.upname);
        pd  = new ProgressDialog(this);

        /*
camera.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

       File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/" + System.currentTimeMillis() + ".jpg"); //== where you want full size image
selectedImagePath = imgFile.getAbsolutePath();
        Log.v("file saved ",imgFile.getAbsolutePath());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imgFile));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
});

*/
        choose.setOnClickListener(
        new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent in = new Intent();

        in.setType("image/*");
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,SELECT_PICTURE);
    }
});

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compressImage ci = new compressImage(uploadactivity.this);
             String path =    ci.compressImage(selectedImagePath);




                if (!path.equals(null)) {
                    List<NameValuePair> lst = new ArrayList<NameValuePair>();
                    SharedPreferences shp = getSharedPreferences("mytoken",MODE_PRIVATE);

                    lst.add(new BasicNameValuePair("token",shp.getString("token",null)));
                    lst.add(new BasicNameValuePair("caption",ed.getText().toString()));
                    lst.add(new BasicNameValuePair("post_pic",path));

                    post_profile pp = new post_profile(uploadactivity.this,lst,uploadactivity.this,"post_pic");
                    pp.execute(uploadactivity.this.getString(R.string.ip)+"/upload_pic");
                }

                else
                {
                    Toast.makeText(uploadactivity.this,"Please Select a image",Toast.LENGTH_SHORT).show();
                }

            }


        });



    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

                Log.v("reugugng6g",selectedImagePath);

                Glide.with(this).load(selectedImagePath).into(image);
            }

           else if (requestCode == REQUEST_IMAGE_CAPTURE) {
               /* Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                image.setImageBitmap(imageBitmap);

                selectedImagePath = getPath((Uri) extras.get(MediaStore.EXTRA_OUTPUT));
                Log.v("path where saved",selectedImagePath);
            */

               Glide.with(this).load(selectedImagePath).into(image);

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


    @Override
    public void onTaskComplete(String response) {
pd.dismiss();
        Log.v("this is response",response);
        Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onTaskStarted() {
pd.setMessage("Uploading..");
        pd.show();
    }
}
