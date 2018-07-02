package diploma.edu.zp.guide_my_own.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import diploma.edu.zp.guide_my_own.R;

/**
 * craeted by Vitalii
 */

public class AlertDialogFragment extends DialogFragment {
    public static final String TAG = "AlertDialogFragment";
    public static final String MESSAGE_RES_KEY = "MESSAGE_RES_KEY";
    public static final String MESSAGE_KEY = "MESSAGE_KEY";
    public static final String TITLE_RES_KEY = "TITLE_RES_KEY";
    public static final String TITLE_MSG_KEY = "TITLE_MSG_KEY";
    public static final String NEUTRAL_BUTTON_KEY = "NEUTRAL_BUTTON_KEY";
    public static final String ICON_RES_ID_KEY = "ICON_RES_ID_KEY";

    /* Create a new instance of MyDialogFragment, providing
     * as an argument. */
    public static AlertDialogFragment newInstance(@NonNull Bundle args) {
        if (args == null) args = new Bundle();
        AlertDialogFragment f = new AlertDialogFragment();
        f.setArguments(args);
        return f;
    }

    /**
     * Create a dialog fragment with custom fields
     * @param msg
     * @param title
     * @param neutralbtn
     * @param iconResId
     * @return
     */
    public static AlertDialogFragment newInstance(String msg, int title, int neutralbtn, int iconResId) {
        Bundle args = new Bundle();
        args.putString(MESSAGE_KEY, msg);
        args.putInt(TITLE_RES_KEY, title);
        args.putInt(NEUTRAL_BUTTON_KEY, neutralbtn);
        args.putInt(ICON_RES_ID_KEY, iconResId);
        AlertDialogFragment f = new AlertDialogFragment();
        f.setArguments(args);
        return f;
    }

    /**
     * Shortcut for error dialog
     * @param msg
     * @return
     */
    public static AlertDialogFragment error(String msg) {
        AlertDialogFragment f = AlertDialogFragment.newInstance(
                msg,
                R.string.alert,
                android.R.string.ok,
                android.R.drawable.ic_dialog_info
        );
        return f;
    }

    /**
     * Shortcut for error dialog
     * @param msg
     * @return
     */
    public static AlertDialogFragment custom(String title, String msg, int iconResId) {
        Bundle b = new Bundle();
        b.putString(TITLE_MSG_KEY,title);
        b.putString(MESSAGE_KEY,msg);
        b.putInt(ICON_RES_ID_KEY,iconResId);
        AlertDialogFragment f = AlertDialogFragment.newInstance(b);
        return f;
    }

    /**
     * Shortcut for success dialog
     * @param msg
     * @return
     */
    public static AlertDialogFragment success(String msg) {
        AlertDialogFragment f = AlertDialogFragment.newInstance(
                msg, R.string.done, android.R.string.ok, R.drawable.ic_done_black_24dp);
        return f;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msgString = getArguments().getString(MESSAGE_KEY, null);
        int msgResId = getArguments().getInt(MESSAGE_RES_KEY, R.string.alert);
        int titleResId = getArguments().getInt(TITLE_RES_KEY);
        String titleMsg = getArguments().getString(TITLE_MSG_KEY);
        int okResId = getArguments().getInt(NEUTRAL_BUTTON_KEY,android.R.string.ok);
        int iconResId = getArguments().getInt(ICON_RES_ID_KEY,android.R.drawable.stat_notify_error);

        AlertDialog.Builder b =  new AlertDialog.Builder(getActivity(), R.style.My_Dialog);
        if (titleResId != 0) b.setTitle(titleResId);
        if (titleMsg != null) b.setTitle(titleMsg);
        if (msgString != null) b.setMessage(msgString);
        if (msgResId != 0 && msgString == null) b.setMessage(msgResId);
        if (okResId != 0) b.setNeutralButton(okResId, null);
        if (iconResId != 0) b.setIcon(iconResId);
        return b.create();
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.e("ABSDIALOGFRA", e.toString());
        }
    }

}
