package com.beswell.beswell_al;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ShowHistRes extends Activity {

    TextView tv;
    Intent intent;

    Spinner sp;
    int pos;

    Button next;
    Button prev;

    TextView hist_id;
    TextView hist_time;
    TextView hist_plate;
    TextView hist_cardcode;
    TextView hist_cctype;
    TextView hist_cctimes;
    TextView hist_star;
    TextView hist_comment;

    TextView count;
    TextView cccount;

    ArrayAdapter<String> adapter;
    Bundle records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_show_hist_res);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_hist);

        intent = getIntent();
        records = intent.getBundleExtra("Result");

        sp = (Spinner)findViewById(R.id.hist_item);

        next = (Button)findViewById(R.id.hist_nextPage);
        prev = (Button)findViewById(R.id.hist_prevPage);

        hist_id = (TextView)findViewById(R.id.hist_id);
        hist_time = (TextView)findViewById(R.id.hist_time);
        hist_plate = (TextView)findViewById(R.id.hist_plate);
        hist_cardcode = (TextView)findViewById(R.id.hist_cardcode);
        hist_cctype = (TextView)findViewById(R.id.hist_cctype);
        hist_cctimes = (TextView)findViewById(R.id.hist_cctimes);
        hist_star = (TextView)findViewById(R.id.hist_star);
        hist_comment = (TextView)findViewById(R.id.hist_comment);

        count = (TextView)findViewById(R.id.total_count);
        cccount = (TextView)findViewById(R.id.total_cccount);

        int count_i = records.size();
        int cccount_i = 0;
        for(int i = 0; i < count_i; i++){
            String str = records.getString("" + i);
            String[] item = str.split("  ");
            cccount_i += Integer.parseInt(item[3]);
        }
        count.setText("数量合计：" + count_i);
        cccount.setText("洗车次数合计：" + cccount_i);

        setContent(records, 0);

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1/*R.layout.spinner_item*/);
        for (int i = 0; i < records.size(); i++) {
            int temp = i + 1;
            adapter.add("第" + temp + "页");
        }

        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                setContent(records, position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0) {
                    Toast.makeText(getApplicationContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
                } else {
                    pos--;
                    setContent(records, pos);
                    sp.setSelection(pos);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == records.size() - 1) {
                    Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                } else {
                    pos++;
                    setContent(records, pos);
                    sp.setSelection(pos);
                }
            }
        });
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_hist_res, menu);
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

    public void setContent(Bundle set, int serialNo){
        String record = set.getString("" + serialNo);
        String[] elem = record.split("  ");

        hist_id.setText(" 序号：" + (serialNo + 1));
        if(serialNo == 0) {
            hist_time.setText(" 时间：" + elem[0].substring(0, 16));
        }
        else{
            hist_time.setText(" 时间：" + elem[0].substring(0, 17));
        }
        hist_plate.setText(" 车牌：" + elem[1]);
        hist_cctype.setText(" 类型：" + elem[2]);
        hist_cardcode.setText(" 卡号：" + elem[4]);
        hist_cctimes.setText(" 次数：" + elem[3]);
        if(elem[5].equals("null")) {
            hist_star.setText(" 评分： - ");
        }
        else{
            hist_star.setText(" 评分：" + elem[5]);
        }
        if(elem[6].equals("null")) {
            hist_comment.setText(" 评价： - ");
        }
        else{
            hist_comment.setText(" 评价：" + elem[6]);
        }
    }
}
