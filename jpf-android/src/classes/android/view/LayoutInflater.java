package android.view;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.xml.sax.SAXException;

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
  private static int TYPE = 0;
  private static int ID = 1;
  private static int NAME = 2;
  private static int HASHCODE = 3;
  private static int TEXT = 4;

  /** Not sure what context to store here TODO */
  Context c;
  
  /**
   * Used to create an unique name and id field for components that are not
   * named in the R.java file. Window
   * has count 0.
   */
  private static int count = 1;

  public LayoutInflater(Context c) {
    this.c = c;

  }

  /**
   * Obtains the LayoutInflater from the given context.
   */
  public static LayoutInflater from(Context context) {
    LayoutInflater layoutInflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (layoutInflater == null) {
      throw new AssertionError("LayoutInflater not found.");
    }
    return layoutInflater;
  }

  /**
   * Inflate a new view hierarchy from the specified xml resource. Throws
   * {@link InflateException} if there is an error.
   * 
   * @param resource
   *          ID for an XML layout resource to load (e.g.,
   *          <code>R.layout.main_page</code>)
   * @param root
   *          Optional view to be the parent of the generated hierarchy.
   * @return The root View of the inflated hierarchy. If root was supplied,
   *         this is the root View; otherwise it is the root of the inflated
   *         XML file.
   */
  public View inflate(int resourceID, ViewGroup root) {
    try {
      String filename = loadLayout(resourceID);
      int rootNativeHash = getRootHash(resourceID);

      View view = visit(rootNativeHash, resourceID);
     // print(view, "\t");

      if (root != null && view != null)
        root.addView(view);
      else
        return view;

    } catch (Exception e) {
      Log.e(TAG, "Error inflating layout file " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return root;

  }

  private native int getRootHash(int resourceID);

  private native String loadLayout(int resourceID);

  public View visit(int nodeCode, int resourceID) throws SAXException {
    if (nodeCode == -1)
      return null;

    // set the info of the view
    String[] node = getNodeInfo(nodeCode, resourceID);

    View view = null;
    try {
      //inflate the view
      view = inflateView(node);

      // visit the children of the view
      int[] childrenIds = getChildren(nodeCode, resourceID);
      View child = null;
      if (childrenIds != null) {
        for (int id : childrenIds) {
          //visit the child
          child = visit(id, resourceID);
          //add child to parent
          ((ViewGroup) view).addView(child);
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "Error inflating view " + e.getMessage());
      throw new RuntimeException(e);
    }
    return view;
  }

  public native String[] getNodeInfo(int hash, int resourceID);

  public native int[] getChildren(int hash, int resourceID);

  private void print(View v, String space) {
    if (v == null) {
      System.out.println("NULL VIEW");
      return;
    }
    System.out.print(v.toString() + "{");
    if (v instanceof ViewGroup) {
      for (View c : ((ViewGroup) v).getChildren()) {
        if (c != null) {
          System.out.print("\n");
          System.out.print(space);
          print(c, space + "\t");
        }
      }
    }

    System.out.print("}");
  }

  /**
   * Makes use of Java reflection to create an instance of a {@link View}
   * object.
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
  public View inflateView(String[] info) throws ClassNotFoundException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

    View view = null;
    // TODO change widget package if widget define in other file

    //inflate the view
    Class<? extends View> cls = (Class<? extends View>) Class.forName(WIDGET_PACKAGE + "." + info[TYPE]);
    Class[] intArgsClass = new Class[] { Context.class };
    Object[] intArgs = new Object[] { c };
    Constructor intArgsConstructor = cls.getConstructor(intArgsClass);
    view = (View) intArgsConstructor.newInstance(intArgs);

    //setGenericProperties
    view.setName(info[NAME]);
    view.setId(Integer.parseInt(info[ID]));
    view.setNativeHashCode(Integer.parseInt(info[HASHCODE]));
    if (!info[TEXT].equals(""))
      ((TextView) view).setText(info[TEXT]);

    Log.v(TAG, "Inflated " + view.toString());

    return view;
  }

}