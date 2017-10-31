package com.devdroid.sleepassistant.mode;

import android.os.Handler;
import android.os.Message;

import java.util.Random;

public class MyRandom extends Thread {
	private Random random;
	public static boolean isRandom = true;
	private int i;
	private Message msg;
	private Handler handler;
	
	public MyRandom() {
	}
	
	public MyRandom(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		super.run();
		random = new Random();

		while (isRandom) {
			i = random.nextInt(60);
			msg = new Message();
			msg.what = i;
			handler.sendMessage(msg);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

	}

}
