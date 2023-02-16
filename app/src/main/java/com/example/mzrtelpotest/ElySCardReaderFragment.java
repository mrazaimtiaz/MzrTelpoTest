package com.example.mzrtelpotest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.elyctis.idtabsdk.scard.SCardContext;
import com.elyctis.idtabsdk.scard.SCardHandle;
import com.elyctis.idtabsdk.scard.SCardReader;
import com.elyctis.idtabsdk.utils.HexUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * - Lists the smartcard readers
 * - Provides buttons to connect/disconnect the smartcard
 * - Displays the ATR of the connected smartcard
 */
public class ElySCardReaderFragment extends Fragment implements
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ElySCardReaderFragment";

    private SCardContext mSCardContext;
    private List<SCardReader> mSCardReaderList;

    private ElySCardReaderAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.elyscardreader_item_list, container, false);

        mSCardContext = new SCardContext(getActivity());
        mSCardReaderList = new ArrayList<SCardReader>();
        ListView lvSCardReader = (ListView)(view.findViewById(R.id.reader_list));

        mAdapter = new ElySCardReaderAdapter(this, mSCardReaderList);
        lvSCardReader.setAdapter(mAdapter);
        lvSCardReader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SCardReader reader = (SCardReader) buttonView.getTag(R.id.reader);
        TextView tvAtr = (TextView) buttonView.getTag(R.id.ref_atr);
        String atr = "";
        if(buttonView.isChecked()) {
            atr = connectCard(reader);
            if (atr != "") {
                tvAtr.setText(atr);
                ((ElySCardActivity)getActivity()).setSmartCardReader(reader);
            } else {
                buttonView.setChecked(false);
            }
        }

        if (atr == "") {
            disconnectCard(reader);
            tvAtr.setText(R.string.text_no_card_connected);
            // This should not be required but used to bypass a bug on second connection
            // TODO: Fix the bug
            refreshReaderList();
        }
    }

    public int refreshReaderList() {
        if(mSCardContext == null) {
            return 0;
        }

        mSCardReaderList.clear();
        mSCardReaderList.addAll(Arrays.asList(mSCardContext.listReaders()));
        mAdapter.notifyDataSetChanged();

        return mSCardReaderList.size();
    }

    public String connectCard(SCardReader reader) {
        String atr = "";
        try {
            SCardHandle card = reader.connect((byte) SCardReader.PROTOCOL_T1);
            if(card!=null) {

                atr = HexUtil.toHexString(card.getAtr(), "");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return atr;
    }

    public void disconnectCard(SCardReader reader) {
        try {
            reader.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
