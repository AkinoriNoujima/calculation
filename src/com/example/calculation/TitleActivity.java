package com.example.calculation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleActivity extends Activity implements OnTouchListener {

	//各view
	private TextView maxScoreText;
	private TextView infoText;
	private ImageView oneMinuteBtn;
	private ImageView endlessBtn;
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
		setContentView(R.layout.activity_title);

		maxScoreText = (TextView) findViewById(R.id.maxScoreText);
		infoText = (TextView) findViewById(R.id.infoText);
		oneMinuteBtn = (ImageView) findViewById(R.id.oneMinuteBtn);
		endlessBtn = (ImageView) findViewById(R.id.endlessBtn);
		oneMinuteBtn.setOnTouchListener(this);
		endlessBtn.setOnTouchListener(this);

		// 透明にするアニメーションを作成
		AlphaAnimation animation_alpha = new AlphaAnimation(1, 0);
		// アニメーション実行時間を指定する（ms）
		animation_alpha.setDuration(1500);
		// アニメーションの起動
		this.infoText.startAnimation(animation_alpha);
		//アニメーションを繰り返す
		animation_alpha.setRepeatCount(Animation.INFINITE);

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
		mediaPlayer = MediaPlayer.create(this, R.raw.title);
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
	//onTouch
	/*-----------------------------------------------------------------------*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Intent intent;
		switch (event.getAction()) {
		//押したとき
		case MotionEvent.ACTION_DOWN:
			//半透明にして押した感を出す
			switch (v.getId()) {
			case R.id.oneMinuteBtn:
				oneMinuteBtn.setAlpha(100);
				break;
			case R.id.endlessBtn:
				endlessBtn.setAlpha(100);
				break;
			default:
				break;
			}
			break;

		//離したとき
		case MotionEvent.ACTION_UP:
			//ゲームモードの情報を保持して画面遷移させる
			switch (v.getId()) {
			case R.id.oneMinuteBtn:
				oneMinuteBtn.setAlpha(255);
				intent = new Intent(TitleActivity.this, MainActivity.class);
				intent.putExtra("gameMode", MainActivity.GAMEMODE_ONE);
				startActivity(intent);
				break;

			case R.id.endlessBtn:
				endlessBtn.setAlpha(255);
				intent = new Intent(TitleActivity.this, MainActivity.class);
				intent.putExtra("gameMode", MainActivity.GAMEMODE_ENDLESS);
				startActivity(intent);
				break;
			default:
				break;
			}
			break;
		}
		return true;
	}
}