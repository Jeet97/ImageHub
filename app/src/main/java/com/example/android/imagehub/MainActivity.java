package com.example.android.imagehub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<getsetclass>>,SwipeRefreshLayout.OnRefreshListener{

    String url;
    RecyclerView rv;
    customadapter ca;
    LinearLayoutManager llm;
    private final static int SELECT_PICTURE = 1;
    ImageView user_photo;
    String selectedImagePath;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pb;
    Toolbar tb;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.imagelist);
        pb = (ProgressBar) findViewById(R.id.pbar);
     fab  = (FloatingActionButton) findViewById(R.id.fab);
        tb = (Toolbar) findViewById(R.id.toolbar);
        user_photo = (ImageView) findViewById(R.id.user_profile);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        url = this.getString(R.string.ip)+"/uploads";


        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shp = getSharedPreferences("mytoken",MODE_PRIVATE);
                shp.edit().remove("token").commit();
                Intent in = new Intent(MainActivity.this,Login_activity.class);
                startActivity(in);
                finish();
            }
        });

        //setSupportActionBar(tb);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(MainActivity.this,uploadactivity.class);
                startActivity(in);

            }
        });

        getSupportLoaderManager().initLoader(0,null,this).forceLoad();

    }

    @Override
    protected void onResume() {
        super.onResume();
      this.onRefresh();

    }


    @Override
    public Loader<ArrayList<getsetclass>> onCreateLoader(int id, Bundle args) {
        return new customloader(this,url);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<getsetclass>> loader, ArrayList<getsetclass> data) {

if (swipeRefreshLayout.isRefreshing())
    swipeRefreshLayout.setRefreshing(false);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        ca = new customadapter(this,data);
        rv.setAdapter(ca);
        pb.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<getsetclass>> loader) {

    }


    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(0,null,this).forceLoad();

if (!swipeRefreshLayout.isRefreshing())
    swipeRefreshLayout.setRefreshing(true);
    }
}
