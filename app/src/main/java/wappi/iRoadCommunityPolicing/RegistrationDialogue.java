package wappi.iRoadCommunityPolicing;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wappi.iRoadCommunityPolicing.R;


/**
 * Dialog allowing users to select a date.
 */
public class RegistrationDialogue extends DialogFragment {
    private static final String TAG="DefaultCountryChooserDialogue";
    private boolean sentSucessfully=false;
    private OnCompleteListener mListener;
    private SharedPreferences sharedPrefs;
    private String phoneNumber="";

    public RegistrationDialogue() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        RelativeLayout relativeLayout =(RelativeLayout)inflater.inflate(R.layout.dialogue_registration, container, false);

        final EditText phoneNumberEditText = (EditText)relativeLayout.findViewById(R.id.phone_number);

        TextView okButton=(TextView)relativeLayout.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentSucessfully=true;
                phoneNumber = phoneNumberEditText.getText().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("phoneNumber",phoneNumber );
                editor.commit();
                dismiss();
            }
        });

        TextView cancelButton=(TextView)relativeLayout.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentSucessfully=false;
                dismiss();
            }
        });


        return relativeLayout;
    }


    @Override
    public void dismiss() {
        super.dismiss();
        mListener.onComplete(sentSucessfully, phoneNumber);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onActivityCreated(Bundle arg0){
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations= R.style.dialogue_animation;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(boolean saved, String phoneNumber);
    }

    public void onAttach(Fragment activity) {
        super.onAttach(activity.getActivity());
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }


}
