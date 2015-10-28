package com.beswell.beswell_al;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ShowQueryRes extends ActionBarActivity {

    private Button all;
    private Button checked;
    private Button unchecked;

    public QueryListAdapter qla;
    public ListView lv;
    public List<String[]> data;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_query_res);

        all = (Button)findViewById(R.id.all);
        checked = (Button)findViewById(R.id.checked);
        unchecked = (Button)findViewById(R.id.unchecked);

        lv = (ListView)findViewById(R.id.query_res);

        initLV();

        intent = getIntent();
        final Bundle raw = intent.getBundleExtra("query");

        Log.d("SQS Debug", raw.toString());
        setData(99, raw);
        refresh();
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData(99, raw);
                refresh();
            }
        });

        checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData(1, raw);
                refresh();
            }
        });

        unchecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData(0, raw);
                refresh();
            }
        });
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_query_res, menu);
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
    }*/

    public void initLV(){
        data = new ArrayList<String[]>();
        qla = new QueryListAdapter(getApplicationContext(), data);
        lv.setAdapter(qla);
    }

    public void setData(int flag, Bundle input){
        switch (flag){
            case 0:
                data.clear();
                for(int i = 0; i < input.size(); i++){
                    String tmp = input.getString("" + i);
                    String[] elems = tmp.split("  ");
                    if(elems[elems.length - 1].equals("0")){
                        data.add(elems);
                    }
                }
                break;
            case 1:
                data.clear();
                for(int i = 0; i < input.size(); i++){
                    String tmp = input.getString("" + i);
                    String[] elems = tmp.split("  ");
                    if(elems[elems.length - 1].equals("1")){
                        data.add(elems);
                    }
                }
                break;
            case 99:
                data.clear();
                for(int i = 0; i < input.size(); i++){
                    String tmp = input.getString("" + i);
                    String[] elems = tmp.split("  ");
                    data.add(elems);
                }
                break;
        }
    }

    public void refresh(){
        qla.notifyDataSetChanged();
    }
}
