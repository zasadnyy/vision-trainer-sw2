package ua.org.zasadnyy.visiontrainer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;


/**
 * The sample control preference activity handles the preferences for the sample
 * control extension.
 */
public class PreferenceActivity extends android.preference.PreferenceActivity {

    private static final int DIALOG_READ_ME = 1;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preference);

        // Handle read me.
        Preference preference = findPreference(getText(R.string.preference_key_read_me));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog(DIALOG_READ_ME);
                return true;
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case DIALOG_READ_ME:
                dialog = createReadMeDialog();
                break;
            default:
                Log.w(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "Not a valid dialog id: " + id);
                break;
        }

        return dialog;
    }

    /**
     * Create the Read me dialog.
     *
     * @return the Dialog
     */
    private Dialog createReadMeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.preference_option_read_me_txt)
                .setTitle(R.string.preference_option_read_me)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

}
