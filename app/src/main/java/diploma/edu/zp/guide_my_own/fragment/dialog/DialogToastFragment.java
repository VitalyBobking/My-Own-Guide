package diploma.edu.zp.guide_my_own.fragment.dialog;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

/**
 * craeted by Vitalii
 */

public class DialogToastFragment extends Fragment {
    private ProgressDialog progress;
    private AlertDialogFragment errorDialog;
    private AlertDialogFragment customDialog;
    private AlertDialogFragment successAlertDialog ;

    protected void showProgress(String message){
        if(progress!=null)
            cancelProgress();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(message);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    protected void cancelProgress(){
        if (progress!= null)
            progress.dismiss();
        progress = null;
    }

    protected void showCustom(String title, String msg, int iconId){
        customDialog = AlertDialogFragment.custom(title,msg,iconId);
        customDialog.show(getChildFragmentManager(),"CustomDialog");
    }

    protected void showSuccess(String msg){
        successAlertDialog = AlertDialogFragment.success(msg);
        successAlertDialog.show(getChildFragmentManager(),"SuccessDialogFragment");
    }

    protected void showErrorDialog(String msg){
        errorDialog = AlertDialogFragment.error(msg);
        errorDialog.show( getChildFragmentManager(),"AlertDialogFragment");
    }

    protected void hideAllDialogs(){
        if (progress!= null)
            progress.dismiss();
        progress = null;
        if (successAlertDialog!= null)
            successAlertDialog.dismiss();
        successAlertDialog = null;
        if (customDialog != null){
            customDialog.dismiss();
        }
    }
}
