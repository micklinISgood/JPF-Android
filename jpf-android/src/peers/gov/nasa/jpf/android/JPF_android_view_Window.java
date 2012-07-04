package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

import android.os.MessageQueue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * Implements the native methods of the {@link Window} class. The main function
 * of this class is to keep a list of all the {@link View}-objects of the
 * application. Their id's and names are looked up in the R.java file and stored
 * as a map: <code>componentMap</code>.
 * 
 * The componentMap contains {@link ViewEntry} objects. They keep a reference to
 * the actual {@link View}-objects in memory.
 * 
 * This componentMap is mainly used to look up the reference of an object by the
 * {@link MessageQueue} 's processScriptAction() method. This method calls the
 * appropriate method on the {@link View}-object according to the action script.
 * 
 */
public class JPF_android_view_Window {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	private static String ID_HEADER = "public static final class id {";
	private static String LAYOUT_HEADER = "public static final class layout {";
	private static String FOOTER = "}";

	private static final char NAME_PREFIX = '$'; // put is front of view
													// object's names
	/**
	 * Maps script actions to {@link View} objects. The key is the name of the
	 * {@link View}-object as defined in the R.java file. The value keeps a
	 * reference to the actual {@link View}-object. This map contains all the
	 * components of the application. The R.java file requires that these names
	 * be unique.
	 */
	private static HashMap<String, ViewEntry> componentMap = new HashMap<String, ViewEntry>();

	/**
	 * Maps the resource ID of a layout to the name of the layout file. This
	 * information is read from the R.java class and stored as a map fir quick
	 * lookup by the {@link LayoutInflater}.
	 */
	private static HashMap<Integer, String> layoutMap = new HashMap<Integer, String>();

	/**
	 * Sets up the environment for the {@link Window} class. Creates
	 * componentMap and layoutMap.
	 * 
	 * @param env
	 * @param cref
	 */
	public static void init0____V(MJIEnv env, int cref) {
		Config conf = env.getConfig();
		String rPath = conf.getString("path"); // TODO get this path without it
												// being specified in the config
		if (rPath == null || rPath.equals("")) {
			log.severe("path not set in jpf.properties");
			return;
		}
		rPath = rPath + "/gen/" + JPF_android_app_ActivityThread.getPackageName().replace('.', '/') + "/R.java";
		
		parseRFile(rPath);

	}

	/**
	 * Parse the R.java file and builds the componentMap and layoutMap.
	 * 
	 * 
	 * @param rPath
	 *            the path to the R.java file on disk
	 */
	private static void parseRFile(String rPath) {
		
		Scanner scanner = null;
		String nextLine;
		try {
			scanner = new Scanner(new FileInputStream(rPath));
			while (scanner != null && scanner.hasNextLine()) {
				nextLine = scanner.nextLine().trim();
				if (nextLine.equals(ID_HEADER)) {
					parseViews(scanner);
				}
				if (nextLine.equals(LAYOUT_HEADER)) {
					parseLayouts(scanner);
				}
			}
		} catch (FileNotFoundException e) {
			log.severe("R.java file not found.");
		} finally {
			scanner.close();
		}

	}

	/**
	 * Parse the name and ID's of the {@link View} objects in the R.java file.
	 * 
	 * @param scanner
	 */
	private static void parseViews(Scanner scanner) {
		String next = "";
		String[] list;
		ViewEntry c;
		while (scanner.hasNextLine()) {
			next = scanner.nextLine().trim();
			if (next.equals(FOOTER))
				break;
			list = getFields(next);
			c = new ViewEntry();

			c.setId(Integer.parseInt(list[1].substring(2), 16));
			c.setName(list[0]);
			componentMap.put("$" + list[0], c);
			System.out.println("insertign in map " + c.getId() + " "
					+ c.getName());

		}
		// TODO Add window to the componentMap to catch window events
		c = new ViewEntry();
		c.setId(0);
		c.setName("window");
		componentMap.put("$" + c.getName(), c);

	}

	/**
	 * Parse the name and ID's of the layout file in the R.java file.
	 * 
	 * @param scanner
	 */
	private static void parseLayouts(Scanner scanner) {
		String next = "";
		String[] list;
		ViewEntry c;
		while (scanner.hasNextLine()) {
			next = scanner.nextLine().trim();
			if (next.equals(FOOTER))
				break;
			list = getFields(next);
			c = new ViewEntry();
			c.setId(Integer.parseInt(list[1].substring(2), 16));
			c.setName(list[0]);
			layoutMap.put(c.getId(), c.getName());

		}

	}

