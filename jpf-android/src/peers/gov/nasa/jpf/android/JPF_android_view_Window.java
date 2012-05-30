package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MJIEnv;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPF_android_view_Window {
	static final char ORDINAL_PREFIX = '#';
	static final char NAME_PREFIX = '$';

	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	static class ViewEntry {
		String id;
		int viewRef;
		int toplevelRef;

		ViewEntry(String id, int toplevelRef, int viewRef) {
			this.id = id;
			this.viewRef = viewRef;
			this.toplevelRef = toplevelRef;
		}
	}

	// this is what we use to map script actions to views
	static HashMap<String, ViewEntry> viewMap = new HashMap<String, ViewEntry>();

	/**
	 * a toplevel window becomes visible - create and store its view map
	 * (id -> ref). This of course means our constraint is that Windows don't
	 * change composition once they become visible
	 */
	public static void setVisible0__Ljava_lang_String_2Z__V(MJIEnv env,
			int objref, int titleRef, boolean isVisible) {

		if (isVisible) {
			String title = env.getStringObject(titleRef);

			if (!viewMap.containsKey(title)) {
				updateViewMap(env, objref, objref, 0, 0, viewMap);
			}
		}
	}

	public static void setTitle0__Ljava_lang_String_2Ljava_lang_String_2__V(
			MJIEnv env, int objref, int oldTitleRef, int newTitleRef) {
		String oldTitle = env.getStringObject(oldTitleRef);
		String newTitle = env.getStringObject(newTitleRef);

		// <2do> we don't deal with this yet - not sure if we should change the
		// view ids
	}

	public static void dispose0__Ljava_lang_String_2__V(MJIEnv env, int objref,
			int titleRef) {
		for (ViewEntry e : viewMap.values()) {
			if (e.toplevelRef == objref) {
				viewMap.remove(e.id);
			}
		}
	}

	static int getViewRef(String id) {
		ViewEntry e = viewMap.get(id);
		if (e != null) {
			int cref = e.viewRef;
			// System.out.println("@@@ " + id + " => " + cref);
			return cref;
		}

		return MJIEnv.NULL;
	}

	static void updateViewMap(MJIEnv env, int topref, int objref,
			int level, int index, HashMap<String, ViewEntry> map) {
		assert env.isInstanceOf(objref, "android.view.View");

		ClassInfo ci = env.getClassInfo(objref);
		String id = Integer.toString(map.size());
		storeViewId(env, map, id, topref, objref, ORDINAL_PREFIX);

		storeViewIds(env, topref, objref, map);

		if (ci.isInstanceOf("java.awt.Container")) {
			int aref = env.getReferenceField(objref, "view");
			if (aref != MJIEnv.NULL) {
				int len = env.getArrayLength(aref);
				for (int i = 0; i < len; i++) {
					int cref = env.getReferenceArrayElement(aref, i);
					updateViewMap(env, topref, cref, level + 1, i, map);
				}
			}
		}
	}

	static void storeViewIds(MJIEnv env, int topref, int objref,
			HashMap<String, ViewEntry> map) {

		String id = getId(env, objref);
		if (id != null) {
			storeViewId(env, map, id, topref, objref, NAME_PREFIX);

			int parentRef = env.getReferenceField(objref, "parent");
			while (parentRef != MJIEnv.NULL) {
				String pid = getId(env, parentRef);
				if (pid != null) {
					id = pid + ':' + id;
					storeViewId(env, map, id, topref, objref, NAME_PREFIX);
				}
				parentRef = env.getReferenceField(parentRef, "parent");
			}
		}
	}

	static void storeViewId(MJIEnv env,
			HashMap<String, ViewEntry> map, String id, int topref,
			int objref, char prefix) {
		id = prefix + id;
		id = id.replace(' ', '_');

		if (log.isLoggable(Level.FINE)) {
			log.fine("mapping view: " + id + " => "
					+ env.getElementInfo(objref));
		}
		// System.out.println("@@@ mapping view: " + id + " => " +
		// env.getElementInfo(objref));

		ViewEntry e = new ViewEntry(id, topref, objref);
		map.put(id, e);
	}

	static String getId(MJIEnv env, int objref) {
		String id = null;

		if (env.isInstanceOf(objref, "android.view.View")) {

			// 'name' field has precedence
			id = env.getStringField(objref, "name");

			// if we don't have a name, try to deduce it from type specific
			// fields
			if (id == null) {

				// <2do> we need to support a lot more
				if (env.isInstanceOf(objref, "javax.swing.JFrame")) {
					id = env.getStringField(objref, "title");

				} else if (env.isInstanceOf(objref, "javax.swing.JLabel")) {
					id = env.getStringField(objref, "text");

				} else if (env.isInstanceOf(objref,
						"javax.swing.AbstractButton")) {
					id = env.getStringField(objref, "text");

				} else if (env.isInstanceOf(objref,
						"javax.swing.text.JTextComponent")) {
					int lblRef = env.getReferenceField(objref, "labeledBy");
					if (lblRef != MJIEnv.NULL) {
						id = env.getStringField(lblRef, "text");
						id += ":input";
					} else {
						id = "input";
					}

				} else if (env.isInstanceOf(objref, "javax.swing.JList")) {
					id = "list";

				} else if (env.isInstanceOf(objref, "javax.swing.JTable")) {
					id = "table";

				} else if (env.isInstanceOf(objref, "javax.swing.JComponent")) {

					// <2do> we might check this for JLists and JTables too
					int borderRef = env.getReferenceField(objref, "border");
					if (borderRef != MJIEnv.NULL
							&& env.isInstanceOf(borderRef,
									"javax.swing.border.TitledBorder")) {
						id = env.getStringField(borderRef, "title");
					}
				}
			}

			if (id != null) {
				id = id.replace(' ', '_');
			}
		}

		return id;
	}
}
