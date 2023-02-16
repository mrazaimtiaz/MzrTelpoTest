package com.example.mzrtelpotest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.elyctis.idtabsdk.scard.SCardReader;

import java.util.List;

public class ElySCardReaderAdapter extends BaseAdapter {

    private ElySCardReaderFragment mReaderFragment;
    List<SCardReader> mSCardReaderList;

    public ElySCardReaderAdapter(ElySCardReaderFragment readerFragment, List<SCardReader> readerList) {
        mReaderFragment = readerFragment;
        mSCardReaderList = readerList;
    }

    public int getCount() {
        return mSCardReaderList.size();
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
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.elyscardreader_item, null);
        }

        SCardReader reader = mSCardReaderList.get(position);

        TextView readerName = (TextView) convertView.findViewById(R.id.elyscardreader_name);
        readerName.setText(reader.getReaderName());

        TextView tvAtr = (TextView) convertView.findViewById(R.id.elyscardreader_atr);

        ToggleButton connect = (ToggleButton) convertView.findViewById(R.id.elyscardreader_connect);
        connect.setTag(R.id.reader, reader);
        connect.setTag(R.id.ref_atr, tvAtr);
        connect.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) mReaderFragment);

        return convertView;
    }
}
