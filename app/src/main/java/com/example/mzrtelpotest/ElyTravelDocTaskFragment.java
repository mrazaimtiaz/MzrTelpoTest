package com.example.mzrtelpotest;

import static android.os.AsyncTask.Status.RUNNING;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.elyctis.idtabsdk.ElyTravelDocTask.ElyTravelDocTask;
import com.elyctis.idtabsdk.icao.IcaoMrzParser;
import com.elyctis.idtabsdk.mrz.DataElement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class ElyTravelDocTaskFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static final String TAG = "ElyTravelDocFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ElyTravelDocTask mTravelDocTask;

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

    private ElyTravelDocTask mElyTravelDocTask = null ;

    private String mMsg;
    private String mCan;
    private String mAccessControlKey;
    private byte[] mEfCardAccess;
    private byte[] mEfSod;
    private byte[] mEfCom;
    private byte[] mChipMrzData;
    private Bitmap mFacialImage;
    private byte[] mFacialFeature;
    private byte[] mFingerFeature;
    private byte[] mIrisFeature;
    private byte[] mPortraitFeature;
    private byte[] mReservedFeature;
    private byte[] mSignatureOrUsualMarkFeature;
    private byte[] mDataFeature;
    private byte[] mStructureFeature;
    private byte[] mSubstanceFeature;
    private byte[] mAddPersonalDetails;
    private byte[] mAddDocumentDeatils;
    private byte[] mOptionalDetails;
    private byte[] mSecurityOptions;
    private byte[] mActiveAuthPublicKeyInfo;
    private byte[] mPersonsToNotify;
    private HashMap<String, DataElement> mDocumentData;
    private boolean mIsPace;
    private boolean mIsBac;
    private boolean mIsPlain;
    private boolean mIsActiveAuth;
    private boolean mIsTerminalAuth;
    private boolean mIsChipAuth;
    private boolean mIsPassiveAuth;
    private String mMrzLines;
    private String mStrTime;


    public ElyTravelDocTaskFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ElyTravelDocTaskFragment newInstance(int sectionNumber) {
        ElyTravelDocTaskFragment fragment = new ElyTravelDocTaskFragment();
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
                if((mElyTravelDocTask != null) && (RUNNING != mElyTravelDocTask.getStatus())) {

                    mElyTravelDocTask.execute();

                    do {

                       // do nothing , Task is running
                    } while (RUNNING == mElyTravelDocTask.getStatus());
                }
            }
        });

        mElyTravelDocTask = new ElyTravelDocTask(getContext(), new ElyTravelDocTask.Listener() {

            @Override
            public void onDocReadStatusCallback(ElyTravelDocTask.InspectionStep iStep,ElyTravelDocTask.InspectionStatus iStatus, String message) {

                if(iStatus == com.elyctis.idtabsdk.ElyTravelDocTask.ElyTravelDocTask.InspectionStatus.SUCCESS) {

                    switch(iStep)
                    {
                        case START :
                        {
                            mMsg = message;
                            break;
                        }
                        case KEY :
                        {
                            mMsg = message;
                            mAccessControlKey = mElyTravelDocTask.getAccessControlKey();
                            break;
                        }
                        case PLAIN :
                        {
                            mMsg = message;
                            mIsPlain = mElyTravelDocTask.isPlainMrtd();
                            break;
                        }
                        case BAC :
                        {
                            mMsg = message;
                            mIsBac = mElyTravelDocTask.isBacMrtd();
                            break;
                        }
                        case PACE :
                        {
                            mMsg = message;
                            mIsPace = mElyTravelDocTask.isPaceMrtd();
                            break;
                        }
                        case ACTIVE_AUTH :
                        {
                            mMsg = message;
                            mIsActiveAuth = mElyTravelDocTask.getActiveAuthStatus();
                            break;
                        }
                        case CHIP_AUTH:
                        {
                            mMsg = message;
                            mIsChipAuth = mElyTravelDocTask.getChipAuthStatus();
                            break;
                        }
                        case TERMINAL_AUTH:
                        {
                            mMsg = message;
                            mIsTerminalAuth = mElyTravelDocTask.getTerminalAuthStatus();
                            break;
                        }
                        case PASSIVE_AUTH:
                        {
                            mMsg = message;
                            mIsPassiveAuth = mElyTravelDocTask.getPassiveAuthStatus();
                            break;
                        }
                        case EF_CARDACCESS:
                        {
                            mMsg = message;
                            mEfCardAccess = mElyTravelDocTask.getEfCardAccess();
                            break;
                        }
                        case EF_COM:
                        {
                            mMsg = message;
                            mEfCom = mElyTravelDocTask.getEfCom();
                            // Assert.assertArrayEquals("The EfCom values are not matched", EmrtdTestValues.efComContentM12,mEfCom);
                            break;
                        }
                        case EF_SOD:
                        {
                            mMsg = message;
                            mEfSod = mElyTravelDocTask.getEfSod();
                            // Assert.assertArrayEquals("The EfCom values are not matched", EmrtdTestValues.efSodContentM12,mEfSod);
                            break;
                        }
                        case MRZ_LINES:
                        {
                            mMsg = message;
                            mMrzLines= mElyTravelDocTask.getMrzLines();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mTvMrz.setText(mMrzLines, TextView.BufferType.SPANNABLE);
                                }
                            });
                            break;
                        }
                        case MRZ_INFO:
                        {
                            mMsg = message;
                            mDocumentData = mElyTravelDocTask.getDocumentInfo();
                            final String givenName = mDocumentData.get(IcaoMrzParser.KEY_GIVEN_NAMES).getRawString();
                            final String surname= mDocumentData.get(IcaoMrzParser.KEY_SURNAME).getRawString();
                            final String dob =mDocumentData.get(IcaoMrzParser.KEY_DOB).getRawString();
                            final String sex= mDocumentData.get(IcaoMrzParser.KEY_SEX).getRawString();
                            final String nationality= mDocumentData.get(IcaoMrzParser.KEY_NATIONALITY).getRawString();
                            final String docCode= mDocumentData.get(IcaoMrzParser.KEY_DOC_CODE).getRawString();
                            final String docNum= mDocumentData.get(IcaoMrzParser.KEY_DOC_NUM).getRawString();
                            final String doe= mDocumentData.get(IcaoMrzParser.KEY_DOE).getRawString();
                            final String issueState =mDocumentData.get(IcaoMrzParser.KEY_ISSUING_STATE).getRawString();
                            final String optionalData= mDocumentData.get(IcaoMrzParser.KEY_OPT_PERSONAL).getRawString();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mTvGivenName.setText(givenName);
                                    mTvSurName.setText(surname);
                                    mTvDob.setText(dob);
                                    mTvGender.setText(sex);
                                    mTvNationality.setText(nationality);
                                    mTvDocType.setText(docCode);
                                    mTvDocNumber.setText(docNum);
                                    mTvDoe.setText(doe);
                                    mTvIssuer.setText(issueState);
                                   mTvOptional.setText(optionalData);
                                }
                            });
                            break;
                        }
                        case DG1:
                        {
                            mMsg = message;
                            mChipMrzData =  mElyTravelDocTask.getChipMrzData();
                            // Assert.assertArrayEquals("The Dg1 values are not matched", EmrtdTestValues.dg1ContentM12,mChipMrzData);
                            break;
                        }
                        case DG2:
                        {
                            mMsg = message;
                            mFacialImage = mElyTravelDocTask.getFacialImage();
                            mFacialFeature = mElyTravelDocTask.getFacialFeature();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mPhotoView.setVisibility(View.VISIBLE);
                                    mPhotoView.setImageBitmap(mFacialImage);
                                }
                            });
                            break;
                        }
                        case DG3:
                        {
                            mMsg = message;
                            mFingerFeature = mElyTravelDocTask.getFingerFeature();
                            //Assert.assertArrayEquals("The Dg3 values are not matched", EmrtdTestValues.dg3ContentM12,mFingerFeature);
                            break;
                        }
                        case DG4:
                        {
                            mMsg = message;
                            mIrisFeature = mElyTravelDocTask.getIrisFeature();
                            break;
                        }
                        case DG5:
                        {
                            mMsg = message;
                            mPortraitFeature= mElyTravelDocTask.getPortraitFeature();
                            break;
                        }
                        case DG6:
                        {
                            mMsg = message;
                            mReservedFeature = mElyTravelDocTask.getReservedDataIfAvailable();
                            break;
                        }
                        case DG7:
                        {
                            mMsg = message;
                            mSignatureOrUsualMarkFeature = mElyTravelDocTask.getSignatureOrUsualMarkFeature();
                            // Assert.assertArrayEquals("The Dg7 values are not matched", EmrtdTestValues.dg7ContentM12,mSignatureOrUsualMarkFeature);
                            break;
                        }
                        case DG8:
                        {
                            mMsg = message;
                            mDataFeature = mElyTravelDocTask.getDataFeature();
                            break;
                        }
                        case DG9:
                        {
                            mMsg = message;
                            mStructureFeature = mElyTravelDocTask.getStructureFeature();
                            break;
                        }
                        case DG10:
                        {
                            mMsg = message;
                            mSubstanceFeature = mElyTravelDocTask.getSubstanceFeature();
                            break;
                        }
                        case DG11:
                        {
                            mMsg = message;
                            mAddPersonalDetails = mElyTravelDocTask.getAdditionalPersonalDetails();
                            break;
                        }
                        case DG12:
                        {
                            mMsg = message;
                            mAddDocumentDeatils = mElyTravelDocTask.getAdditionalDocumentDetails();
                            break;
                        }
                        case DG13:
                        {
                            mMsg = message;
                            mOptionalDetails = mElyTravelDocTask.getOptionalDetails();
                            break;
                        }
                        case DG14:
                        {
                            mMsg = message;
                            mSecurityOptions = mElyTravelDocTask.getSecurityOptions();
                            break;
                        }
                        case DG15:
                        {
                            mMsg = message;
                            mActiveAuthPublicKeyInfo = mElyTravelDocTask.getActiveAuthenticationPublicKeyInfo();
                            //Assert.assertArrayEquals("The Dg5 values are not matched", EmrtdTestValues.dg15ContentM12,mActiveAuthPublicKeyInfo);
                            break;
                        }
                        case DG16:
                        {
                            mMsg = message;
                            mPersonsToNotify = mElyTravelDocTask.getPersonsToNotify();
                            break;
                        }
                        case END:
                        {
                            mMsg = message;
                            break;
                        }
                        default:
                        {
                            mMsg = message;
                            break;
                        }
                    }
                } else {
                    // Inspection failure  cases
                    mMsg = message;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mTvInfoMsg.setText(mMsg);
                    }
                });
            }
        });

        return rootView;
    }

    protected void onProgressUpdate(SecurityCheckItem... progress) {
        mSCAdapter.addItem(progress[0]);
        mSCAdapter.notifyDataSetChanged();
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
}