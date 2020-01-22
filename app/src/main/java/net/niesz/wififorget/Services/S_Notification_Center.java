package net.niesz.wififorget.Services;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;



public class S_Notification_Center extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId){
		return startId;
		//Log.i("view", "service started");
	
		
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	
}
