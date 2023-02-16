package com.example.mzrtelpotest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.elyctis.idtabsdk.icao.Emrtd;
import com.elyctis.idtabsdk.icao.IcaoMrzParser;
import com.elyctis.idtabsdk.icao.KeyAuthentication;
import com.elyctis.idtabsdk.mrz.DataElement;
import com.elyctis.idtabsdk.mrz.MrzParser;
import com.elyctis.idtabsdk.mrz.MrzScanner;
import com.elyctis.idtabsdk.scard.SCardContext;
import com.elyctis.idtabsdk.scard.SCardException;
import com.elyctis.idtabsdk.scard.SCardHandle;
import com.elyctis.idtabsdk.scard.SCardReader;
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity;
import com.elyctis.idtabsdk.utils.FacialImage;
import com.elyctis.idtabsdk.utils.LogHtml;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class ElyTravelDocFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static final String TAG = "ElyTravelDocFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ElyTravelDocTask mTravelDocTask;
    private boolean mIsUsbPermissionGranted;
    private TextView mTvGivenName;
    private TextView mTvSurName;
    private TextView mTvDob;
    private TextView mTvGender;
    private TextView mTvNationality;
    private TextView mTvDocType;
    private TextView mTvDocNumber;
    private TextView mTvDoe;
    private TextView mTvIssuer;
    private TextView mTvOptional;
    private TextView mTvMrz;
    private ImageView mPhotoView;

    private TextView mTvInfoMsg;
    private FloatingActionButton mBtReadDocument;

    private ProgressBar mProgressBar;

    //private String mUserMrz = "";

    //private ListView mSCList;
    private SecurityCheckItemAdapter mSCAdapter;
    //private ArrayList<SecurityCheckItem> mSCList= new ArrayList<SecurityCheckItem>();

    public ElyTravelDocFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ElyTravelDocFragment newInstance(int sectionNumber) {
        ElyTravelDocFragment fragment = new ElyTravelDocFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get references to Textview
        View rootView = inflater.inflate(R.layout.fragment_ely_travel_doc, container, false);
        mTvGivenName = (TextView)rootView.findViewById(R.id.textGivenName);
        mTvSurName = (TextView)rootView.findViewById(R.id.textSurName);
        mTvDob = (TextView)rootView.findViewById(R.id.textDob);
        mTvGender = (TextView)rootView.findViewById(R.id.textGender);
        mTvNationality = (TextView)rootView.findViewById(R.id.textNationality);
        mTvDocType = (TextView)rootView.findViewById(R.id.textDocType);
        mTvDocNumber = (TextView)rootView.findViewById(R.id.textDocNumber);
        mTvDoe = (TextView)rootView.findViewById(R.id.textDoe);
        mTvIssuer = (TextView)rootView.findViewById(R.id.textIssuer);
        mTvOptional = (TextView)rootView.findViewById(R.id.textOptional);
        mTvMrz = (TextView)rootView.findViewById(R.id.textMrz);
        mTvMrz.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO display a dialog box to modify the MRZ
                // Parse the modify MRZ
                /*if (mTvMrz.getTag() == null) {
                    mTvMrz.setTag(USER_UI_UPDATE);
                    //mUserMrz = s.toString();
                } else if (mTvMrz.getTag().equals(PROG_UI_UPDATE)) {
                    mTvMrz.setTag(null);
                    //mUserMrz = "";
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {}
        });

        mPhotoView = (ImageView)rootView.findViewById(R.id.imageView1);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mSCAdapter = new SecurityCheckItemAdapter(this.getContext());
        ListView lv = (ListView)rootView.findViewById(R.id.securityCheckList);
        lv.setAdapter(mSCAdapter);

        mTvInfoMsg = (TextView)rootView.findViewById(R.id.textInfoMsg);
        mBtReadDocument = (FloatingActionButton) rootView.findViewById(R.id.buttonReadDocument);
        mBtReadDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((mTravelDocTask != null) && (AsyncTask.Status.RUNNING == mTravelDocTask.getStatus())) {
                    mTravelDocTask.cancel(true);
                }
                clearView();
                mTravelDocTask = new ElyTravelDocTask(((ElyTravelDocActivity)getActivity()).enableLog);
                mTravelDocTask.execute();
            }
        });
        UsbPermissionActivity usbPermissionActivity = new UsbPermissionActivity(getContext()," com.elyctis.idtabsuite");
        if(!usbPermissionActivity.isAllUsbDevicesAccessPermissionGranted()) {
            usbPermissionActivity.getUsbDeviceAccessPermission();
        }
        clearView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearView();
    }

    void clearView() {
        mTvMrz.setText("");
        mTvMrz.setFocusable(false);
        mTvMrz.setCursorVisible(false);
        mTvGivenName.setText("");
        mTvSurName.setText("");
        mTvDob.setText("");
        mTvGender.setText("");
        mTvNationality.setText("");
        mTvDocType.setText("");
        mTvDocNumber.setText("");
        mTvDoe.setText("");
        mTvIssuer.setText("");
        mTvOptional.setText("");
        mPhotoView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.person));

        // Clear list
        mSCAdapter.clear();
        mSCAdapter.notifyDataSetChanged();
    }

    /**
     * This is a private class for reading travel document using ID TAB SDK lib.
     *
     */
    private class ElyTravelDocTask extends AsyncTask<Void, SecurityCheckItem, Long> {

        private boolean mIsLogEnabled;
        //private boolean mIsTraceEnable = true;

        private Exception mException = null;
        private SCardReader mScReader = null;
        private SCardHandle mScHandle = null;
        private Emrtd mEmrtd = null;
        private HashMap<String, DataElement> mDocumentData = null;

        ElyTravelDocTask(boolean logEnable){
            mIsLogEnabled = logEnable;
        }

        protected void onProgressUpdate(SecurityCheckItem... progress) {
            mSCAdapter.addItem(progress[0]);
            mSCAdapter.notifyDataSetChanged();
        }

        protected void onCancelled(Long params) {
            // Check if an exception as occurred and display it
            if(mException != null) {

            } else {
                // Check warning or success
                //switch(mErrCode)
            }

            updateInfoMessage(R.string.info_default_msg);
            showProgressBar(false);
        }

        protected void onPostExecute(Long params) {
            // Check if an exception as occurred
            if(mException != null) {
                updateInfoMessage(R.string.info_err_msg);
            } else {
                // Check warning or success
                //switch(mErrCode)
                updateInfoMessage(R.string.info_default_msg);
            }
            showProgressBar(false);
        }


        @Override
        protected Long doInBackground(Void... params) {
            long startTime = 0;
            long elapsedSec = 0;

            mException = null;
            mScReader = null;
            mScHandle = null;
            mEmrtd = null;
            mDocumentData = null;

            SecurityCheckItem.resetTotalTime();

            // This task logs two levels: dev and (optionnaly) advanced APDU log through LogHtml
            LogHtml.setLogging(mIsLogEnabled);
            try {
                if (mIsLogEnabled)
                    LogHtml.open("/IdTabSuiteLog/", "IdTabSuite");

                LogHtml.logSubStep("Using version: " + BuildConfig.VERSION_NAME);

                // Run progress bar and measure overall time
                showProgressBar(true);
                updateInfoMessage(R.string.info_reading_msg);

                startTime = System.currentTimeMillis();
                // Read MRZ to get personal information
                String mrzLines = readMrz();
                /*mrzLines = "IDUTOWG30004036<<<<<<<<<<<<<<<\r" +
                    "6007078M0511014UTO<<<<<<<<<<<6\r" +
                    "ICHIRO<<GAIMU<<<<<<<<<<<<<<<<<";

                mrzLines = "IDFRA58RF022487<<<<<<<<<<<<<<<\r" +
                    "7307122F1603062FRA<<<<<<<<<<<8\r" +
                    "SPECIMEN<<NATACHA<<<<<<<<<<<<<";
                    */
                elapsedSec = System.currentTimeMillis() - startTime;
                Log.v(TAG, String.format("%1$s %2$d ms", "TIME - after readMrz", elapsedSec));
                LogHtml.logInfo(mrzLines);

                // Check not empty
                if (mrzLines.equals("\r\r\r")) {
                    publishProgress(new SecurityCheckItem(R.string.text_scan_mrz,
                            R.string.error_mrzscanner_nomrz,
                            R.drawable.wrong,
                            elapsedSec));
                    return -1L;
                }

                // Show MRZ
                publishProgress(new SecurityCheckItem(R.string.text_scan_mrz,
                        R.string.text_validated,
                        R.drawable.right,
                        elapsedSec));

                // Check if MRZ can be parsed
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                startTime = System.currentTimeMillis();
                boolean mrzValid = parseMrz(mrzLines, ssb);
                elapsedSec = System.currentTimeMillis() - startTime;
                if (!mrzValid) {
                    publishProgress(new SecurityCheckItem(R.string.text_parse_mrz_info,
                            R.string.error_mrzparser_failure,
                            R.drawable.wrong,
                            elapsedSec));
                    displayMrzLines(ssb);
                    return -1L;
                }

                // Retrieve and check MRZInfo key to access the chip
                DataElement dg_mrz_info = mDocumentData.get(IcaoMrzParser.KEY_MRZ_INFO);
                String mrz_info = mDocumentData.get(IcaoMrzParser.KEY_MRZ_INFO).getRawString();
                if(dg_mrz_info.getCrcStatus() != DataElement.CrcStatus.CHECKED) {
                    publishProgress(new SecurityCheckItem(R.string.text_parse_mrz_info,
                            R.string.text_invalid_incorrect_data,
                            R.drawable.warning,
                            elapsedSec));
                } else {
                    // Show MRZ
                    publishProgress(new SecurityCheckItem(R.string.text_parse_mrz_info,
                            R.string.text_validated,
                            R.drawable.right,
                            elapsedSec));
                }
                displayMrzLines(ssb);
                displayPersonalInfo(mDocumentData);

                // Select card
                if(!selectCard()) {
                    publishProgress(new SecurityCheckItem(R.string.text_establish_bac,
                            R.string.error_scard_select_failure,
                            R.drawable.wrong,
                            elapsedSec));
                    return -1L;
                }

                // TODO check if the card access is Plain Text

                // Access card
                startTime = System.currentTimeMillis();
                boolean accessGranted = accessCard(mrz_info);
                elapsedSec = System.currentTimeMillis() - startTime;
                Log.v(TAG, String.format("%1$s %2$d ms", "TIME - after accessCard", elapsedSec));
                LogHtml.logInfo("Card Access " + (accessGranted?"Granted":"Denied"));

                // Here BAC is force so no need to check for PACE
                if(!accessGranted) {
                    publishProgress(new SecurityCheckItem(R.string.text_establish_bac,
                            R.string.error_scard_access_denied,
                            R.drawable.wrong,
                            elapsedSec));
                    return -1L;
                }

                publishProgress(new SecurityCheckItem(R.string.text_establish_bac,
                        R.string.text_validated,
                        R.drawable.right,
                        elapsedSec));

                // Read DG1 and DG2
                startTime = System.currentTimeMillis();
                byte[] mDg1Content = mEmrtd.getDatagroup(Emrtd.Datagroup.DG1);
                elapsedSec = System.currentTimeMillis() - startTime;
                if(mDg1Content==null) {
                    publishProgress(new SecurityCheckItem(R.string.text_read_dg1,
                            R.string.error_scard_read,
                            R.drawable.wrong,
                            elapsedSec));
                    return -1L;
                }

                publishProgress(new SecurityCheckItem(R.string.text_read_dg1,
                        R.string.text_validated,
                        R.drawable.right,
                        elapsedSec));

                startTime = System.currentTimeMillis();
                byte[] mDg2Content = mEmrtd.getDatagroup(Emrtd.Datagroup.DG2);
                elapsedSec = System.currentTimeMillis() - startTime;
                if(mDg1Content==null) {
                    publishProgress(new SecurityCheckItem(R.string.text_read_dg2,
                            R.string.error_scard_read,
                            R.drawable.wrong,
                            elapsedSec));
                    return -1L;
                }

                publishProgress(new SecurityCheckItem(R.string.text_read_dg2,
                        R.string.text_validated,
                        R.drawable.right,
                        elapsedSec));

                // Decode DG2
                startTime = System.currentTimeMillis();
                Bitmap decodedImage = FacialImage.getFacialImage(mDg2Content);
                elapsedSec = System.currentTimeMillis() - startTime;
                if(decodedImage==null) {
                    publishProgress(new SecurityCheckItem(R.string.text_decode_facial_img,
                            R.string.error_decoding,
                            R.drawable.wrong,
                            elapsedSec));
                    return -1L;
                }

                publishProgress(new SecurityCheckItem(R.string.text_decode_facial_img,
                        R.string.text_validated,
                        R.drawable.right,
                        elapsedSec));
                setFacialImage(decodedImage);

            } catch (Exception exc) {
                // Handle error message in post
                mException = exc;
            } finally {
                // If card connection active
                releaseCardConnection();
                // Stop progress bar
                showProgressBar(false);
            }

            return 0L;
        }

        private String readMrz() throws Exception {
            Log.v(TAG, "Enter readMrz");
            String mrzLines = "";
            MrzScanner mrzScanner = new MrzScanner(getContext(), null);
            if (!mrzScanner.open())
                throw new Exception(getResources().getString(R.string.error_mrzscanner_conn));

            Log.v(TAG, "Inquire MRZ");
            mrzLines = mrzScanner.readMrz();
            mrzScanner.close();

            return mrzLines;
        }

        private boolean parseMrz(String mrzLines, SpannableStringBuilder ssb) {
            Log.v(TAG, "Enter parseMrz");
            try {
                MrzParser docParser = MrzParser.match(IcaoMrzParser.ICAO_MRZ_PARSERS, mrzLines);
                // Null if no match
                if (docParser == null) {
                    return false;
                }

                // Get the data element
                mDocumentData = docParser.parse(mrzLines);
                // Check each element in the original order
                for (String key : docParser.getMrzFormat().getKeys()) {
                    // KEY_SURNAME is the first extra DataElement and don't need to be displayed
                    if(key==IcaoMrzParser.KEY_SURNAME)
                       break;

                    DataElement.DataStatus dataStatus = mDocumentData.get(key).getStatus();
                    DataElement.CrcStatus crcStatus = mDocumentData.get(key).getCrcStatus();

                    if (mDocumentData.get(key).getStatus() == DataElement.DataStatus.EMPTY)
                        continue;

                    SpannableString sstr = new SpannableString(mDocumentData.get(key).getRawString());

                    if (crcStatus == DataElement.CrcStatus.CORRUPTED ||
                            dataStatus == DataElement.DataStatus.INCOMPLETE ||
                            dataStatus == DataElement.DataStatus.INVALID) {
                        sstr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                                R.color.colorRed)), 0, sstr.length(), 0);
                    } else if (crcStatus == DataElement.CrcStatus.CHECKED) {
                        sstr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                                R.color.colorGreen)), 0, sstr.length(), 0);
                    }
                    // Concat
                    ssb.append(sstr);
                }

                // Re-insert LR
                int iCR = mrzLines.indexOf('\r');
                while (iCR != -1) {
                    ssb.insert(iCR, "\r");
                    iCR = mrzLines.indexOf('\r', iCR + 1);
                }
            } catch (Exception exc) {
                Log.e(TAG, exc.getMessage());

                // Set all MRZ red
                SpannableString sstr = new SpannableString(mrzLines);
                sstr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),
                        R.color.colorRed)), 0, sstr.length(), 0);
                ssb.clearSpans();
                ssb.clear();
                ssb.append(sstr);
                return false;
            }
            return true;
        }

        private boolean selectCard() {
            Log.v(TAG, "Enter selectCard");
            try {
                SCardContext scContext = new SCardContext(getContext());
                SCardReader[] scReaderList = scContext.listReaders();
                for (int i = 0; i < scReaderList.length; ++i) {
                    if (scReaderList[i].getReaderName().equalsIgnoreCase("Elyctis CL Reader 0")) {
                        mScReader = scReaderList[i];
                        break;
                    }
                }

                if (mScReader == null)
                    throw new Exception(getResources().getString(R.string.error_scard_no_reader));

                // Return false is no card / no applet
                return mScReader.isCardPresent();
            } catch (Exception exc) {
                Log.e(TAG, exc.getMessage());
                //LogHtml.logInfo(e.getMessage());
                return false;
            }
        }

        private boolean accessCard(String mrz_info) {
            Log.v(TAG, "Enter accessCard");
            try {
                // Connect to the card and use the Emrtd class
                mScHandle = mScReader.connect(SCardReader.PROTOCOL_ANY);
                mEmrtd = new Emrtd(mScHandle);

                // TODO Replace setKey("MRZ" by setKey("MRZInfo"
                mEmrtd.setKeyAuthentication(new KeyAuthentication(KeyAuthentication.KeyType.MRZ_INFO, mrz_info));
                Log.v(TAG, "Select the E-Passport application");
                //mEpassportSelected = mEmrtd.selectApplication();
                mEmrtd.selectApplication();

                Log.v(TAG, "Do Access Control");
                return mEmrtd.doAccessControl();
            } catch (Exception exc) {
                Log.e(TAG, exc.getMessage());
                //LogHtml.logInfo(e.getMessage());
                return false;
            } finally {
                // TODO Clear outstanding connection
            }
        }


        private byte[] readDG1() {
            Log.v(TAG, "Enter readDG1");
            try {
                byte[] mDg1Content = mEmrtd.getDatagroup(Emrtd.Datagroup.DG1);
                return mDg1Content;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /*private byte[] readDG2(Emrtd emrtd) {
            Log.v(TAG, "Enter readDG2");
            try  {
                byte[] mDg2Content = emrtd.getDatagroup(DG2);

                if (mDg2Content != null) {
                    try {
                        if (mIsTraceEnable) {
                            elapsed = System.currentTimeMillis() - startTime;
                            Log.v("TIME - before getFacialImage", String.valueOf(elapsed));
                        }

                        //Bitmap decodedImage = FacialImage.getFacialImage(mDg2Content);
                        Bitmap decodedImage = getFacialImage(mDg2Content);
                        if (mIsTraceEnable) {
                            elapsed = System.currentTimeMillis() - startTime;
                            Log.v("TIME - after getFacialImage", String.valueOf(elapsed));
                        }
                        if (decodedImage != null) {
                            setFacialImage(decodedImage);
                        } else {
                            throw new Exception("Facial image is unrecognized ");
                        }
                    } catch (Exception e) {
                        throw new Exception("Facial image is unrecognized ");
                    }
                }

            } catch(Exception e) {
                //throw new Exception("Datagroup 2 has not been correctly read");
            } finally {

            }
            return null;
        }*/

        private void releaseCardConnection() {
            try {
                if (mScHandle != null) {
                    mScReader.disconnect();
                }
            } catch ( SCardException e) {
                e.printStackTrace();
            }
        }

        /*public DataElement.DataStatus getDocumentDataStatus(HashMap<String, DataElement> data) {
            if(!data.containsKey(IcaoMrzParser.KEY_MRZ_INFO))
                return DataElement.DataStatus.INCOMPLETE;

            for (String key : data.keySet()) {
                if(data.get(key).getStatus() == DataElement.DataStatus.INCOMPLETE) {
                    return DataElement.DataStatus.INCOMPLETE;
                }else if(data.get(key).getStatus() == DataElement.DataStatus.INVALID){
                    return DataElement.DataStatus.INVALID;
                }
            }
            return DataElement.DataStatus.VALID;
        }

        private String formatDate(String dateValue) {
            try {
                SimpleDateFormat fromUser = new SimpleDateFormat("yyMMdd");
                SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yy");
                return myFormat.format(fromUser.parse(dateValue));
            }catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }*/

        void updateInfoMessage(final int msg){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mTvInfoMsg.setText(msg);
                }
            });
        }

        void showProgressBar(final boolean status){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //mBtReadDocument.setEnabled(!status);
                    //mBtReadDocument.setClickable(!status);
                    if(!status) {
                        mBtReadDocument.show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }else{
                        mBtReadDocument.hide();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        void displayPersonalInfo(HashMap<String, DataElement> documentData) {
            final String givenNames = documentData.get(IcaoMrzParser.KEY_GIVEN_NAMES).getRefineString();
            final String surname = documentData.get(IcaoMrzParser.KEY_SURNAME).getRefineString();
            final String dob = documentData.get(IcaoMrzParser.KEY_DOB).getRefineString();
            final String sex = documentData.get(IcaoMrzParser.KEY_SEX).getRefineString();
            final String nationality = documentData.get(IcaoMrzParser.KEY_NATIONALITY).getRefineString();
            final String docType = documentData.get(IcaoMrzParser.KEY_DOC_CODE).getRefineString();
            final String docNumber = documentData.get(IcaoMrzParser.KEY_DOC_NUM).getRefineString();
            final String doe = documentData.get(IcaoMrzParser.KEY_DOE).getRefineString();
            final String issuer = documentData.get(IcaoMrzParser.KEY_ISSUING_STATE).getRefineString();
            final String optData = documentData.get(IcaoMrzParser.KEY_OPT_PERSONAL).getRefineString();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mTvGivenName.setText(givenNames);
                    mTvSurName.setText(surname);
                    mTvDob.setText(dob);
                    mTvGender.setText(sex);
                    mTvNationality.setText(nationality);
                    mTvDocType.setText(docType);
                    mTvDocNumber.setText(docNumber);
                    mTvDoe.setText(doe);
                    mTvIssuer.setText(issuer);
                    mTvOptional.setText(optData);

                    /*mTvGivenName.setText(mDocumentData.get(IcaoMrzParser.KEY_GIVENS_NAME).getRefineString());
                    mTvSurName.setText(mDocumentData.get(KEY_SIRNAME).getRefineString());
                    if(mDocumentData.get(IcaoMrzParser.KEY_DOB).getStatus() == DataElement.DataStatus.VALID) {
                        mTvDob.setText(
                                mInternationalFormat.format(mDocumentData.get(IcaoMrzParser.KEY_DOB).getTag())
                        );
                    } else { // Date could contains filler '<'
                        mTvDob.setText(mDocumentData.get(IcaoMrzParser.KEY_DOB).getRefineString());
                    }
                    mTvGender.setText(mDocumentData.get(IcaoMrzParser.KEY_SEX).getRefineString());
                    mTvNationality.setText(mDocumentData.get(IcaoMrzParser.KEY_NATIONALITY).getRefineString());
                    mTvDocType.setText(mDocumentData.get(IcaoMrzParser.KEY_DOC_CODE).getRefineString());
                    mTvDocNumber.setText(mDocumentData.get(IcaoMrzParser.KEY_DOC_NUM).getRefineString());
                    if(mDocumentData.get(IcaoMrzParser.KEY_DOB).getStatus() == DataElement.DataStatus.VALID) {
                        mTvDoe.setText(
                                mInternationalFormat.format(mDocumentData.get(IcaoMrzParser.KEY_DOE).getTag())
                        );
                    } else { // Date could contains filler '<'
                        mTvDoe.setText(mDocumentData.get(IcaoMrzParser.KEY_DOE).getRefineString());
                    }
                    mTvIssuer.setText(mDocumentData.get(IcaoMrzParser.KEY_ISSUING_STATE).getRefineString());*/
                }
            });
        }

        void displayMrzLines(final SpannableStringBuilder ssbMrzLines) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    /*String replMrzLine = mrzLines.replace("\r", "\n");
                    String[] newMrzLines = replMrzLine.split("_");
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    for (int index = 0; index < (newMrzLines.length); index++) {
                        SpannableString str1 = new SpannableString(newMrzLines[index]);
                        str1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str1.length(), 0);
                        builder.append(str1);
                        if (index != (newMrzLines.length - 1)) {
                            SpannableString str2 = new SpannableString("_");
                            str2.setSpan(new ForegroundColorSpan(Color.RED), 0, str2.length(), 0);
                            builder.append(str2);
                        }
                    }
                    mTvMrz.setFocusable(false);
                    mTvMrz.setCursorVisible(true);
                    mTvMrz.setFocusableInTouchMode(true);
                    mTvMrz.setClickable(true);*/
                    mTvMrz.setText(ssbMrzLines, TextView.BufferType.SPANNABLE);
                }
            });
        }

        void setFacialImage(final Bitmap decodedImage) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mPhotoView.setVisibility(View.VISIBLE);
                    mPhotoView.setImageBitmap(decodedImage);
                }
            });

        }

        /*void setSecurityCheckInfo(final CheckedTextView checkedTextView,final String Info,final int id){
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        checkedTextView.setText(Info);
                        checkedTextView.setCheckMarkDrawable(id);
                    }
                });
        }*/



        /**
         *
         * @param step
         * @param status
         * @param iResMessage
         */
        /*void setSecurityCheck(InspectionStep step, InspectionStatus status, int iResMessage){
            int labelResId = -1;
            int textResId = -1;
            int drawableResId = -1;

            switch(step) {
                case KEY:
                    labelResId = R.string.can;
                    break;
                case MRZ:
                    labelResId = R.string.mrz;
                    break;
                default:
                    break;
            }

            switch(status) {
                case SUCCESS:
                    textResId = R.string.status_success;
                    drawableResId = R.drawable.right;
                    break;
                case FAILURE:
                    textResId = R.string.status_failure;
                    drawableResId = R.drawable.wrong;
                    break;
                case CORRUPT:
                    textResId = R.string.status_corrupt;
                    drawableResId = R.drawable.wrong;
                    break;
                default:
                    textResId = R.string.status_not_found;
                    drawableResId = R.drawable.wrong;
                    break;
            }

            mInspectionStep.put(step, new SecurityCheckItem(
                    getString(labelResId), getString(textResId), drawableResId));
            publishProgress(UpdateStep.INSPECT);
        }*/
    }
}