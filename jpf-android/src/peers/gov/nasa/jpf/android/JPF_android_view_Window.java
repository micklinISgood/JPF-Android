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

/**
 * some native methods we need to keep track of toplevels to analyze
 */
public class JPF_android_view_Window {

	static final char ORDINAL_PREFIX = '#';
	static final char NAME_PREFIX = '$';

	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	static class ComponentEntry {
		int id;
		String name;
		int componentRef;
		int toplevelRef;

		public ComponentEntry() {
		}

		ComponentEntry(String name, int toplevelRef, int componentRef) {
			this.name = name;
			this.componentRef = componentRef;
			this.toplevelRef = toplevelRef;
		}

		ComponentEntry(String name, int id, int toplevelRef, int componentRef) {
			this.id = id;
			this.name = name;
			this.componentRef = componentRef;
			this.toplevelRef = toplevelRef;
		}

	}

	/**
	 * Parse .xml files for gui and R file for id-name pairs
	 * 
	 * @param env
	 * @param objref
	 */
	public static void init____V(MJIEnv env, int objref) {
		Config conf = env.getConfig();
		String rPath = conf.getString("rpath"); // TODO if specified in
												// site.properties???
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

	// public static void setContentView(MJIEnv env, int objref, int layoutId,
	// int rootref) {
	// ComponentEntry layout = layoutMap.get(layoutId);
	// ViewGroup v = env.get
	//
	// //lookup path+name of layout file
	// //open file and retrieve the contents
	// // build list of attributes
	//
	// static int createJPFFile(MJIEnv env, File file) {
	// int newFileRef = env.newObject("java.io.File");
	// ElementInfo fileEI = env.getElementInfo(newFileRef);
	//
	// int fileNameRef = env.newString(file.getPath());
	// fileEI.setReferenceField("filename", fileNameRef);
	//
	// return newFileRef;
	// }
	//
	// }

	// this is what we use to map script actions to Components
	static HashMap<Integer, String> componentIdMap = new HashMap<Integer, String>();
	static HashMap<String, ComponentEntry> componentMap = new HashMap<String, ComponentEntry>();
	// this is what we use to map script actions to Components
	// static HashMap<Integer, ComponentEntry> layoutMap = new HashMap<String,
	// ComponentEntry>();
	// this is what we use to map script actions to Components
	static HashMap<String, ComponentEntry> activityMap = new HashMap<String, ComponentEntry>();

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

	public static void setTitle0__Ljava_lang_String_2Ljava_lang_String_2__V(
			MJIEnv env, int objref, int oldTitleRef, int newTitleRef) {
		String oldTitle = env.getStringObject(oldTitleRef);
		String newTitle = env.getStringObject(newTitleRef);

		// <2do> we don't deal with this yet - not sure if we should change the
		// component ids
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

	static String getName(MJIEnv env, int objref) {
		String name = null;

		if (env.isInstanceOf(objref, "android.view.View")) {

			// 'id' field has precedence
			int id = env.getIntField(objref, "mID");
			System.out.println("IDIDIDIDDIDI: " + id);
			name = componentIdMap.get(id);
			System.out.println("Name " + name);
			// if we don't have a name, try to deduce it from type specific
			// fields
			// if (id == null) { // This component is not referenced so it has
			// no id field
			// // <2do> we need to support a lot more
			// if (env.isInstanceOf(objref, "javax.swing.JFrame")) {
			// id = env.getStringField(objref, "title");
			//
			// } else if (env.isInstanceOf(objref, "javax.swing.JLabel")) {
			// id = env.getStringField(objref, "text");
			//
			// } else if (env.isInstanceOf(objref,
			// "javax.swing.AbstractButton")) {
			// id = env.getStringField(objref, "text");
			//
			// } else if (env.isInstanceOf(objref,
			// "javax.swing.text.JTextComponent")) {
			// int lblRef = env.getReferenceField(objref, "labeledBy");
			// if (lblRef != MJIEnv.NULL) {
			// id = env.getStringField(lblRef, "text");
			// id += ":input";
			// } else {
			// id = "input";
			// }
			//
			// } else if (env.isInstanceOf(objref, "javax.swing.JList")) {
			// id = "list";
			//
			// } else if (env.isInstanceOf(objref, "javax.swing.JTable")) {
			// id = "table";
			//
			// } else if (env.isInstanceOf(objref, "javax.swing.JComponent")) {
			//
			// // <2do> we might check this for JLists and JTables too
			// int borderRef = env.getReferenceField(objref, "border");
			// if (borderRef != MJIEnv.NULL
			// && env.isInstanceOf(borderRef,
			// "javax.swing.border.TitledBorder")) {
			// id = env.getStringField(borderRef, "title");
			// }
			// }
			// }
			//
			// if (id != null) {
			// id = id.replace(' ', '_');
			// }
			if (name == null) { // word nie ge-refence nie
				name = Integer.toString(count) + "_";
				count++;
			}
		}

		return name;
	}

	static private int count = 0;
}
