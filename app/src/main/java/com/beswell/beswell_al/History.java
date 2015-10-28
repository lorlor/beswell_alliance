package com.beswell.beswell_al;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by beswell10 on 2015/10/19.
 */
public class History extends DialogFragment {

    String IP = "192.168.0.101";

    Bundle records;

    DatePicker st;
    DatePicker et;

    Button query;
    Button back;

    String st_str;
    String et_str;

    String out;

    int stCode;
    int rsCount;

    String hash;
    String AlID;

    public static History newInstance(String input, String alid){
        History rcf = new History();
        Bundle b = new Bundle();
        b.putString("hash", input);
        b.putString("Alid", alid);
        rcf.setArguments(b);

        return rcf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.fragment_history, null);

        st = (DatePicker)v.findViewById(R.id.hist_startTime);
        et = (DatePicker)v.findViewById(R.id.hist_endTime);

        query = (Button)v.findViewById(R.id.hist_query);
        back = (Button)v.findViewById(R.id.hist_back);

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int st_y = st.getYear();
                int st_m = st.getMonth() + 1;
                int st_d = st.getDayOfMonth();
                st_str = st_y + "-" + st_m + "-" + st_d;

                int et_y = et.getYear();
                int et_m = st.getMonth() + 1;
                int et_d = et.getDayOfMonth();
                et_str = et_y + "-" + et_m + "-" + et_d;
                boolean flag = false;
                if(st_y > et_y){
//                    System.out.println("Invalid date");
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("起始日期应早于或等于截止日期").create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
                else if(st_y == et_y){
                    if(st_m > et_m){
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setMessage("起始日期应早于或等于截止日期").create();
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                    }
                    else if(st_m == et_m){
                        if(st_d <= et_d){
                            st_str = st_y + "-" + st_m + "-" + st_d;
                            et_str = et_y + "-" + et_m + "-" + et_d;
                            flag =true;
                        }
                        else{
                            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setMessage("起始日期应早于或等于截止日期").create();
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    }
                    else{
                        st_str = st_y + "-" + st_m + "-" + st_d;
                        et_str = et_y + "-" + et_m + "-" + et_d;
                        flag =true;
                    }
                }
                else{
                    st_str = st_y + "-" + st_m + "-" + st_d;
                    et_str = et_y + "-" + et_m + "-" + et_d;
                    flag =true;
                }
                if(flag) {
                    UserAsyncTask uat = new UserAsyncTask();
                    uat.execute(IP);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("历史记录查询")
                .setView(v);

        return builder.create();
    }

    class UserAsyncTask extends AsyncTask<String, Void, String>{

        String retValue = null;
        String[] out = null;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(out.length == 0){
                Toast.makeText(getActivity().getApplicationContext(), "没有对应数据，谢谢", Toast.LENGTH_SHORT).show();
            }
            else{
                Bundle tmp = new Bundle();
                for(int index = 0; index < out.length; index++){
                    tmp.putString(""+index, out[index]);
                }

                Intent intent = new Intent(getActivity(), ShowHistRes.class);
                intent.putExtra("Result", tmp);

                startActivity(intent);
                getDialog().dismiss();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/ALServer.php";
            String methodName = "al_queryCCRecordByDate";
            String soapAction = "http://" + params[0] + "/ALServer.php/al_queryCCRecordByDate";

            records = new Bundle();
            hash = getArguments().getString("hash");
            AlID = getArguments().getString("Alid");

            SoapObject request = new SoapObject(nameSpace, methodName);

            request.addProperty("hashStr", hash);
            request.addProperty("alid", AlID);
            request.addProperty("startTime", st_str);
            request.addProperty("endTime", et_str);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);


            HttpTransportSE httpTransport = new HttpTransportSE(url);

            httpTransport.debug = true;
            try {
                httpTransport.call(soapAction, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //send request

            SoapObject result = (SoapObject) envelope.bodyIn;
            retValue = result.getProperty("return").toString();
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
            Log.d("Edub >>>>>>>", retValue);

            return null;
        }
    }
}
