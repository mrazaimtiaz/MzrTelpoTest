package com.example.mzrtelpotest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class DashboardIcon {
    final String text;
    final int imgId;
    final Class shortcut;

    DashboardIcon(int imgId, String text, Class shortcut) {
        super();
        this.imgId = imgId;
        this.text = text;
        this.shortcut = shortcut;
    }
}

class DashboardIconAdapter extends BaseAdapter {
    private final DashboardIcon[] mIcons;
    private Context mContext;

    DashboardIconAdapter(Context context, DashboardIcon[] icons) {
        mContext = context;
        mIcons = icons;
    }

    public int getCount() {
        return mIcons.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dashboard_icon, null);
        }

        TextView iconText = (TextView) convertView.findViewById(R.id.dashboard_icon_text);
        iconText.setText(mIcons[position].text);

        ImageView iconImg = (ImageView) convertView.findViewById(R.id.dashboard_icon_img);
        iconImg.setImageResource(mIcons[position].imgId);

        return convertView;
    }
}
