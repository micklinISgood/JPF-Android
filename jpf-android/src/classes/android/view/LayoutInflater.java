package android.view;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

/**
 * Inflates a layout file given its resource id into a {@link View} hierarchy.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class LayoutInflater {
  public static final String TAG = "LayoutInflater";

  /** The package where Android Widgets are stored */
  private static String WIDGET_PACKAGE = "android.widget";

  /** Not sure what context to store here TODO */
  Context c;

  /** The name of the layout file being inflated */
  String layout;

  public LayoutInflater(Context c) {
    this.c = c;

  }

  /**
   * Inflates the layout with ID resID and sets root as the root of the {@link View} hierarchy.
   * 
   * @param resId
   * @param root
   * @return the {@link View} hierarchy's root
   */
  public View inflate(int resId, View root) {
    layout = setup(resId);
    nextElement();
    View v = parse();
    print(v);
    return v;

  }

  private void print(View v) {
    // System.out.println(v.toString() + '{');
    if (v instanceof ViewGroup)
      for (View c : ((ViewGroup) v).getChildren()) {
        if (c != null)
          print(c);
      }

    // System.out.println('}');
  }

  /**
   * Recursively parses the layout file and inflates the {@link View} objects to form a {@link View}
   * hierarchy.
   * 
   * @return the root of the hierarchy
   */
  public View parse() {
    View root = null;
    try {

      root = inflateView(getType());
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
      Log.e(TAG, "Error parsing layout file");
      throw new RuntimeException(e);
    }
    return root;
  }

  /**
   * Makes use of Java reflection to create an instance of a {@link View} object.
   * 
   * @param type
   *          The type of the {@link View}
   * @return The {@link View} object
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public View inflateView(String type) throws ClassNotFoundException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

    View result = null;
    // TODO change widget package if widget define in other file
    Class<View> cls = (Class<View>) Class.forName(WIDGET_PACKAGE + "." + type);
    Class[] intArgsClass = new Class[] { Context.class };
    Object[] intArgs = new Object[] { c };
    Constructor intArgsConstructor = cls.getConstructor(intArgsClass);
    result = (View) intArgsConstructor.newInstance(intArgs);

    String name = getName();
    int id = getID(name);
    result.setName(name);
    result.setID(id);
    String text = getText();
    if (!text.equals(""))
      ((TextView) result).setText(text);
    Log.i(TAG, "Inflating View:" + result.toString());

    return result;
  }

  static native String setup(int resRef);

  static native String getType();

  static native String getName();

  static native String getText();

  static native int getID(String name);

  static native boolean nextElement();

  static native String getParent();

  static native int getNumChildren();
}