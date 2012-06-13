package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPF_android_view_LayoutInflater {

	static Document dom = null;
	static InputStream is;
	static Element docRoot = null;
	static Element currentNode = null;
	static LinkedList<Element> nodes = new LinkedList<Element>();

	public static void init(MJIEnv env, int objref, int fileref) {
		Config conf = env.getConfig();
		String lPath = conf.getString("lpath"); // TODO if specified in
		String filename = JPF_android_view_Window.getLayoutName(fileref);

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			is = new FileInputStream(lPath + filename + ".xml");
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is, null);

			docRoot = dom.getDocumentElement();
			buildQueue(docRoot);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public static void buildQueue(Element element) {
		System.out.println("Adding " + element.getNodeName() + " to queue");
		nodes.add(element);
		NodeList items = element.getChildNodes();
		for (int i = 0; i < items.getLength(); i++) {
			Node child = items.item(i);
			if (child instanceof Element) {

				buildQueue((Element) child);
			}
		}

	}

	public static int getType(MJIEnv env, int objref) {
		return env.newString(currentNode.getNodeName());

	}

	public static boolean nextElement(MJIEnv env, int objref) {
		currentNode = nodes.poll();
		System.out.println("Current element is:" + currentNode.getNodeName());
		if (currentNode == null)
			return false;
		return true;
	}

	// public static int getParent(MJIEnv env, int objref) {
	// Node parent = currentNode.getParentNode();
	// if (parent != null) {
	// String id = "";
	// NamedNodeMap list = parent.getAttributes();
	// Node n = list.getNamedItem("android:id");
	// if (n != null) {
	// id = n.getNodeValue().substring(5);
	// return env.newString(id);
	// }
	// }
	//
	// return MJIEnv.NULL;
	// }

	public static int getName(MJIEnv env, int objref) {
		String id = "";
		NamedNodeMap list = currentNode.getAttributes();
		Node n = list.getNamedItem("android:id");
		if (n != null) {
			id = n.getNodeValue().substring(5);

		}

		return env.newString(id);

	}

	public static int getID(MJIEnv env, int objref) {
		int id = -1;
		String name = "$" + env.getStringObject(getName(env, objref));
		System.out.println("Getting Id of name: " + name);
		ComponentEntry e = JPF_android_view_Window.componentMap.get(name);
		if (e != null)
			id = e.id;
		System.out.println("Id: " + id);
		return id;

	}

	public static int getNumChildren(MJIEnv env, int objref) {
		int numChildren = 0;
		NodeList items = currentNode.getChildNodes();
		for (int i = 0; i < items.getLength(); i++) {
			Node child = items.item(i);
			if (child instanceof Element) {
				numChildren++;
			}
		}
		return numChildren;

	}
}
