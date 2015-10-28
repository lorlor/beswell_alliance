package com.beswell.beswell_al;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainScreen extends ActionBarActivity {

    static String IP = "192.168.0.101";

    public Timer timer = null;
    public TimerTask task = null;

    private TextView alid;
    private Button alback;
    private Button alhistory;
    private Button alset;
    private Button alquery;

    private ListView container;
    private ListViewAdapter lva;
    public List<String[]> list;

    public String Alid;
    public String hash;

    int sid;
    SoundPool sp;

    public int history = 0;

    PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        alid = (TextView)findViewById(R.id.AlCCId);
        alback = (Button)findViewById(R.id.AlCCBack);
        alhistory = (Button)findViewById(R.id.AlCCHistory);
        alset = (Button)findViewById(R.id.AlCCSet);
        alquery = (Button)findViewById(R.id.AlCCQuery);
        container = (ListView)findViewById(R.id.AlContent);

        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sid = sp.load(MainScreen.this, R.raw.coin_message, 1);

        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Refresh");

        Intent intent = getIntent();
        Alid = intent.getStringExtra("Alid");
        hash = intent.getStringExtra("hash");
        alid.setText("联盟号：" + Alid);

        alback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(0 == sid){
                    Toast.makeText(getApplicationContext(), "声音加载失败", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(MainScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        alhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.beswell.beswell_al.History h = com.beswell.beswell_al.History.newInstance(hash, Alid);
                h.show(getFragmentManager(), "历史查询");
            }
        });

        alset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAsyncTask sat = new SetAsyncTask();
                sat.execute(IP);
            }
        });

        alquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryAsyncTask qat = new QueryAsyncTask();
                qat.execute(IP);
            }
        });

        initLV();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wl.acquire();
        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        wl.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    public void initLV(){
        String[] gnr_info = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info1 = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info2 = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info3 = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info4 = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info5 = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info6 = new String[]{"One", "2015-12-13", "3"};
        String[] gnr_info7 = new String[]{"One", "2015-12-13", "3"};
        list = new ArrayList<String[]>();

        /*
        list.add(gnr_info);
        list.add(gnr_info1);
        list.add(gnr_info2);
        list.add(gnr_info3);
        list.add(gnr_info4);
        list.add(gnr_info5);
        list.add(gnr_info6);
        list.add(gnr_info7);*/

        lva = new ListViewAdapter(this, list);
        container.setBackgroundResource(R.drawable.beswell_logo);
        container.setAdapter(lva);

    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("Debug in Handler=>", msg.toString());
            String res = msg.obj.toString();
            if(res.substring(0,1).equals("1")){
                container.setBackgroundColor(0xffffff);
                Log.d("Debug", "yes, it\'s me!");
                RefreshQueryAsyncTask rqat = new RefreshQueryAsyncTask();
                rqat.execute(IP);
                sp.play(sid, 1, 1, 0, 0, 1);
            }

            refresh();
        }
    };

    /*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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

    public void refresh(){
        container.setBackgroundColor(0xffffff);
        Log.d("Debug refresh==>", "I am here.");
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                String retValue = "";

                String nameSpace = "http://" + IP;
                String url = "http://" + IP + "/ALServer.php";
                String methodName = "al_noticeCheck";
                String soapAction = "http://" + IP + "/ALServer.php/al_noticeCheck";

                SoapObject request = new SoapObject(nameSpace, methodName);
                // bind parameters here.
                request.addProperty("hashStr", hash);
                request.addProperty("alid", Alid);
                Log.d("Hash", hash);
                Log.d("ALID", Alid);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(request);

                HttpTransportSE se = new HttpTransportSE(url);
                try{
                    se.call(soapAction, envelope);
                }
                catch (HttpResponseException e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                catch (XmlPullParserException e){
                    e.printStackTrace();
                }

                SoapObject ret = (SoapObject) envelope.bodyIn;
                retValue = ret.getProperty("return").toString();
                Log.d("Runable Debug==>", retValue);

                //如果不考虑结果，下面三行进行注释，才能重复运行？？？？？
                Message msg = handler.obtainMessage();
                msg.obj = retValue;
                handler.sendMessage(msg);
            }
        };

        timer.schedule(task, 5000);
    }

    class RefreshQueryAsyncTask extends AsyncTask<String, Void, String>{

        String retValue = null;
        String[] out = null;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            int cur = 0;
            if(out.length == 0){
                cur = 0;
            }
            else {
                cur = out.length;
            }

            if(cur > history) {
                for (int i = 0; i < cur - history; i++) {
                    String[] tmp = out[i].split("  ");
                    if (0 == i) {
                        list.add(i, new String[]{tmp[0].substring(0, 16), tmp[1], tmp[2], tmp[3]});
                    } else
                        list.add(i, new String[]{tmp[0].substring(0, 17), tmp[1], tmp[2], tmp[3]});
                }
                history = cur;
                lva.notifyDataSetChanged();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "al_queryCCRecord";
            String soapAction = "http://" + params[0] + "/ALServer.php/al_queryCCRecord";

            String st = "2015-10-14";
            String et = "2015-10-15";

            SoapObject request = new SoapObject(nameSpace, methodName);
            // bind parameters here.
            request.addProperty("hashStr", hash);
            request.addProperty("alid", Alid);
           /* request.addProperty("st", st);
            request.addProperty("et", et);
*/
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);

            HttpTransportSE se = new HttpTransportSE(url);
            try{
                se.call(soapAction, envelope);
            }
            catch (HttpResponseException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }

            SoapObject ret = (SoapObject) envelope.bodyIn;
            retValue = ret.getProperty("return").toString();
            Log.d("Length of NULL", retValue.length() + " " + retValue);

            if(!retValue.equals("[]")) {
                String ret_1 = retValue.replace("[[", "[").replace("]]", "]").replace(",", " ").replace("[", "").replace("]", ",");
                String[] res = ret_1.split(", ");
                String[] tmp = res[0].split("  ");
                out = new String[res.length];
                for (int index = 0; index < res.length; index++) {
                    out[index] = res[index];
                }
            }
            else{
                out = new String[0];
                Log.d("length of out >>>>>>>>>>", out.length + "");
            }

            return null;
        }
    }

    // This class is not used.
    class BackAsyncTask extends AsyncTask<String, Void, String>{

        String retValue = null;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "login";
            String soapAction = "http://" + params[0] + "/ALServer.php/login";

            SoapObject request = new SoapObject(nameSpace, methodName);
            // bind parameters here.
            request.addProperty("ip", "192.168.0.54");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);

            HttpTransportSE se = new HttpTransportSE(url);
            try{
                se.call(soapAction, envelope);
            }
            catch (HttpResponseException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }

            SoapObject ret = (SoapObject) envelope.bodyIn;
            retValue = ret.getProperty("return").toString();

            /*
            *  Process returned data here.
            * */

            return null;
        }
    }

    class SetAsyncTask extends  AsyncTask<String, Void, String>{

        String retValue = null;
        String[] out = null;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(out.length == 0){
                Toast.makeText(getApplicationContext(), "没有对应数据，谢谢", Toast.LENGTH_SHORT).show();
            }
            else{
                Bundle tmp = new Bundle();
                for(int index = 0; index < out.length; index++){
                    tmp.putString(""+index, out[index]);
                }

                Intent intent = new Intent(MainScreen.this, SetPostOps.class);
                intent.putExtra("set_res", tmp);
                intent.putExtra("hash", hash);
                intent.putExtra("Alid", Alid);

                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "al_getShopInfo";
            String soapAction = "http://" + params[0] + "/ALServer.php/al_getShopInfo";

            SoapObject request = new SoapObject(nameSpace, methodName);
            // bind parameters here.
            request.addProperty("hashStr", hash);
            request.addProperty("alid", Alid);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);

            HttpTransportSE se = new HttpTransportSE(url);
            try{
                se.call(soapAction, envelope);
            }
            catch (HttpResponseException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }

            SoapObject ret = (SoapObject) envelope.bodyIn;
            retValue = ret.getProperty("return").toString();

            Log.d("Set Debug>>>>>>>>>>>", retValue);
            if(!retValue.equals("[]")) {
                String ret_1 = retValue.replace("[[", "[").replace("]]", "]").replace(",", " ").replace("[", "").replace("]", ",");
                String[] res = ret_1.split(", ");
                String[] tmp = res[0].split("  ");
                out = new String[res.length];
                for (int index = 0; index < res.length; index++) {
                    out[index] = res[index];
                    Log.d(""+ index, out[index]);
                }
            }
            else{
                out = new String[0];
                Log.d("length of SET OUT >>>>>>>>>>", out.length + "");
            }

            return null;
        }
    }

    class QueryAsyncTask extends AsyncTask<String, Void, String>{

        String retValue = null;
        String[] out = null;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(out.length == 0){
                Toast.makeText(getApplicationContext(), "没有对应数据，谢谢", Toast.LENGTH_SHORT).show();
            }
            else{
                Bundle tmp = new Bundle();
                for(int index = 0; index < out.length; index++){
                    tmp.putString(""+index, out[index]);
                }

                Intent intent = new Intent(MainScreen.this, ShowQueryRes.class);
                intent.putExtra("query", tmp);

                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "al_query";
            String soapAction = "http://" + params[0] + "/ALServer.php/al_query";

            SoapObject request = new SoapObject(nameSpace, methodName);
            // bind parameters here.
            request.addProperty("hashStr", hash);
            request.addProperty("alid", Alid);
            request.addProperty("flag", 99);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);

            HttpTransportSE se = new HttpTransportSE(url);
            try{
                se.call(soapAction, envelope);
            }
            catch (HttpResponseException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }

            SoapObject ret = (SoapObject) envelope.bodyIn;
            retValue = ret.getProperty("return").toString();
            Log.d("Query Debug>>>>>>>>>>>", retValue);
            if(!retValue.equals("[]")) {
                String ret_1 = retValue.replace("[[", "[").replace("]]", "]").replace(",", " ").replace("[", "").replace("]", ",");
                String[] res = ret_1.split(", ");
                String[] tmp = res[0].split("  ");
                out = new String[res.length];
                for (int index = 0; index < res.length; index++) {
                    out[index] = res[index];
                    Log.d(""+ index, out[index]);
                }
            }
            else{
                out = new String[0];
                Log.d("length of out >>>>>>>>>>", out.length + "");
            }

            return null;
        }
    }

    public String getCCType(String type){
        switch (type){
            case "0":
                return "外观洗车";
            case "1":
                return "标准洗车";
            default:
                return "未知类型";
        }
    }
}