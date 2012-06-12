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

	static final char ORDINAL_PREFIX = '#';
	static final char NAME_PREFIX = '$'; // put is front of view object's names
											// stored in the componentMap

	// this is what we use to map script actions to Components
	static HashMap<Integer, String> componentIdMap = new HashMap<Integer, String>();
	static HashMap<String, ComponentEntry> componentMap = new HashMap<String, ComponentEntry>();

	// this is what we use to map script actions to Components
	// static HashMap<Integer, ComponentEntry> layoutMap = new HashMap<String,
	// ComponentEntry>();

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
						System.out.println("id = " + list[1] + " " + c.id);
						c.name = list[0];
						componentMap.put("$" + list[0], c);
						componentIdMap.put(c.id, c.name);
						System.out.println("insertign in map " + c.id + " "
								+ c.name);

					}
				}
				//
				// if (next.equals("public static final class layout {")) {
				// while (scanner.hasNextLine()) {
				// next = scanner.nextLine().trim();
				// if (next.equals("}"))
				// break;
				// list = getFields(next);
				// c = new ComponentEntry();
				// c.id = Integer.parseInt(list[1].substring(2), 16);
				// c.name = list[0];
				// componentIdMap.put(c.id, c.name);
				// componentMap.put("%" + list[0], c);
				//
				// }
				// }

			}
			c = new ComponentEntry();
			c.id = 0;
			c.name = "window";
			componentIdMap.put(c.id, c.name);
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
		System.out.println("next " + next);
		list = next.split(" ");
		next = list[list.length - 1]; // last item in list
		list = next.split("=");

		System.out.println(list[0] + "_" + list[1]);
		return list;

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
				System.out.println("updating map");
				updateComponentMap(env, objref, objref, 0, 0, componentMap);

			}
		}
	}

	public static void dispose0__Ljava_lang_String_2__V(MJIEnv env, int objref,
			int titleRef) {
		String title = Integer.toString(env.getIntegerObject(titleRef), 16);
		for (ComponentEntry e : componentMap.values()) {
			if (e.toplevelRef == objref) {
				componentMap.remove(e.id);
			}
		}
	}

	static int getViewRef(String name) {
		log.info("NAME:" + name);

		ComponentEntry e = componentMap.get(name);
		if (e != null) {
			int cref = e.componentRef;
			System.out.println("@@@ " + name + " => " + cref);
			return cref;
		}

		return MJIEnv.NULL;
	}

	static void updateComponentMap(MJIEnv env, int topref, int objref,
			int level, int index, HashMap<String, ComponentEntry> map) {
		assert env.isInstanceOf(objref, "android.view.View");

		ClassInfo ci = env.getClassInfo(objref);
		String id = Integer.toString(map.size());
		// storeComponentId(env, map, id, topref, objref, ORDINAL_PREFIX);

		storeComponentIds(env, topref, objref, map);
		// System.out.println("classinfo " + ci);
		if (ci.isInstanceOf("android.view.ViewGroup")) {
			System.out.println("is viewgroup ****************");
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

	static void storeComponentIds(MJIEnv env, int topref, int objref,
			HashMap<String, ComponentEntry> map) {

		String id = getName(env, objref);
		if (id != null) {
			storeComponentId(env, map, id, topref, objref, NAME_PREFIX);

			// int parentRef = env.getReferenceField(objref, "mParent");
			// while (parentRef != MJIEnv.NULL) {
			// String pid = getName(env, parentRef);
			// if (pid != null) {
			// id = pid + ':' + id;
			// storeComponentId(env, map, id, topref, objref, NAME_PREFIX);
			// }
			// parentRef = env.getReferenceField(parentRef, "mParent");
			// }
		}
	}

	static void storeComponentId(MJIEnv env,
			HashMap<String, ComponentEntry> map, String id, int topref,
			int objref, char prefix) {
		id = prefix + id;
		id = id.replace(' ', '_');
		//
		// if (log.isLoggable(Level.INFO)) {
		// log.info("mapping component: " + id + " => "
		// + env.getElementInfo(objref));
		// }
		System.out.println("@@@ mapping component: " + id + " => "
				+ env.getElementInfo(objref));

		ComponentEntry e = new ComponentEntry(id, topref, objref);
		if (!map.containsKey(id)) {
			map.put(id, e);
		} else {
			ComponentEntry c = map.get(id);
			c.toplevelRef = topref;
			c.componentRef = objref;
		}
	}

	/**
	 * Returns the name field of an object. If the name field is null, a name is
	 * generated.
	 * 
	 * @param env
	 * @param objref
	 * @return
	 */
	static String getName(MJIEnv env, int objref) {
		String name = null;
		if (env.isInstanceOf(objref, "android.view.View")) {
			name = env.getStringField(objref, "name");
			System.out.println("Name " + name);
			if (name == null) { // assume this view is not referenced form code
								// so generate new name
				name = Integer.toString(count) + "_";
				env.setReferenceField(objref, "name", env.newString(name));
				count++;
			}
		}
		return name;
	}

	static private int count = 0;
}
