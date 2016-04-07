package com.innotech.imap_taxi.ping;

import java.util.Timer;

public class PingHelper {
	private static PingHelper instance;
	private Timer timer;
	private PingTask task;
	private long period;

	public static PingHelper getInstance() {
		if (instance == null) {
			instance = new PingHelper();
		}

		return instance;
	}

	private PingHelper() {
		super();
	//	timer = new Timer();
	//	task = new PingTask();
		period = 30 * 1000;
	}

	public void start() {
		timer = new Timer();
		task = new PingTask();
		timer.scheduleAtFixedRate(task, 0, period);
	}

	public void stop() {
		timer.cancel();
		task.cancel();
		instance = null;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}
}
