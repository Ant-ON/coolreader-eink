package org.coolreader;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class CoolReaderApp extends Application
{
	@SuppressLint("StaticFieldLeak")
	private static volatile Context mCtx;

	@Override
	public void onCreate()
	{
		mCtx = this;
		super.onCreate();
	}

	@Override
	protected void attachBaseContext(Context base)
	{
		mCtx = this;
		super.attachBaseContext(base);
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		mCtx = this;
	}

	public static Context get()
	{
		return mCtx;
	}
}
