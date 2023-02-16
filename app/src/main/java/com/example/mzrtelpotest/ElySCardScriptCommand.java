package com.example.mzrtelpotest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements methods to load and validate the commands in an *.apdu script file.
 */
public class ElySCardScriptCommand {

    private static final String TAG = "ElySCardScriptCommand";
    final static String sRegExCData = "^[0-9A-Fa-f]+$";
    final static String sRegExRData = "^[0-9A-Fa-fxX]+$";

    private String mCName;
    private String mCData;
    private String mRData;

    public ElySCardScriptCommand(String cName, String cData, String rData) {
        mCName = cName;
        mCData = cData;
        mRData = rData;
    }

    public String getCmdName() { return mCName;}
    public String getCmdData() { return mCData;}
    public String getRespData() { return mRData;}

    /**
     * Read the commands from the selected file.
     *
     * @param file The selected file from where the commands to be loaded
     *
     * @return List of ElyScardScriptCommand type
     */
    public static List<ElySCardScriptCommand> loadApduScript(File file) throws IOException {
        final int MAX_CMDS = 1024;
        final String TAG_CMD = "#";
        final int ROW_CMD_NAME = 1;
        final int ROW_CMD_DATA = 2;
        final int ROW_RES_DATA = 3;
        String strError ="";
        List<ElySCardScriptCommand> cmdList = new ArrayList<>();

        try {
            long lCurrentLineNum = 0;       // Contains the line number of the file
            int nCurrentRow = ROW_CMD_NAME; // Contains the line number of the command (1 to 3)

            InputStream iStream = new FileInputStream(file);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(iStream));
            String strCName = "", strCData = "", strRData ="";
            String strCurrent;

            // Check end of file
            while ((strCurrent = bufReader.readLine()) != null) {

                lCurrentLineNum ++;

                // Get CName, CData and RData pertaining to a command

                // nCurrentRow cannot be more than ROW_RES_DATA
                if (nCurrentRow > ROW_RES_DATA) {
                    strError = buildError (file.getName(), "-", "-", "Unknown error");
                    break;
                }

                // If the current line is empty move to the next line
                if (0 == strCurrent.length()) {
                    continue;
                }

                // '#' indicates starting point of a command
                if (strCurrent.substring(0,1).equals(TAG_CMD)) {

                    // Case: When a command contains no response (possible with
                    // PROTOCOL_UNDEFINED case), store the current command and move to next command
                    if (ROW_RES_DATA == nCurrentRow) {
                        strRData = "";
                        nCurrentRow = ROW_CMD_NAME;
                        cmdList.add(new ElySCardScriptCommand(strCName, strCData, strRData));
                    }

                    // Case: When there is no command name
                    if (1 == strCurrent.length()) {
                        strCName += " -No command name-";
                    }

                    // Case: If the number of commands have reached the MAX_CMDS
                    if (MAX_CMDS == cmdList.size()) {
                        strError = buildError (file.getName(), "-", "-",
                                "Too many commands in the selected script");
                        break;
                    }

                    // Case: Store the command name
                    if (ROW_CMD_NAME == nCurrentRow) {
                        strCName = strCurrent;
                        nCurrentRow ++;
                    } else { // Case: TAG_CMD again in the next line (this is error)
                        strError = buildError (file.getName(), Long.toString(lCurrentLineNum),
                                strCName, "Previous command is incomplete");
                        break;
                    }
                }

                // Store the command data
                else if (ROW_CMD_DATA == nCurrentRow) {

                    if (isValidData (strCurrent, sRegExCData)) {
                        strCData = strCurrent;
                        nCurrentRow ++;
                    } else {
                        strError = buildError (file.getName(), Long.toString(lCurrentLineNum),
                                strCName, "Invalid command data");
                        break;
                    }

                }

                // Store the expected response data
                else if (ROW_RES_DATA == nCurrentRow) {

                    if (isValidData (strCurrent, sRegExRData)) {
                        strRData = strCurrent;
                        nCurrentRow = ROW_CMD_NAME;
                        cmdList.add(new ElySCardScriptCommand(strCName, strCData, strRData));
                    } else {
                        strError = buildError (file.getName(), Long.toString(lCurrentLineNum),
                                strCName, "Invalid expected response data");
                        break;
                    }
                }
            }

            if ((null == strCurrent) && (0 == cmdList.size())) {
                strError = buildError (file.getName(), "-", "-", "Empty file");
            }

            if (iStream != null) {
                iStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (strError != "") {
            Log.e(TAG, "ERROR: While loading the commands from the script");
            throw new InvalidParameterException(strError);
        }

        return cmdList;
    }

    /**
     * Validate the data.
     *
     * @param strData Data to be validated
     * @param strRegexPattern Regex pattern to be matched
     *
     * @return boolean (true if valid, false if invalid)
     */
    public static boolean isValidData (String strData, String strRegexPattern) {
        try {
            // Check if the length is a multiple of 2
            if ((strData.length() % 2) != 0) {
                Log.e(TAG, "ERROR: Length of the command is not even");
            } else {
                // Check if the data matches the regex pattern
                return strData.matches(strRegexPattern);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Compare expected response data with the actual received data
     *
     * @param strReceived Received response from the smartcard reader
     * @param strExcepted Expected response from the smartcard reader
     *
     * @return boolean (true if valid, false if invalid)
     */
    public static boolean isValidResponse (String strReceived, String strExcepted) {
        boolean bIsValid = true;
        try {
            // Change the strings to upper-case for comparison
            strReceived = strReceived.toUpperCase();
            strExcepted = strExcepted.toUpperCase();

            do {
                // If length of the response mismatches, return error
                if (strExcepted.length() != strReceived.length()) {
                    bIsValid = false;
                    break;
                }

                // If the hexadecimal pattern mismatches, return error
                if (false == isValidData (strReceived, sRegExCData)) {
                    bIsValid = false;
                    break;
                }

                for (int i = 0; i < strExcepted.length(); i ++) {

                    // Check if the character matches the expected character
                    if ((strExcepted.charAt(i) == 'X')
                            || (strExcepted.charAt(i) == strReceived.charAt(i))) {
                        continue;
                    }

                    // Otherwise mark error
                    else {
                        Log.e(TAG, "ERROR: In response data");
                        bIsValid = false;
                        break;
                    }
                }

            } while (false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bIsValid;
    }

    /**
     * Builds a string with an error message
     *
     * @param strFileName File name
     * @param strLineNum Line number
     * @param strCName Command name
     * @param strErrMsg Error message
     *
     * @return String (Error message)
     */
    private static String buildError (String strFileName, String strLineNum, String strCName,
                                      String strErrMsg) {
        String strError;
        strError = "File Name \t: " + strFileName + "\r\n";
        strError += "Line Number \t: " + strLineNum + "\r\n";
        strError += "Command Name \t: " + strCName + "\r\n";
        strError += "Error Detail \t: " + strErrMsg;
        return strError;
    }
}
