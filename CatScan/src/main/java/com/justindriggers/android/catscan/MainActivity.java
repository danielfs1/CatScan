package com.justindriggers.android.catscan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private Handler mHandler;
    private ListView listView;
    private LogEntityAdapter adapter;
    private List<LogEntity> logs;
    private LogReaderTask logReaderTask;
    private TextView unreadNotification;

    private Animation animSlideIn;
    private Animation animSlideOut;

    private int unreadCount = 0;
    private int lastTopCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animSlideIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.abc_slide_in_top);
        animSlideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_out_top);

        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);

        unreadNotification = (TextView)findViewById(R.id.unreadNotification);
        unreadNotification.setVisibility(View.INVISIBLE);

        logs = new ArrayList<>();
        adapter = new LogEntityAdapter(this, logs);

        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0) {
                    lastTopCount = totalItemCount;
                    if(unreadNotification.getVisibility() == View.VISIBLE) {
                        unreadNotification.setVisibility(View.INVISIBLE);
                        unreadNotification.startAnimation(animSlideOut);
                    }
                } else {
                    if(unreadNotification.getVisibility() == View.INVISIBLE) {
                        unreadNotification.setVisibility(View.VISIBLE);
                        unreadNotification.startAnimation(animSlideIn);
                    }
                }
            }
        });

        unreadNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(0);
            }
        });

        mHandler = new LogHandler(adapter, runnable);

        logReaderTask = new LogReaderTask(mHandler);
        logReaderTask.execute();
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            unreadCount = logs.size() - lastTopCount;
            unreadNotification.setText(unreadCount + " new logs");
        }
    };

    @Override
    protected void onDestroy() {
        logReaderTask.stopTask();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "test");
        startActivity(new Intent(this, LogDetailActivity.class).putExtra("type", adapter.getItem(index).getPriority().getThemeResource()));
    }
}
