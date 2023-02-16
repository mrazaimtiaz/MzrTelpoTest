package com.example.mzrtelpotest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class SecurityCheckItem {
    final int fLabelId;
    final int fTextId;
    final int fImgId;
    final String fTime;

    private static Long sTotal;

    SecurityCheckItem(int labelId, int textId, int imgId, Long time) {
        super();
        fLabelId = labelId;
        fTextId = textId;
        fImgId = imgId;

        sTotal += time;
        fTime = String.format("(+%.3f)  %.3fs", time.floatValue()/1000, sTotal.floatValue()/1000);
    }

    public static void resetTotalTime() {
        sTotal = 0L;
    }
}

public class SecurityCheckItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SecurityCheckItem> mItemsList;

    private static class ViewHolder {
        public TextView label;
        public TextView text;
        public ImageView image;
        public TextView time;
    }

    SecurityCheckItemAdapter(Context context) {
        mContext = context;
        mItemsList = new ArrayList<SecurityCheckItem>();
    }

    public int getCount() {
        return mItemsList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    //public void updateList(SecurityCheckItem [] list) {
    //    mItemsList = list;
    //}

    public void addItem(SecurityCheckItem item) {
        mItemsList.add(item);
    }

    public void clear() {
        mItemsList.clear();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.securitycheck_item, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.securitycheck_item_label);
            viewHolder.text = (TextView) convertView.findViewById(R.id.securitycheck_item_text);
            viewHolder.time = (TextView) convertView.findViewById(R.id.securitycheck_item_time);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.securitycheck_item_img);
            convertView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) convertView.getTag();
        SecurityCheckItem sc = mItemsList.get(position);
        holder.label.setText(sc.fLabelId);
        holder.text.setText(sc.fTextId);
        holder.time.setText(sc.fTime);
        holder.image.setImageResource(sc.fImgId);

        return convertView;
    }
}
