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
import android.view.Window;

/**
 * Implements the native methods of the Window class. The main function of the
 * window is to keep a list of all the view-components of this window. Their
 * id's and names are looked up in the R.java file an d kept as a map
 * componenntMap and componentIdMap.
 * 
 * The componenntMap contains {@link ComponentEntry} objects. They also keep a
 * reference to the actual view objects in memory.
 * 
 * This componenntMap is mainly used to look up the reference of a object by the
 * {@link MessageQueue} 's processScriptAction() method.
 * 
 * The componentIdMap is used to lookup the {@link ComponentEntry} of a view
 * object when the object is create. The reference to the object is then stored
 * in the objects {@link ComponentEntry}.
 */
public class JPF_android_view_Window {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	static final char NAME_PREFIX = '$'; // put is front of view object's names
											// stored in the componentMap

	// this is what we use to map script actions to Components
	// static HashMap<Integer, String> componentIdMap = new HashMap<Integer,
	// String>();
	static HashMap<String, ComponentEntry> componentMap = new HashMap<String, ComponentEntry>();

	// this is what we use to map script actions to Components
	static HashMap<Integer, String> layoutMap = new HashMap<Integer, String>();

	/**
	 * Parse .xml files for gui and R file for id-name pairs
	 * 
	 * @param env
	 * @param objref
	 */
	public static void init0____V(MJIEnv env, int cref) {
		Config conf = env.getConfig();
		String rPath = conf.getString("rpath"); // TODO if specified in
		parseRFile(rPath);

	}

	private static void parseRFile(String rPath) {
		if (rPath == null) {
			log.severe("no R-file");
		}
		Scanner scanner = null;
		try {
			String next;
			String[] list;
			scanner = new Scanner(new FileInputStream(rPath));
			ComponentEntry c;
			while (scanner != null && scanner.hasNextLine()) {
				next = scanner.nextLine().trim();

				if (next.equals("public static final class id {")) {
					while (scanner.hasNextLine()) {
						next = scanner.nextLine().trim();
						if (next.equals("}"))
							break;
						list = getFields(next);
						c = new ComponentEntry();

						c.id = Integer.parseInt(list[1].substring(2), 16);
						c.name = list[0];
						componentMap.put("$" + list[0], c);
						System.out.println("insertign in map " + c.id + " "
								+ c.name);

					}
				}

				if (next.equals("public static final class layout {")) {
					while (scanner.hasNextLine()) {
						next = scanner.nextLine().trim();
						if (next.equals("}"))
							break;
						list = getFields(next);
						c = new ComponentEntry();
						c.id = Integer.parseInt(list[1].substring(2), 16);
						c.name = list[0];
						layoutMap.put(c.id, c.name);

					}
				}

			}
			c = new ComponentEntry();
			c.id = 0;
			c.name = "window";
			componentMap.put("$" + c.name, c);
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			scanner.close();
		}

	}

	private static String[] getFields(String next) {
		String[] list;
		next = next.substring(0, next.length() - 1); // gooi ; weg
		list = next.split(" ");
		next = list[list.length - 1]; // last item in list
		list = next.split("=");

		System.out.println("Parsed from R " +  list[0] + " " + list[1]);
		return list;

	}

	/**
	 * Called from Window's init. used by layout inflator to inflate layouts
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
	 * a toplevel window becomes visible - create and store its component map
	 * (id -> ref). This of course means our constraint is that Windows don't
	 * change composition once they become visible
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

	// public static void dispose0__Ljava_lang_String_2__V(MJIEnv env, int
	// objref,
	// int titleRef) {
	// String title = Integer.toString(env.getIntegerObject(titleRef), 16);
	// for (ComponentEntry e : componentMap.values()) {
	// if (e.toplevelRef == objref) {
	// componentMap.remove(e.id);
	// }
	// }
	// }

	/**
	 * Used by MessageQueue to get the reference the an object
	 * 
	 * @param name
	 * @return
	 */
	static int getViewRef(String name) {
		log.info("NAME:" + name);
		ComponentEntry e = componentMap.get(name);
		if (e != null) {
			int cref = e.componentRef;
			log.fine("Getting reference to view " + name + " => " + cref);
			return cref;
		}

		return MJIEnv.NULL;
	}

	/**
	 * Used by setContentView(int resId) method in {@link Window} to resolve the
	 * id of a layout the the name of the layout file.
	 * 
	 * @param id
	 *            the id of the layout as defined in the file R.java
	 * @return
	 */
	static String getLayoutName(int id) {
		String name = layoutMap.get(id);
		log.fine("Getting the name of Layout " + id + ": " + name);
		return name;
	}

	static void updateComponentMap(MJIEnv env, int topref, int objref,
			int level, int index, HashMap<String, ComponentEntry> map) {
		assert env.isInstanceOf(objref, "android.view.View");

		ClassInfo ci = env.getClassInfo(objref);
		storeComponent(env, topref, objref, map);

		if (ci.isInstanceOf("android.view.ViewGroup")) {
			System.out.println("is  a viewgroup ****************");
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
			HashMap<String, ComponentEntry> map) {

		String name = NAME_PREFIX + getName(env, objref);

		if (!map.containsKey(name)) {
			ComponentEntry e = new ComponentEntry(name, Integer.parseInt(name
					.substring(1, name.length() - 1)), topref, objref);
			map.put(name, e);
			log.fine("Adding component to map NAME: " + name + " => "
					+ env.getElementInfo(objref));
		} else {
			ComponentEntry c = map.get(name);
			// id is set when R-file is parsed
			c.toplevelRef = topref;
			c.componentRef = objref;
			log.fine("updating component to map NAME: " + name + " ID " + c.id
					+ " => " + env.getElementInfo(objref));
		}

	}

	/**
	 * Returns the name field of the object. If the name field is null, a name
	 * is generated.
	 * 
	 * @param env
	 * @param objref
	 * @return
	 */
	static String getName(MJIEnv env, int objref) {
		String name = null;
		int id = -1;
		if (env.isInstanceOf(objref, "android.view.View")) {
			name = env.getStringField(objref, "name");
			id = env.getIntField(objref, "mID");
			if (name == null || name.trim().equals("")) { // assume this view is
															// notreferenced
															// from code so
															// generate new name
				name = Integer.toString(count) + "_";
				env.setReferenceField(objref, "name", env.newString(name));
			}

			if (id < 0) {
				System.out.println("Changing id from " + id + "to " + count);
				id = count;
				env.setIntField(objref, "mID", id);

			}
			count = count + 1;
			System.out.println("Name: " + name + " ID: " + id);
		}
		return name;
	}

	static private int count = 1;
}
