package android.media;

import android.content.Context;

public class MediaPlayer {

	public static MediaPlayer create(Context context, int resid) {

		return new MediaPlayer();
	}
	
	
	public  void start() throws IllegalStateException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
      
    }
	
	public  void stop() throws IllegalStateException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

}
