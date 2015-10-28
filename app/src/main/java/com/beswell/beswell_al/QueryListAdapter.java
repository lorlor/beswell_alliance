package com.beswell.beswell_al;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by beswell10 on 2015/10/22.
 */
public class QueryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String[]> data;
    private LayoutInflater li;

    public QueryListAdapter(Context context, List<String[]> data){
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

    public class Items{
        public TextView id;
        public TextView cdt;
        public TextView cardcode;
        public TextView cctype;
        public TextView cost;
        public TextView plate;
        public TextView st;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Items total = null;
        if(convertView == null){
            total = new Items();
            convertView = li.inflate(R.layout.query_res, null);
            total.id = (TextView)convertView.findViewById(R.id.query_id);
            total.cdt = (TextView)convertView.findViewById(R.id.query_cdt);
            total.cardcode = (TextView)convertView.findViewById(R.id.query_cardcode);
            total.cctype = (TextView)convertView.findViewById(R.id.query_cctype);
            total.cost = (TextView)convertView.findViewById(R.id.query_cost);
            total.plate = (TextView)convertView.findViewById(R.id.query_plate);
            total.st = (TextView)convertView.findViewById(R.id.query_st);
            convertView.setTag(total);
        }
        else{
            total = (Items)convertView.getTag();
        }

        total.id.setText("序号：" + (position + 1));
        total.id.setTextSize(25);
        total.id.setGravity(View.TEXT_ALIGNMENT_CENTER);
        total.id.setTypeface(Typeface.MONOSPACE, 1);
        total.cdt.setText("时间：" + data.get(position)[0]);
        total.cdt.setTextSize(15);
        total.plate.setText("主车牌：" + data.get(position)[4]);
        total.plate.setTextSize(15);
        total.cardcode.setText("卡号：" + data.get(position)[1]);
        total.cardcode.setTextSize(15);
        total.cctype.setText("类型：" + getCCType(data.get(position)[2]));
        total.cctype.setTextSize(15);
        total.cost.setText("金额：" + data.get(position)[3]);
        total.cost.setTextSize(15);
        total.st.setText("状态：" + getState(data.get(position)[5]));
        total.st.setTextSize(15);

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
