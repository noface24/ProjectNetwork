package com.example.view;

import android.util.Log;

public class ThreadCountDown extends Thread {

	private ViewMainActivity vMA;
	private boolean stop;
	private  String imageUrlT = "";
	private String ip;
	public int num = 0;

	public ThreadCountDown(ViewMainActivity viewMainActivity, String url) {
		this.vMA = viewMainActivity;
		//http://192.168.10.235:8080/stream/live.jpg?id=13		
		ip = url;
	}

	public void requestStop() {
		this.stop = true;

	}

	@Override
	public void run() {

		while (true) {
			try {
				sleep((long) 100);
			} catch (InterruptedException e) {
				Log.e("log_thread", "Error Thread : " + e.toString());
			}

			if (this.stop)
				return;

			this.vMA.runOnUiThread(new Runnable() {
				public void run() {
					//vMA.threadProcess("http://"+ip+":8080/stream/live.jpg?num="+num+"&comm=",num);	
					vMA.threadProcess("http://"+ip+":8080/stream/live.jpg?comm=");
				}
			});
		}
	}
}