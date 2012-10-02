package android.content.res;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.AttributeSet;

public class Resources {
  static final String TAG = "Resources";
  Configuration mConfiguration = new Configuration();

  public static int selectDefaultTheme(int curTheme, int targetSdkVersion) {
    return 0;
  }

  public static int selectSystemTheme(int curTheme, int targetSdkVersion, int orig, int holo,
                                      int deviceDefault) {
    return 0;
  }

  /**
   * This exception is thrown by the resource APIs when a requested resource can not be found.
   */
  public static class NotFoundException extends RuntimeException {
    public NotFoundException() {
    }

    public NotFoundException(String name) {
      super(name);
    }
  }

  /**
   * Create a new Resources object on top of an existing set of assets in an AssetManager.
   * 
   * @param assets
   *          Previously created AssetManager.
   * @param metrics
   *          Current display metrics to consider when selecting/computing resource values.
   * @param config
   *          Desired device configuration to consider when selecting/computing resource values (optional).
   */
  public Resources(Configuration config) {
    mConfiguration = config;
  }

  /**
   * Return a global shared Resources object that provides access to only system resources (no application
   * resources), and is not configured for the current screen (can not use dimension units, does not change
   * based on orientation, etc).
   */
  public static Resources getSystem() {
    return null;
  }

  /**
   * Return the string value associated with a particular resource ID. The returned object will be a String if
   * this is a plain string; it will be some other type of CharSequence if it is styled. {@more}
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return CharSequence The string data associated with the resource, plus possibly styled text information.
   */
  public CharSequence getText(int id) throws NotFoundException {

    return null;
  }

