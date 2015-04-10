package net.somethingdreadful.MAL.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.crashlytics.android.Crashlytics;

import net.somethingdreadful.MAL.PrefManager;
import net.somethingdreadful.MAL.R;

public class NumberPickerDialogFragment extends DialogFragment {

    NumberPicker numberPicker;
    EditText numberInput;
    private onUpdateClickListener callback;
    boolean inputScore = false;

    private View makeNumberPicker() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_episode_picker, null);
        int max = (int) getValue("max");
        float current = getValue("current");

        numberInput = (EditText) view.findViewById(R.id.numberInput);
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        if (!inputScore) {
            numberPicker.setMaxValue(max != 0 ? max : 999);
            numberPicker.setMinValue(0);
            numberPicker.setValue((int) current);
            numberInput.setVisibility(View.GONE);
        } else {
            numberInput.setText(Float.toString(current));
            numberPicker.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setView(makeNumberPicker());
        builder.setTitle(getArguments().getString("title"));
        builder.setPositiveButton(R.string.dialog_label_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                numberPicker.clearFocus();
                numberInput.clearFocus();
                float value = Float.parseFloat(!numberInput.getText().toString().equals("") ? numberInput.getText().toString() : "0.0");
                callback.onUpdated(numberPicker.getValue(), getArguments().getInt("id"), value);
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Get the integer from an argument.
     *
     * @param key The argument name
     * @return int The number of the argument
     */
    public float getValue(String key) {
        try {
            if (getArguments().getInt("id") == R.id.scorePanel && PrefManager.getScoreType() != 3 && PrefManager.getScoreType() != 1)
                inputScore = true;
            return inputScore ? getArguments().getFloat(key) : getArguments().getInt(key);
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, "MALX", "EpisodesPickerDialogFragment.makeNumberPicker(" + key + "): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Set the Callback for update purpose.
     *
     * @param callback The activity/fragment where the callback is located
     * @return NumberPickerDialogFragment This will return the dialog itself to make init simple
     */
    public NumberPickerDialogFragment setOnSendClickListener(onUpdateClickListener callback) {
        this.callback = callback;
        return this;
    }

    /**
     * The interface for callback
     */
    public interface onUpdateClickListener {
        public void onUpdated(int number, int id, float decimal);
    }
}