	/**
	 * Returns the NAME and ID of an object given its declaration.
	 * 
	 * @param line
	 * @return
	 */
	private static String[] getFields(String line) {
		String[] list;
		line = line.substring(0, line.length() - 1); // throw ';' away
		list = line.split(" ");
		line = list[list.length - 1]; // last item in list
		list = line.split("=");
		log.fine("Parsed from R.java NAME: " + list[0] + " ID: " + list[1]);
		return list;

	}

	/**
	 * Called from Window's init method. Returns the list of resource id's of
	 * the layout files of an application.
	 * 
	 * @param env
	 * @param objref
	 * @return
	 */
	public static int getLayouts(MJIEnv env, int objref) {
		int[] keys = new int[layoutMap.size()];
		int index = 0;
		for (Integer i : layoutMap.keySet()) {
			keys[index] = i;
			index++;
		}
		return env.newIntArray(keys);
	}

	/**
	 * Window becomes visible - create and store its component map (id -> ref).
	 * This of course means our constraint is that Windows don't change
	 * composition once they become visible //TODO
	 */
	public static void setVisible0__Ljava_lang_String_2Z__V(MJIEnv env,
			int objref, int titleRef, boolean isVisible) {

		if (isVisible) {
			String title = env.getStringObject(titleRef);
			if (!componentMap.containsKey(title)) {
				System.out
						.println("Inserting component referneces into componentMap");
				updateComponentMap(env, objref, objref, 0, 0, componentMap);

			}
		}
	}

	// TODO
	// public static void dispose0__Ljava_lang_String_2__V(MJIEnv env, int
	// objref,
	// int titleRef) {
	// String title = Integer.toString(env.getIntegerObject(titleRef), 16);
	// for (ViewEntry e : componentMap.values()) {
	// if (e.toplevelRef == objref) {
	// componentMap.remove(e.id);
	// }
	// }
	// }

	/**
	 * Used by the {@link MessageQueue} to get the reference of a {@link View}
	 * object
	 * 
	 * @param name
	 *            the name of the object
	 * @return
	 */
	static int getViewRef(String name) {
		ViewEntry e = componentMap.get(name);
		if (e != null) {
			int cref = e.getComponentRef();
			log.fine("Getting reference to View " + name + " => " + cref);
			return cref;
		} else
			log.severe("Cannot get refernce to view: " + name
					+ " view does not exist in the componentMap");

		return MJIEnv.NULL;
	}

	/**
	 * Used by setContentView(int resId) method in {@link Window} to resolve the
	 * resource id of a layout the name of the layout file.
	 * 
	 * @param id
	 *            the resource id of the layout as defined in the file R.java
	 * @return
	 */
	static String getLayoutName(int id) {
		String name = layoutMap.get(id);
		log.fine("Getting the name of Layout " + id + ": " + name);
		return name;
	}

	/**
	 * 
	 * @param env
	 * @param topref
	 * @param objref
	 * @param level
	 * @param index
	 * @param map
	 */
	static void updateComponentMap(MJIEnv env, int topref, int objref,
			int level, int index, HashMap<String, ViewEntry> map) {
		assert env.isInstanceOf(objref, "android.view.View");

		ClassInfo ci = env.getClassInfo(objref);
		storeComponent(env, topref, objref, map);

		if (ci.isInstanceOf("android.view.ViewGroup")) {
			log.fine(ci.getName() + " is  an instance of  viewgroup");
			int aref = env.getReferenceField(objref, "mChildren");
			if (aref != MJIEnv.NULL) {
				int len = env.getArrayLength(aref);
				for (int i = 0; i < len; i++) {
					int cref = env.getReferenceArrayElement(aref, i);
					if (cref != MJIEnv.NULL)
						updateComponentMap(env, topref, cref, level + 1, i, map);
				}
			}
		}
	}

	static void storeComponent(MJIEnv env, int topref, int objref,
			HashMap<String, ViewEntry> map) {

		String name = NAME_PREFIX + env.getStringField(objref, "name");
		int id = env.getIntField(objref, "mID");

		if (!map.containsKey(name)) {
			ViewEntry e = new ViewEntry(name, id, topref, objref);
			map.put(name, e);
			log.fine("Adding component to map NAME: " + name + " => "
					+ env.getElementInfo(objref));
		} else {
			ViewEntry c = map.get(name);
			// id is set when R-file is parsed
			c.setToplevelRef(topref);
			c.setComponentRef(objref);
			log.fine("updating component to map NAME: " + name + " ID "
					+ c.getId() + " => " + env.getElementInfo(objref));
		}

	}

	/**
	 * Returns the name field of a View . If the name field is null, a unique
	 * name is generated.
	 * 
	 * @param env
	 * @param objref
	 * @return
	 */
	static int getID(String name) {
		ViewEntry c = JPF_android_view_Window.componentMap.get(name);
		if (c == null)
			return -1;
		else
			return c.getId();
	}

}
