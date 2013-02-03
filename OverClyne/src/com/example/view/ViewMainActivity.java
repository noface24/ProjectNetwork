package com.example.view;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.overload.OverClyne.R;
import com.overload.OverClyne.R.id;
import com.overload.OverClyne.R.layout;

import android.R.integer;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class ViewMainActivity extends Activity implements SensorEventListener {
	public AQuery aq;
	private ThreadCountDown threadCountDown;
	private String imageUrl;
	private SensorManager sensorManager;
	private EditText ipddr;
	private Button go, back, servoR, servoL, startThread, beep;;
	private boolean checkGo = false, checkBack = false, threadstatus = false;
	private TextView view_Sonic, Viewaccelerometer;
	private Button sonic;
	private Button flash;
	private int sonic_int = 3;
	private String UrlCommand = "";
	private String tempcomm = "";
	private Button SetServo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BitmapAjaxCallback.setPixelLimit(600 * 500);
		BitmapAjaxCallback.setMaxPixelLimit(2000000);
		BitmapAjaxCallback.setCacheLimit(80);
		AQUtility.cleanCacheAsync(this, 0, 0);
		BitmapAjaxCallback.clearCache();
		AjaxCallback.setNetworkLimit(8);
		aq = new AQuery(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_main);
		// ************** sensor *************//
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		// ----------------------------------//
		startThread = (Button) findViewById(R.id.button1);
		view_Sonic = (TextView) findViewById(R.id.view_Sonic);
		go = (Button) findViewById(R.id.button3);
		servoR = (Button) findViewById(R.id.SRight);
		servoL = (Button) findViewById(R.id.SLeft);
		back = (Button) findViewById(R.id.button4);
		beep = (Button) findViewById(R.id.button2);
		ipddr = (EditText) findViewById(R.id.editText1);
		sonic = (Button) findViewById(R.id.sonic);
		flash = (Button) findViewById(R.id.flash);
		SetServo = (Button) findViewById(R.id.setServo);
		SetServo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SetServo.getText().toString().equals("Ser Off")) {
					aq.ajax(UrlCommand + "SetServoON", String.class,
							new AjaxCallback<String>() {

								@Override
								public void callback(String url, String html,
										AjaxStatus status) {
									SetServo.setText("Ser On");
								}
							});
					
				}
				if (SetServo.getText().toString().equals("Ser On")) {
					aq.ajax(UrlCommand + "SetServoOFF", String.class,
							new AjaxCallback<String>() {

								@Override
								public void callback(String url, String html,
										AjaxStatus status) {
									SetServo.setText("Ser Off");
								}
							});
					
				}

			}
		});
		flash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				aq.ajax(UrlCommand + "flash", String.class,
						new AjaxCallback<String>() {

							@Override
							public void callback(String url, String html,
									AjaxStatus status) {
							}
						});

			}
		});
		Viewaccelerometer = (TextView) findViewById(R.id.Viewaccelerometer);

		sonic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				aq.ajax(UrlCommand + "sonic", String.class,
						new AjaxCallback<String>() {

							@Override
							public void callback(String url, String html,
									AjaxStatus status) {
								sonic_int = 0;
							}
						});

			}
		});

		// /---------------- Set Servo X----------------///
		servoL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// aq.id(R.id.).image(imageUrl + "x", false, false);
				aq.ajax(UrlCommand + "x", String.class,
						new AjaxCallback<String>() {

							@Override
							public void callback(String url, String html,
									AjaxStatus status) {
							}
						});
			}
		});
		// /---------------- Set Servo Y----------------///
		servoR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// aq.id(R.id.imageView1).image(imageUrl + "y", false, false, 0,
				// AQuery.FLAG_ACTIVITY_NO_ANIMATION);
				aq.ajax(UrlCommand + "y", String.class,
						new AjaxCallback<String>() {

							@Override
							public void callback(String url, String html,
									AjaxStatus status) {
							}
						});

			}
		});
		// /------------------------------------------------///

		// /-------------- setOnTouch Go ----------------///
		go.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					checkGo = true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					checkGo = false;
				}
				return false;
			}
		});
		// /-------------- -------------- ----------------///

		// /-------------- setOnTouch Back ----------------///
		back.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					checkBack = true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					checkBack = false;
				}
				return false;
			}
		});
		// /-------------- -------------- ----------------///

		// /-------------- setOnClick Beep ----------------///
		beep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// aq.id(R.id.imageView1).image(imageUrl + "beep", false,
				// false);
				aq.ajax(UrlCommand + "beep", String.class,
						new AjaxCallback<String>() {

							@Override
							public void callback(String url, String html,
									AjaxStatus status) {
							}
						});
			}
		});

		// /-------------- -------------- ----------------///

		// /-------------- startThread and get ip address ------///
		startThread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// http://192.168.43.62:8080/?comm=x
				UrlCommand = "http://" + ipddr.getText().toString().trim()
						+ ":8080/?comm=";
				if (threadstatus == false) {
					threadCountDown = new ThreadCountDown(
							ViewMainActivity.this, ipddr.getText().toString());
					startThread.setText("Stop!");
					threadstatus = true;
					threadCountDown.start();
				} else {
					// -----------------Button--stopService----------------//
					threadCountDown.requestStop();
					threadstatus = false;
					startThread.setText("Start");

				}
			}
		});

	}

	@Override
	protected void onPause() {
		if (threadstatus == true) {
			threadCountDown.requestStop();
		}
		super.onPause();
	}

	public void threadProcess(String imageUrl) {
		if (this.imageUrl != imageUrl)
			this.imageUrl = imageUrl;

		if (sonic_int < 2) {
			aq.ajax(UrlCommand + "sonic_int", String.class,
					new AjaxCallback<String>() {

						@Override
						public void callback(String url, String html,
								AjaxStatus status) {
							if (html.equals("o")) {
								view_Sonic.setText("Out Of Rang");
							} else
								view_Sonic.setText(html.trim() + " cm.");
						}
					});
			sonic_int++;
			// view_Sonic.setText(String.valueOf(++sonic_int)+tempcomm);
		}

		if (!comm.equals(tempcomm)) {
			aq.ajax(UrlCommand + comm, String.class,
					new AjaxCallback<String>() {

						@Override
						public void callback(String url, String html,
								AjaxStatus status) {
						}
					});
			tempcomm = comm;
			// view_Sonic.setText(String.valueOf(++sonic_int)+tempcomm);
		}
		aq.id(R.id.imageView1).image(imageUrl + "1", false, false);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// ****************** sensor smooth ***************//
	static final float ALPHA = 0.5f;
	protected float[] accelVals;

	protected float[] lowPass(float[] input, float[] output) {
		if (output == null)
			return input;

		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}

	// -----------------------------------------------//

	private String comm = "sn";

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelVals = lowPass(event.values, accelVals);
			// float x = accelVals[0];
			float y = accelVals[1];
			// float z = accelVals[2];
			// txtacc.setText("X:" + x + " Y:" + y + " Z:" + z);
			String tm = "";
			// forward
			// if (x < 0.2) {
			// tm += "g";
			// } else if (x > 4.0) {
			// tm += "b";
			// } else {
			// tm += "s";
			// }
			// if (y > 2.8) {
			// tm += "r";
			// } else if (y < -2.8) {
			// tm += "l";
			// } else {
			// tm += "n";
			// }

			if (checkBack == false && checkGo == false) {
				if (y > 2.8) {
					tm += "sr";
				} else if (y < -2.8) {
					tm += "sl";
				} else {
					tm += "sn";
				}
			}
			if (checkGo == true) {
				if (y > 2.8) {
					tm += "gr";
				} else if (y < -2.8) {
					tm += "gl";
				} else {
					tm += "gn";
				}
			}
			if (checkBack == true) {
				if (y > 2.8) {
					tm += "br";
				} else if (y < -2.8) {
					tm += "bl";
				} else {
					tm += "bn";
				}
			}

			comm = tm;
			Viewaccelerometer.setText(comm);

		}
	}

}
