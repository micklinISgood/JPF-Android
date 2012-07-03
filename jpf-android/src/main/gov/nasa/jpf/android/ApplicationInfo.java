package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.IntentFilter;

public class ApplicationInfo {
	static Logger log = JPF.getLogger("gov.nasa.jpf.android");

	public class ComponentEntry {
		public String name;
		public List<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
		public HashMap<String, String> attributes = new HashMap<String, String>();
	}

	private String packageName;
	private String path;

	HashMap<String, ComponentEntry> activityMap = new HashMap<String, ComponentEntry>();
	HashMap<String, ComponentEntry> serviceMap = new HashMap<String, ComponentEntry>();
	HashMap<String, ComponentEntry> receiverMap = new HashMap<String, ComponentEntry>();

	public void init(MJIEnv env) {
		Config conf = env.getConfig();
		path = conf.getProperty("path");
		parseManifestFile(path);
	}

	public void parseManifestFile(String mPath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			InputStream is = new FileInputStream(mPath + "/AndroidManifest.xml");
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is, null);

			Element docRoot = dom.getDocumentElement();
			parse(docRoot);

		} catch (Exception e) {
			log.severe("Could not parse Mainifest: " + mPath
					+ "/AndroidManifest.xml does not exist");
		}
	}

	private void parse(Element root) {
		String type = root.getNodeName();
		if (type.equals("manifest")) {
			packageName = root.getAttribute("package");

			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n instanceof Element)
					parse((Element) n);
			}

		} else if (type.equals("application")) {

			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n instanceof Element)
					parse((Element) n);
			}

		} else if (type.equals("activity")) {
			String name = root.getAttribute("android:name");
			if (name.startsWith(".")) {
				name = packageName + name;
			}
			ComponentEntry c = new ComponentEntry();
			c.name = name;
			NodeList filters = root.getChildNodes();
			for (int i = 0; i < filters.getLength(); i++) {
				Node n = filters.item(i);
				if (n.getNodeName().equals("intent-filter")) {
					IntentFilter f = new IntentFilter();
					NodeList att = n.getChildNodes();
					for (int j = 0; j < att.getLength(); j++) {
						Node a = att.item(j);
						if (a.getNodeName().equals("action")) {
							f.addAction(((Element) a)
									.getAttribute("android:name"));
						}
						if (a.getNodeName().equals("category")) {
							f.addCategory(((Element) a)
									.getAttribute("android:name"));
						}
					}
					c.intentFilters.add(f);
				}

			}
			activityMap.put(name, c);

		} else if (type.equals("service")) {

		} else if (type.equals("receiver")) {

		}

	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HashMap<String, ComponentEntry> getActivityMap() {
		return activityMap;
	}

	public void setActivityMap(HashMap<String, ComponentEntry> activityMap) {
		this.activityMap = activityMap;
	}

	public HashMap<String, ComponentEntry> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(HashMap<String, ComponentEntry> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public HashMap<String, ComponentEntry> getReceiverMap() {
		return receiverMap;
	}

	public void setReceiverMap(HashMap<String, ComponentEntry> receiverMap) {
		this.receiverMap = receiverMap;
	}

	public ComponentEntry getActivity(String name) {
		int index = name.indexOf("\\.");
		if (index == -1) {
			return activityMap.get(getPackageName() + "." + name);
		} else
			return activityMap.get(name);
	}

}
