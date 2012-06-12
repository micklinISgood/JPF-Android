package android.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

/**
 * Inflates a resource value into a view hierarchy.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class LayoutInflater {
	boolean done = false;
	Context c;
	Document dom = null;
	View rootView;
	InputStream is;

	public LayoutInflater(Context c) {
		this.c = c;

	}

	public View inflate(int resId, View root) {
		System.out.println("inflating " + resId);
		try {
			String contents = getFileContents(resId);
			is = new ByteArrayInputStream(contents.getBytes("UTF-8"));

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is, null);
			Element docRoot = dom.getDocumentElement();

			rootView = parse(docRoot);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		System.out.println(rootView.toString());
		if (root != null) {
			((ViewGroup) root).addView(rootView);
		}

		return rootView;

	}

	public View parse(Node root) {
		System.out.println(root.getLocalName());
		View rootView = null;
		try {
			rootView = createView(root.getNodeName(), root.getAttributes());
			if (rootView instanceof ViewGroup) {
				NodeList items = root.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					Node child = items.item(i);
					if (child instanceof Element)
						((ViewGroup) rootView).addView(parse(child));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rootView;
	}

	public View createView(String name, NamedNodeMap list)
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {

		View ret = null;
		Class<View> cls = (Class<View>) Class.forName("android.widget." + name);

		Class[] intArgsClass = new Class[] { Context.class };
		Object[] intArgs = new Object[] { c };
		Constructor intArgsConstructor = cls.getConstructor(intArgsClass);
		ret = (View) intArgsConstructor.newInstance(intArgs);
		Node n = list.getNamedItem("android:id");
		if (n != null) {
			String nodeName = n.getNodeValue().substring(5);
			ret.setName(nodeName);
		}

		return ret;
	}

	static native String getFileContents(int resId);

}