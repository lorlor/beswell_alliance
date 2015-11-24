package com.beswell.beswell_al;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.MessageDigest;

/*
* 这是【阳光联盟】的入口Activity，通过在该页面上输入登录名及密码进行下一步操作。
* 如果验证正确，则进入内容操作；
* 如果验证失败，则通过Toast提示验证失败以进行重新验证。*/

public class LoginActivity extends Activity {

    static String IP = "192.168.0.101";

    private EditText username;
    private EditText passwd;
    private Button login;

    public String username_str;
    public String passwd_str;

    /**
     * ConnectivityManager和NetworkInfo的配合可以用来检查当前手机是否联网、联网状态以及网络类型。
     */
    ConnectivityManager cm;
    NetworkInfo ni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        /**
         * 下面两行添加的是友盟的两个API，用以监控App的运行情况，具体的内容可以到友盟的官网查看。
         * 这里所使用的AppKey，是我自己申请账号中反馈回来的，请记得修改此处的值。
         */
        AnalyticsConfig.setAppkey(this, "565265b367e58e8b83004709");
        AnalyticsConfig.setChannel("Beswell_alliance_site");

        username = (EditText)findViewById(R.id.et_login_username);
        passwd = (EditText)findViewById(R.id.et_login_passwd);
        login = (Button)findViewById(R.id.btn_login);
        cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_str = username.getText().toString();
                passwd_str = passwd.getText().toString();

                /**
                 * 判断用户是否输入了信息，以及手机是否联网
                 */

                if(username_str.equals("") || passwd_str.equals("")){
                    Toast.makeText(getApplicationContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }
                else {
                    // 用户输入【不】为空

                    if(cm.getActiveNetworkInfo() != null) {
                        ni = cm.getActiveNetworkInfo();
                        if (ni.isAvailable()) {
                            LoginAsyncTask lat = new LoginAsyncTask();
                            lat.execute(IP);
                        } else {
                            Toast.makeText(getApplicationContext(), "网络不可用，请检查网络是否连接", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "网络不可用，请检查网络是否连接", Toast.LENGTH_SHORT).show();
                    }
                }
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

    @Override
    /**
     * onKeyDown和DialogInterface.OnClickListener两个函数进行配合，
     * 用来在程序中处理返回事件，由于此处是登录界面，故在处在返回事件时弹出
     * 一个提示窗口告知用户将推出程序
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
            isExit.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", listener);
            // 显示对话框
            isExit.show();

        }

        return false;

    }
    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };


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

    /**
     * 所有的网络异步任务都需要在线程当中进行，同时在非UI线程中不能更改UI控件的内容。
     * 故此，程序中的所有涉及WebService的操作都自定义异步类继承自AsyncTask<>
     */
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
            retValue = ret.getProperty("return").toString();

            try {
                Log.d("Passwd==>", MD5(passwd_str));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * 对于输入的密码明文进行MD5加密
     */
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
