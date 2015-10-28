package com.beswell.beswell_al;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by beswell10 on 2015/10/12.
 */
public class ListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String[]> data;
    private LayoutInflater li;

    public ListViewAdapter(Context context, List<String[]> data){
        this.mContext = context;
        this.data = data;
        li = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class Items{
        public TextView payInfo;
        public TextView chargeTime;
        public TextView plate;
        public TextView counts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Items total = null;
        if(convertView == null){
            total = new Items();
            convertView = li.inflate(R.layout.list_item, null);
            total.payInfo = (TextView)convertView.findViewById(R.id.payInfo);
            total.chargeTime = (TextView)convertView.findViewById(R.id.chargeTime);
            total.plate = (TextView)convertView.findViewById(R.id.plate);
            total.counts = (TextView)convertView.findViewById(R.id.counts);
            convertView.setTag(total);
        }
        else{
            total = (Items)convertView.getTag();
        }

        total.payInfo.setText("到账通知");
        total.payInfo.setTextSize(25);
        total.payInfo.setGravity(View.TEXT_ALIGNMENT_CENTER);
        total.payInfo.setTypeface(Typeface.MONOSPACE, 1);
        total.chargeTime.setText("付款时间：" + data.get(position)[0]);
        total.chargeTime.setTextSize(15);
        total.plate.setText("车牌号：" + data.get(position)[1]);
        total.plate.setTextSize(15);
        total.counts.setText(getCCType(data.get(position)[2]) + data.get(position)[3] + "次");
        total.counts.setTextSize(15);

        return convertView;
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