  /**
   * Return the character sequence associated with a particular resource ID for a particular numerical
   * quantity.
   * 
   * <p>
   * See <a href="{@docRoot}guide/topics/resources/string-resource.html#Plurals">String Resources</a> for more
   * on quantity strings.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @param quantity
   *          The number used to get the correct string for the current language's plural rules.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return CharSequence The string data associated with the resource, plus possibly styled text information.
   */
  public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
    return null;
  }

  /**
   * Return the string value associated with a particular resource ID. It will be stripped of any styled text
   * information. {@more}
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return String The string data associated with the resource, stripped of styled text information.
   */
  public String getString(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return the string value associated with a particular resource ID, substituting the format arguments as
   * defined in {@link java.util.Formatter} and {@link java.lang.String#format}. It will be stripped of any
   * styled text information. {@more}
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @param formatArgs
   *          The format arguments that will be used for substitution.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return String The string data associated with the resource, stripped of styled text information.
   */
  public String getString(int id, Object... formatArgs) throws NotFoundException {
    return null;
  }

  /**
   * Return the string value associated with a particular resource ID for a particular numerical quantity,
   * substituting the format arguments as defined in {@link java.util.Formatter} and
   * {@link java.lang.String#format}. It will be stripped of any styled text information. {@more}
   * 
   * <p>
   * See <a href="{@docRoot}guide/topics/resources/string-resource.html#Plurals">String Resources</a> for more
   * on quantity strings.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @param quantity
   *          The number used to get the correct string for the current language's plural rules.
   * @param formatArgs
   *          The format arguments that will be used for substitution.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return String The string data associated with the resource, stripped of styled text information.
   */
  public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
    return null;
  }

  /**
   * Return the string value associated with a particular resource ID for a particular numerical quantity.
   * 
   * <p>
   * See <a href="{@docRoot}guide/topics/resources/string-resource.html#Plurals">String Resources</a> for more
   * on quantity strings.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @param quantity
   *          The number used to get the correct string for the current language's plural rules.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return String The string data associated with the resource, stripped of styled text information.
   */
  public String getQuantityString(int id, int quantity) throws NotFoundException {
    return null;
  }

  /**
   * Return the string value associated with a particular resource ID. The returned object will be a String if
   * this is a plain string; it will be some other type of CharSequence if it is styled.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @param def
   *          The default CharSequence to return.
   * 
   * @return CharSequence The string data associated with the resource, plus possibly styled text information,
   *         or def if id is 0 or not found.
   */
  public CharSequence getText(int id, CharSequence def) {
    return null;
  }

  /**
   * Return the styled text array associated with a particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return The styled text array associated with the resource.
   */
  public CharSequence[] getTextArray(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return the string array associated with a particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return The string array associated with the resource.
   */
  public String[] getStringArray(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return the int array associated with a particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return The int array associated with the resource.
   */
  public int[] getIntArray(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return an array of heterogeneous values.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return Returns a TypedArray holding an array of the array values. Be sure to call
   *         {@link TypedArray#recycle() TypedArray.recycle()} when done with it.
   */
  public TypedArray obtainTypedArray(int id) throws NotFoundException {
    return null;
  }

  /**
   * Retrieve a dimensional for a particular resource ID. Unit conversions are based on the current
   * {@link DisplayMetrics} associated with the resources.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @return Resource dimension value multiplied by the appropriate metric.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getDimensionPixelOffset
   * @see #getDimensionPixelSize
   */
  public float getDimension(int id) throws NotFoundException {
    return id;
  }

  /**
   * Retrieve a dimensional for a particular resource ID for use as an offset in raw pixels. This is the same
   * as {@link #getDimension}, except the returned value is converted to integer pixels for you. An offset
   * conversion involves simply truncating the base value to an integer.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @return Resource dimension value multiplied by the appropriate metric and truncated to integer pixels.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getDimension
   * @see #getDimensionPixelSize
   */
  public int getDimensionPixelOffset(int id) throws NotFoundException {
    return id;
  }

  /**
   * Retrieve a dimensional for a particular resource ID for use as a size in raw pixels. This is the same as
   * {@link #getDimension}, except the returned value is converted to integer pixels for use as a size. A size
   * conversion involves rounding the base value, and ensuring that a non-zero base value is at least one
   * pixel in size.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @return Resource dimension value multiplied by the appropriate metric and truncated to integer pixels.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getDimension
   * @see #getDimensionPixelOffset
   */
  public int getDimensionPixelSize(int id) throws NotFoundException {
    return id;
  }

  /**
   * Retrieve a fractional unit for a particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @param base
   *          The base value of this fraction. In other words, a standard fraction is multiplied by this
   *          value.
   * @param pbase
   *          The parent base value of this fraction. In other words, a parent fraction (nn%p) is multiplied
   *          by this value.
   * 
   * @return Attribute fractional value multiplied by the appropriate base value.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   */
  public float getFraction(int id, int base, int pbase) {
    return pbase;
  }

  /**
   * Return a drawable object associated with a particular resource ID. Various types of objects will be
   * returned depending on the underlying resource -- for example, a solid color, PNG image, scalable image,
   * etc. The Drawable API hides these implementation details.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return Drawable An object that can be used to draw this resource.
   */
  public Drawable getDrawable(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return a drawable object associated with a particular resource ID for the given screen density in DPI.
   * This will set the drawable's density to be the device's density multiplied by the ratio of actual
   * drawable density to requested density. This allows the drawable to be scaled up to the correct size if
   * needed. Various types of objects will be returned depending on the underlying resource -- for example, a
   * solid color, PNG image, scalable image, etc. The Drawable API hides these implementation details.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @param density
   *          the desired screen density indicated by the resource as found in {@link DisplayMetrics}.
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * @return Drawable An object that can be used to draw this resource.
   */
  public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
    return null;

  }

  /**
   * Return a movie object associated with the particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   */
  public Movie getMovie(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return a color integer associated with a particular resource ID. If the resource holds a complex
   * {@link android.content.res.ColorStateList}, then the default color from the set is returned.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return Returns a single color value in the form 0xAARRGGBB.
   */
  public int getColor(int id) throws NotFoundException {
    return id;
  }

  /**
   * Return a color state list associated with a particular resource ID. The resource may contain either a
   * single raw color value, or a complex {@link android.content.res.ColorStateList} holding multiple possible
   * colors.
   * 
   * @param id
   *          The desired resource identifier of a {@link ColorStateList}, as generated by the aapt tool. This
   *          integer encodes the package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return Returns a ColorStateList object containing either a single solid color or multiple colors that
   *         can be selected based on a state.
   */
  public ColorStateList getColorStateList(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return a boolean associated with a particular resource ID. This can be used with any integral resource
   * value, and will return true if it is non-zero.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return Returns the boolean value contained in the resource.
   */
  public boolean getBoolean(int id) throws NotFoundException {
    return false;
  }

  /**
   * Return an integer associated with a particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return Returns the integer value contained in the resource.
   */
  public int getInteger(int id) throws NotFoundException {
    return 0;
  }

  /**
   * Return an XmlResourceParser through which you can read a view layout description for the given resource
   * ID. This parser has limited functionality -- in particular, you can't change its input, and only the
   * high-level events are available.
   * 
   * <p>
   * This function is really a simple wrapper for calling {@link #getXml} with a layout resource.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return A new parser object through which you can read the XML data.
   * 
   * @see #getXml
   */
  public XmlResourceParser getLayout(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return an XmlResourceParser through which you can read an animation description for the given resource
   * ID. This parser has limited functionality -- in particular, you can't change its input, and only the
   * high-level events are available.
   * 
   * <p>
   * This function is really a simple wrapper for calling {@link #getXml} with an animation resource.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return A new parser object through which you can read the XML data.
   * 
   * @see #getXml
   */
  public XmlResourceParser getAnimation(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return an XmlResourceParser through which you can read a generic XML resource for the given resource ID.
   * 
   * <p>
   * The XmlPullParser implementation returned here has some limited functionality. In particular, you can't
   * change its input, and only high-level parsing events are available (since the document was pre-parsed for
   * you at build time, which involved merging text and stripping comments).
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @return A new parser object through which you can read the XML data.
   * 
   * @see android.util.AttributeSet
   */
  public XmlResourceParser getXml(int id) throws NotFoundException {
    return loadXmlResourceParser(id, "xml");
  }

  /**
   * Open a data stream for reading a raw resource. This can only be used with resources whose value is the
   * name of an asset files -- that is, it can be used to open drawable, sound, and raw resources; it will
   * fail on string and color resources.
   * 
   * @param id
   *          The resource identifier to open, as generated by the appt tool.
   * 
   * @return InputStream Access to the resource data.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   */
  public InputStream openRawResource(int id) throws NotFoundException {
    return null;
  }

  /**
   * Open a data stream for reading a raw resource. This can only be used with resources whose value is the
   * name of an asset file -- that is, it can be used to open drawable, sound, and raw resources; it will fail
   * on string and color resources.
   * 
   * @param id
   *          The resource identifier to open, as generated by the appt tool.
   * @param value
   *          The TypedValue object to hold the resource information.
   * 
   * @return InputStream Access to the resource data.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   */
  public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
    return null;
  }

  /**
   * Open a file descriptor for reading a raw resource. This can only be used with resources whose value is
   * the name of an asset files -- that is, it can be used to open drawable, sound, and raw resources; it will
   * fail on string and color resources.
   * 
   * <p>
   * This function only works for resources that are stored in the package as uncompressed data, which
   * typically includes things like mp3 files and png images.
   * 
   * @param id
   *          The resource identifier to open, as generated by the appt tool.
   * 
   * @return AssetFileDescriptor A new file descriptor you can use to read the resource. This includes the
   *         file descriptor itself, as well as the offset and length of data where the resource appears in
   *         the file. A null is returned if the file exists but is compressed.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   */
  public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
    return null;
  }

  /**
   * Return the raw data associated with a particular resource ID.
   * 
   * @param id
   *          The desired resource identifier, as generated by the aapt tool. This integer encodes the
   *          package, type, and resource entry. The value 0 is an invalid identifier.
   * @param outValue
   *          Object in which to place the resource data.
   * @param resolveRefs
   *          If true, a resource that is a reference to another resource will be followed so that you receive
   *          the actual final resource data. If false, the TypedValue will be filled in with the reference
   *          itself.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   */
  public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
  }

  /**
   * Get the raw value associated with a resource with associated density.
   * 
   * @param id
   *          resource identifier
   * @param density
   *          density in DPI
   * @param resolveRefs
   *          If true, a resource that is a reference to another resource will be followed so that you receive
   *          the actual final resource data. If false, the TypedValue will be filled in with the reference
   *          itself.
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * @see #getValue(String, TypedValue, boolean)
   */
  public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs)
      throws NotFoundException {
  }

  /**
   * Return the raw data associated with a particular resource ID. See getIdentifier() for information on how
   * names are mapped to resource IDs, and getString(int) for information on how string resources are
   * retrieved.
   * 
   * <p>
   * Note: use of this function is discouraged. It is much more efficient to retrieve resources by identifier
   * than by name.
   * 
   * @param name
   *          The name of the desired resource. This is passed to getIdentifier() with a default type of
   *          "string".
   * @param outValue
   *          Object in which to place the resource data.
   * @param resolveRefs
   *          If true, a resource that is a reference to another resource will be followed so that you receive
   *          the actual final resource data. If false, the TypedValue will be filled in with the reference
   *          itself.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   */
  public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
  }

  /**
   * This class holds the current attribute values for a particular theme. In other words, a Theme is a set of
   * values for resource attributes; these are used in conjunction with {@link TypedArray} to resolve the
   * final value for an attribute.
   * 
   * <p>
   * The Theme's attributes come into play in two ways: (1) a styled attribute can explicit reference a value
   * in the theme through the "?themeAttribute" syntax; (2) if no value has been defined for a particular
   * styled attribute, as a last resort we will try to find that attribute's value in the Theme.
   * 
   * <p>
   * You will normally use the {@link #obtainStyledAttributes} APIs to retrieve XML attributes with style and
   * theme information applied.
   */
  public final class Theme {
    Theme() {
    }

  }

  /**
   * Generate a new Theme object for this set of Resources. It initially starts out empty.
   * 
   * @return Theme The newly created Theme container.
   */
  public final Theme newTheme() {
    return null;
  }

  /**
   * Retrieve a set of basic attribute values from an AttributeSet, not performing styling of them using a
   * theme and/or style resources.
   * 
   * @param set
   *          The current attribute values to retrieve.
   * @param attrs
   *          The specific attributes to be retrieved.
   * @return Returns a TypedArray holding an array of the attribute values. Be sure to call
   *         {@link TypedArray#recycle() TypedArray.recycle()} when done with it.
   * 
   * @see Theme#obtainStyledAttributes(AttributeSet, int[], int, int)
   */
  public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {

    return null;
  }

  /**
   * Store the newly updated configuration.
   */
  public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
  }

  /**
   * @hide
   */
  public void updateConfiguration(Configuration config, DisplayMetrics metrics, CompatibilityInfo compat) {
  }

  /**
   * Update the system resources configuration if they have previously been initialized.
   * 
   * @hide
   */
  public static void updateSystemConfiguration(Configuration config, DisplayMetrics metrics,
                                               CompatibilityInfo compat) {
  }

  /**
   * @hide
   */
  public static void updateSystemConfiguration(Configuration config, DisplayMetrics metrics) {
  }

  /**
   * Return the current display metrics that are in effect for this resource object. The returned object
   * should be treated as read-only.
   * 
   * @return The resource's current display metrics.
   */
  public DisplayMetrics getDisplayMetrics() {
    return null;
  }

  /**
   * Return the current configuration that is in effect for this resource object. The returned object should
   * be treated as read-only.
   * 
   * @return The resource's current configuration.
   */
  public Configuration getConfiguration() {
    return null;
  }

  /**
   * Return the compatibility mode information for the application. The returned object should be treated as
   * read-only.
   * 
   * @return compatibility info.
   * @hide
   */
  public CompatibilityInfo getCompatibilityInfo() {
    return null;
  }

  /**
   * This is just for testing.
   * 
   * @hide
   */
  public void setCompatibilityInfo(CompatibilityInfo ci) {
  }

  /**
   * Return a resource identifier for the given resource name. A fully qualified resource name is of the form
   * "package:type/entry". The first two components (package and type) are optional if defType and defPackage,
   * respectively, are specified here.
   * 
   * <p>
   * Note: use of this function is discouraged. It is much more efficient to retrieve resources by identifier
   * than by name.
   * 
   * @param name
   *          The name of the desired resource.
   * @param defType
   *          Optional default resource type to find, if "type/" is not included in the name. Can be null to
   *          require an explicit type.
   * @param defPackage
   *          Optional default package to find, if "package:" is not included in the name. Can be null to
   *          require an explicit package.
   * 
   * @return int The associated resource identifier. Returns 0 if no such resource was found. (0 is not a
   *         valid resource ID.)
   */
  public int getIdentifier(String name, String defType, String defPackage) {
    return 0;
  }

  /**
   * Return the full name for a given resource identifier. This name is a single string of the form
   * "package:type/entry".
   * 
   * @param resid
   *          The resource identifier whose name is to be retrieved.
   * 
   * @return A string holding the name of the resource.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getResourcePackageName
   * @see #getResourceTypeName
   * @see #getResourceEntryName
   */
  public String getResourceName(int resid) throws NotFoundException {
    return null;
  }

  /**
   * Return the package name for a given resource identifier.
   * 
   * @param resid
   *          The resource identifier whose package name is to be retrieved.
   * 
   * @return A string holding the package name of the resource.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getResourceName
   */
  public String getResourcePackageName(int resid) throws NotFoundException {
    return null;
  }

  /**
   * Return the type name for a given resource identifier.
   * 
   * @param resid
   *          The resource identifier whose type name is to be retrieved.
   * 
   * @return A string holding the type name of the resource.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getResourceName
   */
  public String getResourceTypeName(int resid) throws NotFoundException {
    return null;
  }

  /**
   * Return the entry name for a given resource identifier.
   * 
   * @param resid
   *          The resource identifier whose entry name is to be retrieved.
   * 
   * @return A string holding the entry name of the resource.
   * 
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * 
   * @see #getResourceName
   */
  public String getResourceEntryName(int resid) throws NotFoundException {
    return null;
  }

  /**
   * Parse a series of {@link android.R.styleable#Extra &lt;extra&gt;} tags from an XML file. You call this
   * when you are at the parent tag of the extra tags, and it will return once all of the child tags have been
   * parsed. This will call {@link #parseBundleExtra} for each extra tag encountered.
   * 
   * @param parser
   *          The parser from which to retrieve the extras.
   * @param outBundle
   *          A Bundle in which to place all parsed extras.
   * @throws XmlPullParserException
   * @throws IOException
   */
  public void parseBundleExtras(XmlResourceParser parser, android.os.Bundle outBundle)
      throws XmlPullParserException, IOException {
  }

  /**
   * Parse a name/value pair out of an XML tag holding that data. The AttributeSet must be holding the data
   * defined by {@link android.R.styleable#Extra}. The following value types are supported:
   * <ul>
   * <li> {@link TypedValue#TYPE_STRING}: {@link Bundle#putCharSequence Bundle.putCharSequence()}
   * <li> {@link TypedValue#TYPE_INT_BOOLEAN}: {@link Bundle#putCharSequence Bundle.putBoolean()}
   * <li> {@link TypedValue#TYPE_FIRST_INT}-{@link TypedValue#TYPE_LAST_INT}: {@link Bundle#putCharSequence
   * Bundle.putBoolean()}
   * <li> {@link TypedValue#TYPE_FLOAT}: {@link Bundle#putCharSequence Bundle.putFloat()}
   * </ul>
   * 
   * @param tagName
   *          The name of the tag these attributes come from; this is only used for reporting error messages.
   * @param attrs
   *          The attributes from which to retrieve the name/value pair.
   * @param outBundle
   *          The Bundle in which to place the parsed value.
   * @throws XmlPullParserException
   *           If the attributes are not valid.
   */
  public void parseBundleExtra(String tagName, AttributeSet attrs, android.os.Bundle outBundle)
      throws XmlPullParserException {
  }

  /**
   * Retrieve underlying AssetManager storage for these resources.
   */
  public final AssetManager getAssets() {
    return null;
  }

  /**
   * Call this to remove all cached loaded layout resources from the Resources object. Only intended for use
   * with performance testing tools.
   */
  public final void flushLayoutCache() {
  }

  /**
   * Start preloading of resource data using this Resources object. Only for use by the zygote process for
   * loading common system resources. {@hide}
   */
  public final void startPreloading() {
  }

  /**
   * Called by zygote when it is done preloading resources, to change back to normal Resources operation.
   */
  public final void finishPreloading() {
  }

  Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {
    return null;
  }

  ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException {

    return null;
  }

  XmlResourceParser loadXmlResourceParser(int id, String type) throws NotFoundException {
    return null;
  }

  XmlResourceParser loadXmlResourceParser(String file, int id, int assetCookie, String type)
      throws NotFoundException {
    return null;
  }

}
