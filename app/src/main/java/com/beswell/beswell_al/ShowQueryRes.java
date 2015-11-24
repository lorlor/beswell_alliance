package com.beswell.beswell_al;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class ShowQueryRes extends Activity {

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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_show_query_res);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_queryres);

        all = (Button)findViewById(R.id.btn_showquery_all);
        checked = (Button)findViewById(R.id.btn_showquery_checked);
        unchecked = (Button)findViewById(R.id.btn_showquery_unchecked);

        lv = (ListView)findViewById(R.id.lv_showquery_displayres);

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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

    /**
     * “开卡查询”中的ListView使用的Adapter是QueryListAdapter，其中使用的自定义的布局文件为
     * query_res.xml
     */
    public void initLV(){
        data = new ArrayList<String[]>();
        qla = new QueryListAdapter(getApplicationContext(), data);
        lv.setAdapter(qla);

        /**
         * 获取设别的长宽值，单位像素（px）
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams lp = findViewById(R.id.query_cont).getLayoutParams();
        lp.width = dm.widthPixels;
        //动态设定布局文件的长和宽，以适应不同的屏幕。此时在.xml的布局文件中设置的长宽值会失效
        lp.height = (int)getResources().getDimension(R.dimen.query_len);

        findViewById(R.id.query_cont).setLayoutParams(lp);
    }

    /**
     * 点击不同按钮（All ，checked ，unchecked）时，调用此函数设定ListView的显示内容
     */
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
