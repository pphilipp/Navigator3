package com.innotech.imap_taxi.voice;

import java.util.ArrayList;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.EfirOrder;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

/**
 * Service converts speech to text and compares result with list of commands (ArrayList<String[]>)
 * returns index of matched list of commands via receiver
 * */
public class CommandsRecognitionService extends Service {
	public static final String LOG_TAG = CommandsRecognitionService.class.getCanonicalName();
	protected SpeechRecognizer mSpeechRecognizer;
	protected Intent mSpeechRecognizerIntent;
	ArrayList<String[]> commands = null;
	private static final String COMMANDS_KEY = "commands";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "Starting service");
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(new VoiceListener());

		mSpeechRecognizerIntent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		mSpeechRecognizerIntent.putExtra(
				RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

		mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
		
		//get ArrayList of commands to compare
		if (intent != null && intent.getExtras() != null) {
			commands = null;
			commands = (ArrayList<String[]>) intent.getExtras().getSerializable(COMMANDS_KEY);
			if (commands == null || commands.size() == 0) {
					Log.d(LOG_TAG, "There`re no commands to perform");
					stopSelf();
				}
			Log.d(LOG_TAG, "There`re " + commands.size() + " sets of commands");
		} else
			stopSelf();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Destroying service");
		mSpeechRecognizer.destroy();
		super.onDestroy();
	}

	private class VoiceListener implements RecognitionListener {

		@Override
		public void onBeginningOfSpeech() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onError(int error) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onResults(Bundle results) {
			Log.d(LOG_TAG, "onResult");
			if (results != null) {
				ArrayList<String> phraseList = results
						.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				if (phraseList != null && phraseList.size() > 0) {
					/*Log.d(TAG, "There`re " + phraseList.size() + " matches");
					for (String phrase : phraseList)
						Log.d(TAG, "Phrase = " + phrase);*/
					Intent result = new Intent(EfirOrder.BROADCAST_ACTION);
					result.putExtra("phraseList", phraseList);
					sendBroadcast(result);

				} else {
					Log.d(LOG_TAG, "Didn`t recognize speech");
				}
			} else {
				Log.d(LOG_TAG, "No results");
			}
			stopSelf();
		}

		@Override
		public void onRmsChanged(float rmsdB) {
			// TODO Auto-generated method stub
		}
	}
}
