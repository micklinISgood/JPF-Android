package android.view;

import android.content.Context;

public class Window {
	Context mContext; 
	
	/**
     * API from a Window back to its caller.  This allows the client to
     * intercept key dispatching, panels and menus, etc.
     */
    public interface Callback {
       
    }

    public Window(Context context) {
        mContext = context;
    }

    /**
     * Return the Context this window policy is running in, for retrieving
     * resources and other information.
     *
     * @return Context The Context that was supplied to the constructor.
     */
    public final Context getContext() {
        return mContext;
    }

	public View findViewById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		
	}

	public void setContentView(View view) {
		// TODO Auto-generated method stub
		
	}
}
