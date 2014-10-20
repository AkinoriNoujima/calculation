package com.example.calculation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity implements OnClickListener {

	//各view
	private TextView maxScoreText;
	private TextView resultText;
	private Button returnBtn;
	private int correctCount;
	private int questionCount;
	private int maxScore;
	//SharedPreference保存用
	private SharedPreferences pref;
	// 音楽再生用(MediaPlayer)
	private MediaPlayer mediaPlayer;

	/*-----------------------------------------------------------------------*/
	//onCreate
	/*-----------------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rsult);

		maxScoreText = (TextView) findViewById(R.id.maxScoreText);
		resultText = (TextView) findViewById(R.id.resultText);
		returnBtn = (Button) findViewById(R.id.returnBtn);
		returnBtn.setOnClickListener(this);

		Intent intent = getIntent();
		correctCount = (Integer) intent.getSerializableExtra("correctCount");
		questionCount = (Integer) intent.getSerializableExtra("questionCount");

		resultText.setText("正答数\n" + correctCount + "/" + questionCount);

		//設定のSharedPreferenceを読み込む
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		//データが無いときは0がデフォルトになる
		maxScore = pref.getInt("maxScore", 0);
		maxScoreText.setText("ハイスコア : " + maxScore + "問正解");
	}

	/*-----------------------------------------------------------------------*/
	//onResume
	/*-----------------------------------------------------------------------*/
	@Override
	protected void onResume() {
		super.onResume();
		// 音楽再生用(MediaPlayer)
		mediaPlayer = MediaPlayer.create(this, R.raw.result);
		// title音楽再生(ループあり)
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}

	/*-----------------------------------------------------------------------*/
	//onPause
	/*-----------------------------------------------------------------------*/
	@Override
	protected void onPause() {
		super.onPause();
		//mediaPlayer解放
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/*-----------------------------------------------------------------------*/
	//onClick
	/*-----------------------------------------------------------------------*/
	@Override
	public void onClick(View v) {
		Intent intent;
		intent = new Intent(ResultActivity.this, TitleActivity.class);
		startActivity(intent);
	}

	/*-----------------------------------------------------------------------*/
	//dispatchKeyEvent
	//戻るキーを押したときの挙動
	/*-----------------------------------------------------------------------*/
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				//戻るボタンが押されたときの処理
				Intent intent = new Intent(ResultActivity.this, TitleActivity.class);
				startActivity(intent);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}