package com.example.calculation;

import java.util.Arrays;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {
	//aaa
	//ゲームモード定数
	public static final int GAMEMODE_ONE = 1;
	public static final int GAMEMODE_ENDLESS = 2;
	// handler用
	private android.os.Handler handler = new android.os.Handler();
	//各view
	private TextView timerText;
	private TextView countText;
	private TextView questionText;
	private TextView answerText;
	private ImageView oneImage;
	private ImageView endlessImage;
	private ImageView maruImage;
	private ImageView batsuImage;
	private ImageView checkBtn;;
	private ImageView[] gridBtns;
	private ImageView minusBtn;
	private ImageView clearBtn;
	private ImageView passBtn;
	//各変数
	private int questionCount = 0;
	private int correctCount = 0;
	private int leftNumber;
	private int rightNumber;
	private int arithmeticOperatorNumber;
	private String tapStr;
	private String[] arithmeticOperators = { "+", "−", "✕", "÷" };
	private int correctAnswer;
	private int maxScore = 0;
	private Boolean tapFlag;
	//ゲームモード管理用
	private int gameMode;
	//CountDownの初期値
	private final MyCountDownTimer cdt = new MyCountDownTimer(60000, 1000);
	//SharedPreference保存用
	private SharedPreferences pref;
	// 音楽再生用(MediaPlayer)
	private MediaPlayer mediaPlayer;
	// 音楽再生用(SoundPool)
	private SoundPool soundPool;
	private int[] soundIds = new int[SOUND_FILES.length];
	// 効果音の配列
	private static final int[] SOUND_FILES = {
			R.raw.push, R.raw.ok, R.raw.ng
	};

	/*-----------------------------------------------------------------------*/
	//initSoundPool
	/*-----------------------------------------------------------------------*/
	private void initSoundPool() {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		for (int i = 0; i < soundIds.length; i++) {
			soundIds[i] = soundPool.load(this, SOUND_FILES[i], 1);
		}
	}

	/*-----------------------------------------------------------------------*/
	//onCreate
	/*-----------------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//各find
		timerText = (TextView) findViewById(R.id.timerText);
		countText = (TextView) findViewById(R.id.countText);
		questionText = (TextView) findViewById(R.id.questionText);
		answerText = (TextView) findViewById(R.id.answerText);
		gridBtns = new ImageView[] {
				(ImageView) findViewById(R.id.btn0),
				(ImageView) findViewById(R.id.btn1),
				(ImageView) findViewById(R.id.btn2),
				(ImageView) findViewById(R.id.btn3),
				(ImageView) findViewById(R.id.btn4),
				(ImageView) findViewById(R.id.btn5),
				(ImageView) findViewById(R.id.btn6),
				(ImageView) findViewById(R.id.btn7),
				(ImageView) findViewById(R.id.btn8),
				(ImageView) findViewById(R.id.btn9)
		};
		checkBtn = (ImageView) findViewById(R.id.checkBtn);
		minusBtn = (ImageView) findViewById(R.id.minusBtn);
		clearBtn = (ImageView) findViewById(R.id.clearBtn);
		passBtn = (ImageView) findViewById(R.id.passBtn);
		oneImage = (ImageView) findViewById(R.id.oneImage);
		endlessImage = (ImageView) findViewById(R.id.endlessImage);
		maruImage = (ImageView) findViewById(R.id.maruImage);
		batsuImage = (ImageView) findViewById(R.id.batsuImage);

		//各ボタンにOnTouchListenerをセット
		for (int i = 0; i < gridBtns.length; i++) {
			gridBtns[i].setOnTouchListener(this);
		}
		checkBtn.setOnTouchListener(this);
		minusBtn.setOnTouchListener(this);
		clearBtn.setOnTouchListener(this);
		passBtn.setOnTouchListener(this);

		//Intentで渡ってきたゲームモードの情報を受取る
		Intent intent = getIntent();
		gameMode = (Integer) intent.getSerializableExtra("gameMode");

		//ゲームモードによって表示と処理を分ける
		if (gameMode == GAMEMODE_ONE) {
			countText.setVisibility(View.INVISIBLE);
			endlessImage.setVisibility(View.INVISIBLE);
			//設定のSharedPreferenceを読み込む
			pref = PreferenceManager.getDefaultSharedPreferences(this);
			//データが無いときは0がデフォルトになる
			maxScore = pref.getInt("maxScore", 0);
		} else {
			timerText.setVisibility(View.INVISIBLE);
			oneImage.setVisibility(View.INVISIBLE);
		}
	}

	/*-----------------------------------------------------------------------*/
	//onResume
	/*-----------------------------------------------------------------------*/
	@Override
	protected void onResume() {
		super.onResume();
		//1分モード時のライフサイクルをケア
		if (gameMode == GAMEMODE_ONE) {
			questionCount = 0;
			correctCount = 0;
			// カウントダウン開始
			cdt.start();
		}
		// 音楽再生用(MediaPlayer)
		mediaPlayer = MediaPlayer.create(this, R.raw.play);
		// title音楽再生(ループあり)
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
		//soundPlayer準備
		initSoundPool();
		//問題作成
		setQuestion();
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
		//soundPool解放
		if (soundPool != null) {
			soundPool.release();
			soundPool = null;
		}
		//カウントダウンを停止
		if (gameMode == GAMEMODE_ONE) {
			cdt.cancel();
		}
	}

	/*-----------------------------------------------------------------------*/
	//onTouch
	/*-----------------------------------------------------------------------*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		//押したとき
		case MotionEvent.ACTION_DOWN:
			//半透明にして押した感を出す
			//0〜9の数字
			for (int i = 0; i < gridBtns.length; i++) {
				if (v.equals(gridBtns[i])) {
					gridBtns[i].setAlpha(100);
					soundPool.play(soundIds[0], 1.0f, 1.0f, 1, 0, 1.0f);
				}
			}
			//それ以外
			if (v.equals(checkBtn)) {
				checkBtn.setAlpha(100);
			} else if (v.equals(minusBtn)) {
				minusBtn.setAlpha(100);
				soundPool.play(soundIds[0], 1.0f, 1.0f, 1, 0, 1.0f);
			} else if (v.equals(clearBtn)) {
				clearBtn.setAlpha(100);
				soundPool.play(soundIds[0], 1.0f, 1.0f, 1, 0, 1.0f);
			} else if (v.equals(passBtn)) {
				passBtn.setAlpha(100);
				soundPool.play(soundIds[0], 1.0f, 1.0f, 1, 0, 1.0f);
			}
			break;

		//離したとき
		case MotionEvent.ACTION_UP:
			for (int i = 0; i < gridBtns.length; i++) {
				if (v.equals(gridBtns[i])) {
					gridBtns[i].setAlpha(255);
				}
			}
			checkBtn.setAlpha(255);
			minusBtn.setAlpha(255);
			clearBtn.setAlpha(255);
			passBtn.setAlpha(255);

			//押されたボタンがgridBtnsに含まれていたら
			if (Arrays.asList(gridBtns).contains(v)) {
				for (int i = 0; i < gridBtns.length; i++) {
					if (v.equals(gridBtns[i])) {
						if (!tapFlag) {
							tapStr = i + "";
							tapFlag = true;
						} else {
							if (tapStr.length() > 8) {
								Toast.makeText(MainActivity.this, "入力桁数が大きすぎます", Toast.LENGTH_SHORT).show();
							} else {
								tapStr += i;
							}
						}
						answerText.setText(tapStr);
					}
				}
			} else if (v.getId() == R.id.checkBtn) {
				if (tapFlag && !tapStr.equals("-")) {
					int checkNumber = Integer.parseInt(tapStr);
					if (correctAnswer == checkNumber) {//正解
						//効果音再生
						soundPool.play(soundIds[1], 1.0f, 1.0f, 1, 0, 1.0f);
						maruImage.setVisibility(View.VISIBLE);
						// 500ミリ秒後にRunnableを実行
						handler.postDelayed(invisible, 500);
						tapFlag = false;
						correctCount++;
					} else {//間違い
						//効果音再生
						soundPool.play(soundIds[2], 1.0f, 1.0f, 1, 0, 1.0f);
						batsuImage.setVisibility(View.VISIBLE);
						handler.postDelayed(invisible, 500);
					}
				}
			} else if (v.getId() == R.id.minusBtn) {
				if (!tapFlag) {
					tapStr = "-";
					tapFlag = true;
				} else {//数字の間でマイナスが入らないように
					Toast.makeText(MainActivity.this, "構文が間違っています", Toast.LENGTH_SHORT).show();
				}
				answerText.setText(tapStr);
			} else if (v.getId() == R.id.clearBtn) {
				tapFlag = false;
				answerText.setText("答え");
			} else {//パスボタン
				setQuestion();
			}
		}
		return true;
	}

	/*-----------------------------------------------------------------------*/
	//setQuestion
	/*-----------------------------------------------------------------------*/
	private void setQuestion() {
		tapFlag = false;
		leftNumber = new Random().nextInt(10);
		rightNumber = new Random().nextInt(10);
		arithmeticOperatorNumber = new Random().nextInt(4);

		if (arithmeticOperatorNumber == 0) {//足し算
			correctAnswer = leftNumber + rightNumber;
		} else if (arithmeticOperatorNumber == 1) {//引き算
			correctAnswer = leftNumber - rightNumber;
		} else if (arithmeticOperatorNumber == 2) {//掛け算
			correctAnswer = leftNumber * rightNumber;
		} else {//割り算
			//0除算しないように対策。右の数字に0がこないようにしてなおかつ割り切れる時
			if (rightNumber != 0 && leftNumber % rightNumber == 0) {
				correctAnswer = leftNumber / rightNumber;
			} else {
				setQuestion();
				return;
			}
		}
		questionText.setText("「 " + leftNumber + arithmeticOperators[arithmeticOperatorNumber] + rightNumber + " 」");
		answerText.setText("答え");
		questionCount++;
		countText.setText("正答数 : " + correctCount + "/" + questionCount);
	}

	/*-----------------------------------------------------------------------*/
	//Runnable
	//マルバツの表示
	/*-----------------------------------------------------------------------*/
	private final Runnable invisible = new Runnable() {
		@Override
		public void run() {//マルバツ表示を隠す
			maruImage.setVisibility(View.INVISIBLE);
			batsuImage.setVisibility(View.INVISIBLE);
			setQuestion();
		}
	};

	/*-----------------------------------------------------------------------*/
	//MyCountDownTimer
	/*-----------------------------------------------------------------------*/
	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// カウントダウン完了後に呼ばれる
			timerText.setText("0");
			if (correctCount > maxScore) {//獲得スコアがハイスコアを上回っていたら更新
				maxScore = correctCount;
				//SharedPreferencesを更新
				pref.edit().putInt("maxScore", maxScore).commit();
			}
			Intent intent;
			intent = new Intent(MainActivity.this, ResultActivity.class);
			intent.putExtra("correctCount", correctCount);
			intent.putExtra("questionCount", questionCount);
			startActivity(intent);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// インターバル(countDownInterval)毎に呼ばれる
			timerText.setText(Long.toString(millisUntilFinished / 1000 % 60));
		}
	}
}
