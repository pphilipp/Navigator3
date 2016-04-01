package com.innotech.imap_taxi.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

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

    EditText nick,login,password,serverMaster, serverSlave, portMaster, portSlave, dispatcherPhone, fontSize;
    CheckBox isAutoSignIn, isEtherCircle, isFirstCircleNotif, isSecondCirlceNotif, isEtherNotif;
    SeekBar volume;
    TextView volumeLevel;
	//private CheckBoxPreference mListPreference;

	//AlertDialog.Builder builder;

	@Override
	public void onCreate(Bundle savedInstanceState) {

//		if (ServerData.getInstance().isNight) {
//			setTheme(R.style.Theme_NoTitle_Black);
//		} else {
//			setTheme(R.style.Theme_NoTitle);
//		}

		super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_new);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext());
        nick = (EditText) findViewById(R.id.prefNick);
        login = (EditText) findViewById(R.id.prefLogin);
        password = (EditText) findViewById(R.id.prefPassword);
        serverMaster = (EditText) findViewById(R.id.prefServerMaster);
        serverSlave = (EditText) findViewById(R.id.prefServerSlave);
        portMaster = (EditText) findViewById(R.id.prefPortMaster);
        portSlave = (EditText) findViewById(R.id.prefPortSlave);
        dispatcherPhone = (EditText) findViewById(R.id.prefDispPhone);
        fontSize = (EditText) findViewById(R.id.prefFontSize);

        isAutoSignIn = (CheckBox) findViewById(R.id.prefIsAutoSignIn);
        isEtherCircle = (CheckBox) findViewById(R.id.prefIsEtherCircle);
        isEtherNotif = (CheckBox) findViewById(R.id.prefIsEtherNotif);
        isFirstCircleNotif = (CheckBox) findViewById(R.id.prefIsFirstCircleNotif);
        isSecondCirlceNotif = (CheckBox) findViewById(R.id.prefIsSecondCircleNotif);

        volume = (SeekBar) findViewById(R.id.prefVolume);

        volumeLevel = (TextView) findViewById(R.id.prefVolumeLevel);

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


	}

	@Override
	protected void onResume() {
		super.onResume();

	}

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
        editor.commit();
//        isAutoSignIn.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SIGN_IN, false));
//        isEtherCircle.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SEARCH, false));
//        isEtherNotif.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SEARCH1_NOTIF, false));
//        isFirstCircleNotif.setChecked(sharedPrefs.getBoolean(KEY_AUTO_SEARCH2_NOTIF, false));
//        isSecondCirlceNotif.setChecked(sharedPrefs.getBoolean(KEY_ETHER_NOTIF, false));

	}

//	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//		my_func();
//	}


//	private void my_func() {
//		if (!sharedPrefs.getString(KEY_NICK, "").equals("")) {
//			nick.setSummary(sharedPrefs.getString(KEY_NICK, ""));
//		} else {
//			nick.setSummary("Введите позывной");
//		}
//
//		if (!sharedPrefs.getString(KEY_LOGIN, "").equals("")) {
//			login.setSummary(sharedPrefs.getString(KEY_LOGIN, ""));
//		} else {
//			login.setSummary("Введите логин");
//		}
//
//		if (!sharedPrefs.getString(KEY_PASS, "").equals("")) {
//			pass.setSummary("******");
//		} else {
//			pass.setSummary("Введите пароль");
//		}
//        //отличие от imap_symi
//		if (!sharedPrefs.getString(KEY_HOST, "").equals("")) {
//			host.setSummary(sharedPrefs.getString(KEY_HOST, ""));
//		} else {
//			host.setSummary("Введите сервер");
//		}
//        //отличие от imap_symi
//        if (!sharedPrefs.getString(KEY_PORT, "").equals("")) {
//			port.setSummary(sharedPrefs.getString(KEY_PORT, ""));
//		} else {
//			port.setSummary("Введите порт");
//		}
//
//        if (!sharedPrefs.getString(KEY_HOST_SLAVE, "").equals("")) {
//            slaveHost.setSummary(sharedPrefs.getString(KEY_HOST_SLAVE, ""));
//        } else {
//            slaveHost.setSummary("Введите сервер");
//        }
//        //отличие от imap_symi
//        if (!sharedPrefs.getString(KEY_PORT_SLAVE, "").equals("")) {
//            slavePort.setSummary(sharedPrefs.getString(KEY_PORT_SLAVE, ""));
//        } else {
//            slavePort.setSummary("Введите порт");
//        }
//
//
//
//		if (!sharedPrefs.getString(KEY_DISP_PHONE, "").equals("")) {
//			disp_phone.setSummary(sharedPrefs.getString(KEY_DISP_PHONE, ""));
//		} else {
//			disp_phone.setSummary("Введите телефон диспетчера");
//		}
//
//		if (!sharedPrefs.getString(KEY_TEXT_SIZE, "").equals("")) {
//
//			int txtSz = Integer.parseInt(sharedPrefs.getString(KEY_TEXT_SIZE, "0"));
//			if ((txtSz>0) && (txtSz<=30)) {
//				//ServerData.getInstance().textSize = txtSz + 14;
//				text_size.setSummary(sharedPrefs.getString(KEY_TEXT_SIZE, "0"));
//			} else {
//				Editor ed=sharedPrefs.edit();
//				ed.putString(KEY_TEXT_SIZE, "0");
//				ed.apply();
//				text_size.setSummary("Введите размер шрифта (1-30)");
//			}
//
//		} else {
//			Editor ed=sharedPrefs.edit();
//			ed.putString(KEY_TEXT_SIZE, "0");
//			ed.apply();
//			text_size.setSummary("Введите размер шрифта (1-30)");
//		}
//
//		if (!sharedPrefs.getString(KEY_DISP_PHONE, "").equals("")) {
//			disp_phone.setSummary(sharedPrefs.getString(KEY_DISP_PHONE, ""));
//		} else {
//			disp_phone.setSummary("Введите телефон диспетчера");
//		}
//
//		if (sharedPrefs.getInt(KEY_VOLUME, -1)!=-1){
//			volume.setSummary("Звук на уровне "+ sharedPrefs.getInt(KEY_VOLUME, 50));
//		}else{
//			volume.setSummary("Звук на уровне 50");
//		}
//
//	}

}