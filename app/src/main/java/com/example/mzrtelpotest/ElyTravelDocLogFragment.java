package com.example.mzrtelpotest;

/**
 * Created by Raghunathan Madhu on 2/16/2017.
 */
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.elyctis.idtabsdk.utils.LogHtml;

import java.io.File;
import java.util.ArrayList;

public class ElyTravelDocLogFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public RadioGroup rgHtmlLogView;
    private WebView webHtmlLogView;
    TextView mHtmlLog;
    String logFolderPath = "/IdTabSuiteLog/";

    public ElyTravelDocLogFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ElyTravelDocLogFragment newInstance(int sectionNumber) {
        ElyTravelDocLogFragment fragment = new ElyTravelDocLogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_ely_travel_doc_log, container, false);
        rgHtmlLogView = (RadioGroup) rootView.findViewById(R.id.rgHtmlListFiles);
        mHtmlLog=(TextView)rootView.findViewById(R.id.htmlLog);
        webHtmlLogView = (WebView)rootView.findViewById(R.id.webHtmlLogView);


        rgHtmlLogView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                File file = null;
                int selectedRadioButtonID = rgHtmlLogView.getCheckedRadioButtonId();

                if (selectedRadioButtonID != -1) {
                    RadioButton selectedRadioButton = (RadioButton) rootView.findViewById(selectedRadioButtonID);
                    String selectedHtmlLogFile = selectedRadioButton.getText().toString();
                    try {
                        file= LogHtml.getHtmlLogFile(logFolderPath,selectedHtmlLogFile);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    webHtmlLogView.loadUrl("file:///" + (file.getPath()));
                    webHtmlLogView.setVisibility(View.VISIBLE);
                    webHtmlLogView.setBackgroundColor(Color.TRANSPARENT);
                    WebSettings settings = webHtmlLogView.getSettings();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        settings.setTextZoom((int)getResources().getDimension(R.dimen.web_view_zoom_size));
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<String> FileList=new ArrayList<String>();
        try {
            FileList=LogHtml.listHtmlLogFiles(logFolderPath);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if((FileList!=null) && (FileList.size() > 0)) {
            for (int i=0; i < FileList.size(); i++) {
                // Initialize a new RadioButton
                RadioButton rbHtmlLogView = new RadioButton(getActivity().getApplicationContext());
                // Set a Text for new RadioButton
                rbHtmlLogView.setText(FileList.get(i));

                // Set the text color of Radio Button
                rbHtmlLogView.setTextColor(Color.BLACK);

                rbHtmlLogView.setTextSize(getResources().getDimension(R.dimen.radio_group_button_size));

                rbHtmlLogView.setPadding(6, 6, 6, 6);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rbHtmlLogView.setButtonTintList(ColorStateList.valueOf(0xFF1430BE));
                }

                rgHtmlLogView.addView(rbHtmlLogView);
            }
            if(rgHtmlLogView.getChildCount() > 0 ) {
                webHtmlLogView.setVisibility(View.INVISIBLE);
                rgHtmlLogView.setVisibility(View.VISIBLE);
                mHtmlLog.setText("   Please choose the log file to open");
            }
        }
        else {
            mHtmlLog.setText("   ElyTravelDocument log files does not exists");
        }
    }
}
