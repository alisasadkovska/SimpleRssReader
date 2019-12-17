package com.alisasadkovska.simplerssreader.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.alisasadkovska.simplerssreader.Adapter.FeedAdapter;
import com.alisasadkovska.simplerssreader.common.Common;
import com.alisasadkovska.simplerssreader.common.HTTPDataHandler;
import com.alisasadkovska.simplerssreader.R;
import com.alisasadkovska.simplerssreader.model.RSSObject;
import com.google.gson.Gson;

import static com.alisasadkovska.simplerssreader.common.Common.API_KEY;
import static com.alisasadkovska.simplerssreader.common.Common.RSS_link;

public class SourceActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    RSSObject rssObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadRss();
    }

    private void loadRss() {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, String, String> loadRssAsync = new AsyncTask<String, String, String>() {
            ProgressDialog mDialog = new ProgressDialog(SourceActivity.this);

            @Override
            protected void onPreExecute() {
               mDialog.setMessage("Please wait...");
               mDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String result;
                HTTPDataHandler http = new HTTPDataHandler();
                result = http.GetHTTPData(params[0]);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
               mDialog.dismiss();
               rssObject = new Gson().fromJson(s, RSSObject.class);
               FeedAdapter adapter = new FeedAdapter(rssObject, getBaseContext());
               recyclerView.setAdapter(adapter);
               adapter.notifyDataSetChanged();
            }
        };

        String RSS_to_json_API = "https://api.rss2json.com/v1/api.json?rss_url=";


        loadRssAsync.execute(RSS_to_json_API + RSS_link + API_KEY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh)
            loadRss();
        return true;

    }
}
