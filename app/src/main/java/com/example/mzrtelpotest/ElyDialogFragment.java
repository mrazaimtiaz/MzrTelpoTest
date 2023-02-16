package com.example.mzrtelpotest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 */

public class ElyDialogFragment extends DialogFragment {

    public interface ElyDialogResponseHandler {
        public void doPositiveClick();
        public void doNegativeClick();
    }

    /**
     * ActionButton enumerates the possible option.
     */
    public enum ActionButton {
        OK,
        OK_CANCEL
    }

    public static ElyDialogFragment newInstance(int title, int message, int icon) {
        return newInstance(title, message, icon, ActionButton.OK);
    }

    public static ElyDialogFragment newInstance(int title, int message, int icon, ActionButton ab) {
        ElyDialogFragment fragment = new ElyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        args.putInt("icon", icon);
        args.putString("action", ab.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, int title, int message, int icon) {
        show(fm, title, message, icon, ActionButton.OK);
    }

    public static void show(FragmentManager fm, int title, int message, int icon, ActionButton ab) {
        // Dismiss previous dialog
        FragmentTransaction ft = fm.beginTransaction();
        // This looks useless, as back button is working as expected
        /*Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null && prev instanceof DialogFragment) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();

            // This looks useless, as back button is working as expected
            //ft.remove(prev);
        }*/
        // This looks useless, as back button is working as expected
        //ft.addToBackStack(null);

        DialogFragment dlg = newInstance(title, message, icon, ab);
        dlg.show(ft, "dialog");
    }

    public static void dismiss(FragmentManager fm) {
        // Dismiss previous dialog
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null && prev instanceof DialogFragment) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();

            // This looks useless, as back button is working as expected
            //ft.remove(prev);
        }
        // This looks useless, as back button is working as expected
        //ft.addToBackStack(null);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        int message = getArguments().getInt("message");
        int icon = getArguments().getInt("icon");
        ActionButton action = ActionButton.valueOf(getArguments().getString("action"));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setIconAttribute(icon)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (getActivity() instanceof ElyDialogResponseHandler) {
                                    ((ElyDialogResponseHandler) getActivity()).doPositiveClick();
                                }
                            }
                        }
                );

        if(action==ActionButton.OK_CANCEL) {
            builder.setNegativeButton(R.string.alert_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (getActivity() instanceof ElyDialogResponseHandler) {
                                ((ElyDialogResponseHandler) getActivity()).doNegativeClick();
                            }
                        }
                    }
            );
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
