package com.msg.utils;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.msg.common.Configs;

/**
 * 录音工具类
 * 
 * @author gongchao
 * 
 */
public class AudioRecord extends Activity {
	private static final String LOG_TAG = "AudioRecordTest";
	private String mFileName = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	public AudioRecord() {
		mFileName = Configs.FILE_PATH + "audio_temp.amr";

		File destFile = new File(mFileName);
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		if (destFile.exists()) {
			destFile.delete();
		}
	}

	public String getmFileName() {
		return mFileName;
	}

	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}

	public void startPlaying(String filename) {
		if (new File(filename).exists()) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(filename);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				Log.e(LOG_TAG, "prepare() failed");
			}
		} else {
			Log.e(LOG_TAG, "file no exists");
			Toast.makeText(AudioRecord.this, "请插入SD卡", Toast.LENGTH_LONG).show();
			return;
		}
	}

	public int getLength(String filename) {
		if (new File(filename).exists()) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(filename);
				mPlayer.prepare();
				return mPlayer.getDuration() / 1000;
			} catch (IOException e) {
				Log.e(LOG_TAG, "prepare() failed");
				return 0;
			} finally {
				stopPlaying();
			}
		} else {
			Log.e(LOG_TAG, "file no exists");
			return 0;
		}
	}

	public void stopPlaying() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		} else {
			Log.e(LOG_TAG, "mPlayer is null");
		}
	}

	public void startRecording(String filename) {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(filename);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
		mRecorder.start();
	}

	public void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	public void removeFile() {
		mFileName = Configs.FILE_PATH + "audio_temp.amr";
		File destFile = new File(mFileName);
		if (destFile.exists()) {
			destFile.delete();
		}
	}
	
	public double getAmplitude() {	
		if (mRecorder != null){			
			return  (mRecorder.getMaxAmplitude());		
			}		
		else			
			return 0;	
		}
}
