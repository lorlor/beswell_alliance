package com.beswell.beswell_al;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.MessageDigest;


public class LoginActivity extends ActionBarActivity {

    static String IP = "192.168.0.101";

    private EditText username;
    private EditText passwd;
    private Button login;

    public TextView tv;

    public String username_str;
    public String passwd_str;

    ConnectivityManager cm;
    NetworkInfo ni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.AlLoginUsername);
        passwd = (EditText)findViewById(R.id.AlLoginPasswd);
        login = (Button)findViewById(R.id.AlLoginBtn);
        cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();

        tv = new TextView(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_str = username.getText().toString();
                passwd_str = passwd.getText().toString();

                if(username_str.equals("") || passwd_str.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (ni.isAvailable()) {
                        LoginAsyncTask lat = new LoginAsyncTask();
                        lat.execute(IP);
                    } else {
                        Toast.makeText(getApplicationContext(), "网络不可用，请检查网络是否连接", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    class LoginAsyncTask extends AsyncTask<String, Void, String>{

        String retValue = null;
        int stCode = -1;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(retValue.equals("False")){
                Toast.makeText(getApplicationContext(), "用户名或密码不正确，请重试!", Toast.LENGTH_SHORT).show();
            }
            else if(retValue.equals("[]")){

                Toast.makeText(getApplicationContext(), "网络问题，请重试", Toast.LENGTH_SHORT).show();
            }
            else{
                String[] parts = retValue.split("\\.");
                String alid = parts[0];
                String hashStr = parts[1];

                Intent intent = new Intent(LoginActivity.this, MainScreen.class);
                intent.putExtra("Alid", alid);
                intent.putExtra("hash", hashStr);
                startActivity(intent);
                finish();
             }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "al_login";
            String soapAction = "http://" + params[0] + "/ALServer.php/al_login";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("userCode", username_str);
            try {
                request.addProperty("passwd", MD5(passwd_str));
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.addProperty("openid", "");
            request.addProperty("ip", "192.168.0.54");
            request.addProperty("agent", "");

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
            try {
                retValue = ret.getProperty("return").toString();
            }
            catch (NullPointerException e){
                Toast.makeText(getApplicationContext(), "网络错误，请重试", Toast.LENGTH_SHORT).show();
            }
            try {
                Log.d("Passwd==>", MD5(passwd_str));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static String MD5(String input) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = input.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
