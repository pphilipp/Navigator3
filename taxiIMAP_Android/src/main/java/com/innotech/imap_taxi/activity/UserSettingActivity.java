package com.innotech.imap_taxi.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserSettingActivity extends Activity  {
	SharedPreferences sharedPrefs;
	public static final String KEY_NICK = "prefNick";
	public static final String KEY_LOGIN = "prefLogin";
	public static final String KEY_PASS = "prefPass";
	public static final String KEY_HOST = "prefHost";
	public static final String KEY_PORT = "prefPort";
    public static final String KEY_HOST_SLAVE = "prefHostSlave";
	public static final String KEY_PORT_SLAVE = "prefPortSlave";
	public static final String KEY_DISP_PHONE = "prefDispPhone";
	public static final String KEY_TEXT_SIZE = "prefTextSize";
	public static final String KEY_VOLUME = "prefVolume";
    public static final String KEY_AUTO_SIGN_IN = "prefIsAutoEnter";
    public static final String KEY_AUTO_SEARCH = "prefIsAutoSearch";
    public static final String KEY_AUTO_SEARCH1_NOTIF = "prefAutoSearch1";
    public static final String KEY_AUTO_SEARCH2_NOTIF = "prefAutoSearch2";
    public static final String KEY_ETHER_NOTIF = "prefAutoSearchEfir";
    @Bind(R.id.prefNick) EditText nick;
    @Bind(R.id.prefLogin) EditText login;
    @Bind(R.id.prefPassword) EditText password;
    @Bind(R.id.prefServerMaster)EditText serverMaster;
    @Bind(R.id.prefServerSlave)EditText serverSlave;
    @Bind(R.id.prefPortMaster)EditText portMaster;
    @Bind(R.id.prefPortSlave)EditText portSlave;
    @Bind(R.id.prefDispPhone)EditText dispatcherPhone;
    @Bind(R.id.prefFontSize)EditText fontSize;
    @Bind(R.id.prefIsAutoSignIn)CheckBox isAutoSignIn;
    @Bind(R.id.prefIsEtherCircle)CheckBox isEtherCircle;
    @Bind(R.id.prefIsFirstCircleNotif)CheckBox isFirstCircleNotif;
    @Bind(R.id.prefIsSecondCircleNotif)CheckBox isSecondCirlceNotif;
    @Bind(R.id.prefIsEtherNotif)CheckBox isEtherNotif;
    @Bind(R.id.prefVolume)SeekBar volume;
    @Bind(R.id.prefVolumeLevel)TextView volumeLevel;
    @Bind(R.id.app_version) TextView tvAppVersion;
	//private CheckBoxPreference mListPreference;
	//AlertDialog.Builder builder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_new);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext());
        ButterKnife.bind(this);
        nick.setText(sharedPrefs.getString(KEY_NICK,""));
        login.setText(sharedPrefs.getString(KEY_LOGIN,""));
        password.setText(sharedPrefs.getString(KEY_PASS,""));
        serverMaster.setText(sharedPrefs.getString(KEY_HOST,""));
        serverSlave.setText(sharedPrefs.getString(KEY_HOST_SLAVE,""));
        portMaster.setText(sharedPrefs.getString(KEY_PORT,""));
        portSlave.setText(sharedPrefs.getString(KEY_PORT_SLAVE,""));
        dispatcherPhone.setText(sharedPrefs.getString(KEY_DISP_PHONE,""));
        fontSize.setText(sharedPrefs.getString(KEY_TEXT_SIZE, "10"));
        int vol = sharedPrefs.getInt(KEY_VOLUME, 50);

        volumeLevel.setText(vol + "%");
        volume.setProgress(vol);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumeLevel.setText(seekBar.getProgress()+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        isAutoSignIn.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SIGN_IN, false));
        isEtherCircle.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SEARCH, false));
        isEtherNotif.setChecked(sharedPrefs.getBoolean(KEY_ETHER_NOTIF, true));
        isFirstCircleNotif.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SEARCH1_NOTIF, true));
        isSecondCirlceNotif.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SEARCH2_NOTIF, true));

        initAppBuildVersion();
	}

    private void initAppBuildVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        tvAppVersion.setText("v." + version);
    }

    @Override
	protected void onResume() {super.onResume();}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_NICK, String.valueOf(nick.getText()));
        editor.putString(KEY_LOGIN, String.valueOf(login.getText()));
        editor.putString(KEY_PASS, String.valueOf(password.getText()));
        editor.putString(KEY_HOST, String.valueOf(serverMaster.getText()));
        editor.putString(KEY_HOST_SLAVE, String.valueOf(serverSlave.getText()));
        editor.putString(KEY_PORT, String.valueOf(portMaster.getText()));
        editor.putString(KEY_PORT_SLAVE, String.valueOf(portSlave.getText()));
        editor.putString(KEY_DISP_PHONE, String.valueOf(dispatcherPhone.getText()));
        editor.putString(KEY_TEXT_SIZE, String.valueOf(fontSize.getText()));
        editor.putInt(KEY_VOLUME, volume.getProgress());
        editor.putBoolean(KEY_AUTO_SIGN_IN, isAutoSignIn.isChecked());
        editor.putBoolean(KEY_AUTO_SEARCH, isEtherCircle.isChecked());
        editor.putBoolean(KEY_AUTO_SEARCH1_NOTIF, isFirstCircleNotif.isChecked());
        editor.putBoolean(KEY_AUTO_SEARCH2_NOTIF, isSecondCirlceNotif.isChecked());
        editor.putBoolean(KEY_ETHER_NOTIF, isEtherNotif.isChecked());
        editor.apply();
	}

}