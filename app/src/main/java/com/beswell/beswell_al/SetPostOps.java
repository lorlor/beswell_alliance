package com.beswell.beswell_al;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;


public class SetPostOps extends Activity {

    String IP = "192.168.0.100";

    public EditText shopname;
    public EditText contactperson;
    public EditText contactphone;
    public EditText shoploc;
    public EditText cca;
    public EditText cca2;
    public EditText ccb;
    public EditText st;
    public Button submit;

    public Intent intent;
    public LocationManager lm;

    String tmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_set_post_ops);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_postset);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> provider = lm.getProviders(true);

        if(provider.contains(LocationManager.NETWORK_PROVIDER)){
            String net_provider = LocationManager.NETWORK_PROVIDER;
            Log.d("Net Provider", net_provider);
        }
        else if(provider.contains(LocationManager.GPS_PROVIDER)){
            String gps_provider = LocationManager.GPS_PROVIDER;
            Log.d("GPS Provider", gps_provider);
        }
        else{
            Log.d("No Provider", "------------------------------");
        }

        shopname = (EditText)findViewById(R.id.set_shopname);
        contactperson = (EditText)findViewById(R.id.set_contactperson);
        contactphone = (EditText)findViewById(R.id.set_contactphone);
        shoploc = (EditText)findViewById(R.id.set_shoploc);
        cca = (EditText)findViewById(R.id.set_cca);
        cca2 = (EditText)findViewById(R.id.set_cca2);
        ccb = (EditText)findViewById(R.id.set_ccb);
        st = (EditText)findViewById(R.id.set_st);

        submit = (Button)findViewById(R.id.set_confirm);

        intent = getIntent();
        Bundle data = intent.getBundleExtra("set_res");
        tmp = data.getString("0").replace(",", "");
        Log.d("SetPost Debug", tmp);
        setContent();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPOAsyncTask sat = new SPOAsyncTask();
                sat.execute(IP);
            }
        });
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_post_ops, menu);
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

    class SPOAsyncTask extends AsyncTask<String, Void, String>{

        String retValue = null;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(retValue.equals("Success")){
                Toast.makeText(getApplicationContext(), "申请成功，请等待审核", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "al_submit";
            String soapAction = "http://" + params[0] + "/ALServer.php/al_submit";

            String hash = intent.getStringExtra("hash");
            String Alid = intent.getStringExtra("Alid");

            String shopname_str= shopname.getText().toString();
            String contactperson_str = contactperson.getText().toString();
            String contactphone_str = contactphone.getText().toString();
            String shoploc_str = shoploc.getText().toString();
            int cca_i = Integer.parseInt(cca.getText().toString());
            int cca2_i = Integer.parseInt(cca2.getText().toString());
            int ccb_i = Integer.parseInt(ccb.getText().toString());
            String st_str = st.getText().toString();

            SoapObject request = new SoapObject(nameSpace, methodName);
            // bind parameters here.
            request.addProperty("hashStr", hash);
            request.addProperty("alid", Alid);
            request.addProperty("shopName", shopname_str);
            request.addProperty("shopLoc", shoploc_str);
            request.addProperty("ccaCost", cca_i);
            request.addProperty("cca2Cost", cca2_i);
            request.addProperty("ccbCost", ccb_i);
            request.addProperty("contact", contactperson_str);
            request.addProperty("phone", contactphone_str);
            request.addProperty(":L", 0);
            request.addProperty(":D", 0);

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

            return null;
        }
    }

    public void setContent(){
        String[] elems = tmp.split("  ");
        shopname.setText(elems[0]);
        contactperson.setText(elems[1]);
        contactphone.setText(elems[2]);
        shoploc.setText(elems[3]);
        cca.setText(elems[4]);
        cca2.setText(elems[5]);
        ccb.setText(elems[6]);
        st.setText(getState(elems[7]));
    }

    public String getState(String type){
        switch (type){
            case "0":
                return "未审核";
            case "1":
                return "已审核";
            default:
                return "未知状态";
        }
    }
}
