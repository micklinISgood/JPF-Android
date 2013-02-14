package android.content;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;

public class Intent {

  Activity parent;
  String mAction;
  String mType;
  ComponentName mComponent;
  HashSet<String> mCategories;
  Bundle mExtras;

  public Bundle getExtras() {
    return mExtras;
  }

  public Activity getParent() {
    return parent;
  }

  /**
   * Return the MIME data type of this intent, only if it will be needed for intent resolution. This is not
   * generally useful for application code; it is used by the frameworks for communicating with back-end
   * system services.
   * 
   * @param resolver
   *          A ContentResolver that can be used to determine the MIME type of the intent's data.
   * 
   * @return The MIME type of this intent, or null if it is unknown or not needed.
   */
  public String resolveTypeIfNeeded(ContentResolver resolver) {
    return null;
  }

  public void setParent(Activity parent) {
    this.parent = parent;
  }

  public Intent() {

  }

  public Intent(Context packageContext, Class<?> cls) {
    mComponent = new ComponentName(packageContext, cls);

  }

  public ComponentName getComponent() {
    return mComponent;
  }

  /**
   * Add a new category to the intent. Categories provide additional detail about the action the intent is
   * perform. When resolving an intent, only activities that provide <em>all</em> of the requested categories
   * will be used.
   * 
   * @param category
   *          The desired category. This can be either one of the predefined Intent categories, or a custom
   *          category in your own namespace.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #hasCategory
   * @see #removeCategory
   */
  public Intent addCategory(String category) {
    if (mCategories == null) {
      mCategories = new HashSet<String>();
    }
    mCategories.add(category.intern());
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The boolean data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getBooleanExtra(String, boolean)
   */
  public Intent putExtra(String name, boolean value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putBoolean(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The byte data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getByteExtra(String, byte)
   */
  public Intent putExtra(String name, byte value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putByte(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The char data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getCharExtra(String, char)
   */
  public Intent putExtra(String name, char value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putChar(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The short data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getShortExtra(String, short)
   */
  public Intent putExtra(String name, short value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putShort(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The integer data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getIntExtra(String, int)
   */
  public Intent putExtra(String name, int value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putInt(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The long data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getLongExtra(String, long)
   */
  public Intent putExtra(String name, long value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putLong(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The float data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getFloatExtra(String, float)
   */
  public Intent putExtra(String name, float value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putFloat(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The double data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getDoubleExtra(String, double)
   */
  public Intent putExtra(String name, double value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putDouble(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The String data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getStringExtra(String)
   */
  public Intent putExtra(String name, String value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putString(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The CharSequence data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getCharSequenceExtra(String)
   */
  public Intent putExtra(String name, CharSequence value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putCharSequence(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The ArrayList<Integer> data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getIntegerArrayListExtra(String)
   */
  public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putIntegerArrayList(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The ArrayList<String> data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getStringArrayListExtra(String)
   */
  public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putStringArrayList(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The boolean array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getBooleanArrayExtra(String)
   */
  public Intent putExtra(String name, boolean[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putBooleanArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The byte array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getByteArrayExtra(String)
   */
  public Intent putExtra(String name, byte[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putByteArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The short array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getShortArrayExtra(String)
   */
  public Intent putExtra(String name, short[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putShortArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The char array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getCharArrayExtra(String)
   */
  public Intent putExtra(String name, char[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putCharArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The int array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getIntArrayExtra(String)
   */
  public Intent putExtra(String name, int[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putIntArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The byte array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getLongArrayExtra(String)
   */
  public Intent putExtra(String name, long[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putLongArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The float array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getFloatArrayExtra(String)
   */
  public Intent putExtra(String name, float[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putFloatArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The double array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getDoubleArrayExtra(String)
   */
  public Intent putExtra(String name, double[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putDoubleArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The String array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getStringArrayExtra(String)
   */
  public Intent putExtra(String name, String[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putStringArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The CharSequence array data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getCharSequenceArrayExtra(String)
   */
  public Intent putExtra(String name, CharSequence[] value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putCharSequenceArray(name, value);
    return this;
  }

  /**
   * Add extended data to the intent. The name must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param name
   *          The name of the extra data, with package prefix.
   * @param value
   *          The Bundle data value.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #putExtras
   * @see #removeExtra
   * @see #getBundleExtra(String)
   */
  public Intent putExtra(String name, Bundle value) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putBundle(name, value);
    return this;
  }

  /**
   * Copy all extras in 'src' in to this intent.
   * 
   * @param src
   *          Contains the extras to copy.
   * 
   * @see #putExtra
   */
  public Intent putExtras(Intent src) {
    if (src.mExtras != null) {
      if (mExtras == null) {
        mExtras = new Bundle(src.mExtras);
      } else {
        mExtras.putAll(src.mExtras);
      }
    }
    return this;
  }

  /**
   * Add a set of extended data to the intent. The keys must include a package prefix, for example the app
   * com.android.contacts would use names like "com.android.contacts.ShowAll".
   * 
   * @param extras
   *          The Bundle of extras to add to this intent.
   * 
   * @see #putExtra
   * @see #removeExtra
   */
  public Intent putExtras(Bundle extras) {
    if (mExtras == null) {
      mExtras = new Bundle();
    }
    mExtras.putAll(extras);
    return this;
  }

  /**
   * Completely replace the extras in the Intent with the extras in the given Intent.
   * 
   * @param src
   *          The exact extras contained in this Intent are copied into the target intent, replacing any that
   *          were previously there.
   */
  public Intent replaceExtras(Intent src) {
    mExtras = src.mExtras != null ? new Bundle(src.mExtras) : null;
    return this;
  }

  /**
   * Completely replace the extras in the Intent with the given Bundle of extras.
   * 
   * @param extras
   *          The new set of extras in the Intent, or null to erase all extras.
   */
  public Intent replaceExtras(Bundle extras) {
    mExtras = extras != null ? new Bundle(extras) : null;
    return this;
  }

  /**
   * Remove extended data from the intent.
   * 
   * @see #putExtra
   */
  public void removeExtra(String name) {
    if (mExtras != null) {
      mExtras.remove(name);
      if (mExtras.size() == 0) {
        mExtras = null;
      }
    }
  }

  /**
   * (Usually optional) Explicitly set the component to handle the intent. If left with the default value of
   * null, the system will determine the appropriate class to use based on the other fields (action, data,
   * type, categories) in the Intent. If this class is defined, the specified class will always be used
   * regardless of the other fields. You should only set this value when you know you absolutely want a
   * specific class to be used; otherwise it is better to let the system find the appropriate class so that
   * you will respect the installed applications and user preferences.
   * 
   * @param component
   *          The name of the application component to handle the intent, or null to let the system find one
   *          for you.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #setClass
   * @see #setClassName(Context, String)
   * @see #setClassName(String, String)
   * @see #getComponent
   * @see #resolveActivity
   */
  public void setComponent(ComponentName component) {
    // System.out.println("setting component to: " + component);
    mComponent = component;
  }

  /**
   * Set the general action to be performed.
   * 
   * @param action
   *          An action name, such as ACTION_VIEW. Application-specific actions should be prefixed with the
   *          vendor's package name.
   * 
   * @return Returns the same Intent object, for chaining multiple calls into a single statement.
   * 
   * @see #getAction
   */
  public Intent setAction(String action) {
    mAction = action != null ? action.intern() : null;
    return this;
  }

  /**
   * Retrieve the general action to be performed, such as {@link #ACTION_VIEW} . The action describes the
   * general way the rest of the information in the intent should be interpreted -- most importantly, what to
   * do with the data returned by {@link #getData}.
   * 
   * @return The action of this intent or null if none is specified.
   * 
   * @see #setAction
   */
  public String getAction() {
    return mAction;
  }

  /**
   * Return the set of all categories in the intent. If there are no categories, returns NULL.
   * 
   * @return The set of categories you can examine. Do not modify!
   * 
   * @see #hasCategory
   * @see #addCategory
   */
  public Set<String> getCategories() {
    return mCategories;
  }

  public void setExtrasClassLoader(ClassLoader classLoader) {
    // TODO stub

  }

  public void setAllowFds(boolean b) {
    // TODO stub

  }

}
