/*
 * Copyright 2016-2017 Elyctis
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.example.mzrtelpotest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.elyctis.idtabsdk.scard.SCardHandle;
import com.elyctis.idtabsdk.scard.SCardReader;
import com.elyctis.idtabsdk.utils.HexUtil;
import com.elyctis.idtabsdk.utils.LogHtml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * - Displays the controls for the execution of the *.apdu script
 * - Provides option to choose the protocol to be used for the execution
 * - Provides button to choose the file to be used for the execution
 * - Provides button to execute the script file.
 */
public class ElySCardScriptFragment extends Fragment {

    private static final String TAG = "ElySCardScriptFragment";

    private Spinner sProtocol;
    private TextView tvSelectedFileName;
    private TextView tvSelectedReader;
    private Button btnChooseFile;
    private Button btnExecuteFile;

    private File mSelectedFile;
    private byte mSelectedProtocol;
    private SCardReader mScardReader = null;

    private static final String LOG_DIRECTORY = "/elyctis/apdulogs/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.elyscardreader_script, container, false);

        // Create references for the controls in this fragment
        sProtocol = (Spinner) view.findViewById(R.id.spinner_select_protocol);
        tvSelectedFileName = (TextView) view.findViewById(R.id.textView_selected_file);
        tvSelectedReader = (TextView) view.findViewById(R.id.textView_selected_reader);
        btnChooseFile = (Button) view.findViewById(R.id.button_browse);
        btnExecuteFile = (Button) view.findViewById(R.id.button_execute);

