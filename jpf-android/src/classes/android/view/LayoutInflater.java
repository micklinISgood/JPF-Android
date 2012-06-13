package android.view;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

/**
 * Inflates a resource value into a view hierarchy.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class LayoutInflater {
	boolean done = false;
	Context c;

	View rootView;

	public LayoutInflater(Context c) {
		this.c = c;

	}

	public View inflate(int resId, View root) {

		init(resId);
		nextElement();

		return parse();

	}

	static native void init(int resRef);

	public View parse() {
		View root = null;
		try {
			
			root = createView(getType(), getName(), getID());
			if (root instanceof ViewGroup) {
				int numChildren = getNumChildren();
				View child = null;
				for (int i = 0; i < numChildren; i++) {
					nextElement();
					child = parse();
					((ViewGroup) root).addView(child);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return root;
	}

	static native int getNumChildren();

	public View createView(String type, String name, int id)
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		System.out.println("Inflating Type:" + type + " Name: " + name + " ID: " +id);
		
		View ret = null;
		Class<View> cls = (Class<View>) Class.forName("android.widget." + type);

		Class[] intArgsClass = new Class[] { Context.class };
		Object[] intArgs = new Object[] { c };
		Constructor intArgsConstructor = cls.getConstructor(intArgsClass);
		ret = (View) intArgsConstructor.newInstance(intArgs);

		ret.setName(name);
		ret.setID(id);
		return ret;
	}

	static native String getType();

	static native String getName();

	static native int getID();

	static native boolean nextElement();

	static native String getParent();
}