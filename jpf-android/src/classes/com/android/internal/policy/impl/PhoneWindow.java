package com.android.internal.policy.impl;

import android.content.Context;
import android.view.Window;

public class PhoneWindow extends Window {

	public PhoneWindow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
//    private final static String TAG = "PhoneWindow";
//	
//    private LayoutInflater mLayoutInflater;
//    
//
//    // This is the top-level view of the window, containing the window decor.
//    private DecorView mDecor;
//
//    // This is the view in which the window contents are placed. It is either
//    // mDecor itself, or a child of mDecor where the contents go.
//    private ViewGroup mContentParent;
//
//    
//    public PhoneWindow(Context context) {
//        super(context);
//        mLayoutInflater = LayoutInflater.from(context);
//    }
//    
//    
//    @Override
//    public void setContentView(int layoutResID) {
//        if (mContentParent == null) {
//            installDecor();
//        } else {
//            mContentParent.removeAllViews();
//        }
//        mLayoutInflater.inflate(layoutResID, mContentParent);
//        final Callback cb = getCallback();
//        if (cb != null && !isDestroyed()) {
//            cb.onContentChanged();
//        }
//    }

}