        // Enable onItemSelected listener for the sProtocol
        sProtocol.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        onItemSelectedSelectProtocol();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // do nothing
                    }
                }
        );

        // Enable onClick listener for the btnChooseFile
        btnChooseFile.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        onButtonClickedChooseFile();
                    }
                }
        );

        // Enable onClick listener for the btnExecuteFile
        btnExecuteFile.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        onButtonClickedExecuteFile();
                    }
                }
        );

        // Initialize the status of the UI controls in this fragment
        btnChooseFile.setEnabled(true);
        btnExecuteFile.setEnabled(false);

        return view;
    }

    /**
     * Gets the selected protocol from the script fragment.
     *
     * @return none
     */
    public void onItemSelectedSelectProtocol () {

        switch(sProtocol.getSelectedItem().toString()) {
            default:
            case "PROTOCOL_DEFAULT":
                mSelectedProtocol = SCardReader.PROTOCOL_ANY;
                break;
            case "PROTOCOL_T0":
                mSelectedProtocol = SCardReader.PROTOCOL_T0;
                break;
            case "PROTOCOL_T1":
                mSelectedProtocol = SCardReader.PROTOCOL_T1;
                break;
            case "PROTOCOL_UNDEFINED":
                mSelectedProtocol = SCardReader.PROTOCOL_RAW;
                break;
        }
    }

    /**
     * Displays the selected reader with which the commands will be processed.
     *
     * @param sReader Reader instance to be used
     *
     * @return none
     */
    public void displaySelectedSCardReader (SCardReader sReader) {
        try {
            if (sReader != null) {
                tvSelectedReader.setText("Selected reader: " + sReader.getReaderName());
            } else {
                tvSelectedReader.setText("No reader selected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the selected file name in the script fragment.
     *
     * @param strFile Name of the selected file
     *
     * @return none
     */
    public void displayFileInfo(String strFile) {
        tvSelectedFileName.setText("Selected file: " + strFile);
    }

    /**
     * Copy the file from source to destination.
     *
     * @param in input stream where the contents to be read
     * @param out output stream where the contents to be written
     *
     * @return none
     */
    private void copyFile (InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    /**
     * Copy the apdu files from the assets folder.
     *
     * @param parentDir Directory where the "apdu/*.apdu" need to be copied
     *
     * @return boolean (true if passed, false if failed)
     */
    //todo: check if copying the asset folders shall be taken to a separate class based on subsequent ID TAB SUITE implementation
    private boolean copyApduToDisk (String parentDir, String subFolder) {

        // Check if the assets exist
        AssetManager assetManager = (getActivity()).getAssets();
        String[] assetFiles;
        try {
            assetFiles = assetManager.list(subFolder);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
            return false;
        }

        // Check if the destination directory exists
        if(!(Environment.getExternalStorageState().equalsIgnoreCase("MOUNTED"))) {
            return false;
        }
        File destDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                parentDir + File.separator + subFolder);
        if (!destDir.exists())  {
            destDir.mkdirs();
            Log.d (TAG, File.separator + parentDir + File.separator + subFolder + " created");
        }

        try {
            if (assetFiles != null) for (String filename : assetFiles) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(subFolder + File.separator + filename);
                    File outFile = new File(destDir, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch(IOException e) {
                    Log.e(TAG, "Failed to copy asset file: " + filename, e);
                    return false;
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to close the source file", e);
                            return false;
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to close the destination file", e);
                            return false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Retrieves the array list of files (*.apdu) under the parentDir.
     *
     * @param parentDir Directory where the "apdu/*.apdu" exists
     *
     * @return ArrayList of files
     */
    private ArrayList<File> getListFiles(String parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();

        // Check if the storage is mounted
        if(!(Environment.getExternalStorageState().equalsIgnoreCase("MOUNTED"))) {
            try {
                ElyDialogFragment dlg = ElyDialogFragment.newInstance(
                        R.string.error_storage, R.string.error_storage_not_mounted,
                        android.R.drawable.stat_notify_error);
                dlg.show(getFragmentManager(), "dialog");
            } catch(Exception e) {
                e.printStackTrace();
            }
            Log.e (TAG, Environment.getExternalStorageDirectory().toString() + " is not mounted!");
            return null;
        }

        // Files on Internal Storage
        File dir = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + parentDir.toString() + File.separator + "apdu");
        if (!dir.exists()) {
            Log.d (TAG, "Directory with scripts does not exist, copying from assets folder");
            copyApduToDisk(parentDir, "apdu");
        }
        File file[] = dir.listFiles();
        if (0 == file.length) {
            Log.d (TAG, "No scripts, copying from assets folder");
            copyApduToDisk(parentDir, "apdu");
            file = dir.listFiles();
        }
        Log.d(TAG, "Size: " + file.length);

        for (int i=0; i < file.length; i++) {
            Log.d(TAG, "FileName:" + file[i].getName());

            if (file[i].getName().endsWith(".apdu")) {
                inFiles.add(file[i]);
            } else if (file[i].getName().endsWith(".APDU")) {
                inFiles.add(file[i]);
            }
        }
        return inFiles;
    }

    /**
     * Handles the "Choose file" button control and builds an alert builder with a list of available
     * *.apdu files.
     *
     * @return None
     */
    private void onButtonClickedChooseFile() {
        try {
            final ArrayList<File> files = getListFiles("elyctis"); //todo: identify folder name for common usage
            ArrayList<String> fileNames = new ArrayList<String>();
            CharSequence[] items;

            int size = files.size();
            for (int i = 0; i < size; i ++) {
                fileNames.add(files.get(i).getName());
            }

            items = fileNames.toArray(new CharSequence[files.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select a file");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    mSelectedFile = files.get(item);
                    Log.d(TAG, "Selected File: \"" + mSelectedFile + "\"");
                    dialog.dismiss();
                    displayFileInfo(mSelectedFile.getName());

                    mScardReader = ((ElySCardActivity)getActivity()).getSmartCardReader();
                    displaySelectedSCardReader (mScardReader);

                    if(mScardReader!=null)
                        btnExecuteFile.setEnabled(true);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Execute file" button control and executes the commands from the selected file.
     *
     * @return None
     */
    private void onButtonClickedExecuteFile() {
        List<ElySCardScriptCommand> cmdList = new ArrayList<>();

        try {

            // Get the commands from the selected apdu file
            cmdList.clear();
            cmdList.addAll(ElySCardScriptCommand.loadApduScript(mSelectedFile));
            if (cmdList.isEmpty()) {
                ElyDialogFragment dlg = ElyDialogFragment.newInstance(
                        R.string.error_file, R.string.error_file_empty,
                        android.R.drawable.stat_notify_error);
                dlg.show(getFragmentManager(), "dialog");
            }

            // Clear the log view in the ElySCardActivity
            ((ElySCardActivity)getActivity()).webClear();

            // Get the selected protocol from the fragment
            SCardHandle card = null;
            if (mSelectedProtocol != SCardReader.PROTOCOL_RAW) {
                card = mScardReader.connect(mSelectedProtocol);
            }

            // Send the commands one-by-one
            byte[] bRApdu;
            long startTime, elapsedTime;
            long lTotalExecutionTime = 0, lTotalFailed = 0;
            boolean bIsValidResponse;

            // Initialize html log
            LogHtml.open(LOG_DIRECTORY,
                    mSelectedFile.getName().substring(0, mSelectedFile.getName().indexOf('.')));

            for (int i = 0; i < cmdList.size(); i ++) {

                // Send the command to the reader and measure the time taken
                startTime = System.currentTimeMillis();
                if (card != null) {
                    bRApdu = card.transmit(HexUtil.hexStringToByteArray(cmdList.get(i).getCmdData()));
                } else {
                    bRApdu = mScardReader.control(HexUtil.hexStringToByteArray(cmdList.get(i).getCmdData()));
                }
                elapsedTime = (System.currentTimeMillis() - startTime);
                lTotalExecutionTime += elapsedTime;

                // Validate received response
                bIsValidResponse = ElySCardScriptCommand.isValidResponse(
                        HexUtil.toHexString(bRApdu,""), cmdList.get(i).getRespData());
                if (!bIsValidResponse) {
                    lTotalFailed += 1;
                }

                // Append to the html log
                LogHtml.logStep(cmdList.get(i).getCmdName());
                LogHtml.logCommandApdu(HexUtil.hexStringToByteArray(cmdList.get(i).getCmdData()));
                LogHtml.logResponseApdu(bRApdu);
                LogHtml.logTime("", Long.toString(elapsedTime));
                LogHtml.logSubStep(bIsValidResponse ? "Status: Pass" : "Status: Fail");
                LogHtml.logInfo("\n");
                LogHtml.logInfo("\n");
            }

            // Add the statistics
            LogHtml.logInfo("───────────────────────");
            LogHtml.logInfo("  Total number of commands : "
                    + Long.toString(cmdList.size()));
            LogHtml.logInfo("  Total commands passed : "
                    + Long.toString(cmdList.size() - lTotalFailed));
            LogHtml.logInfo("  Total commands failed : "
                    + Long.toString(lTotalFailed));
            LogHtml.logInfo("  Total execution time : "
                    + Long.toString(lTotalExecutionTime) + "ms");
            LogHtml.logInfo("───────────────────────");

            // Write html log
            LogHtml.close();

            List<String> listHtmlLogFiles = LogHtml.listHtmlLogFiles("/elyctis/apdulogs/");
            // Display the log in the ElySCardActivity
            ((ElySCardActivity)getActivity()).webLoad(
                    LOG_DIRECTORY + listHtmlLogFiles.get(0));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
