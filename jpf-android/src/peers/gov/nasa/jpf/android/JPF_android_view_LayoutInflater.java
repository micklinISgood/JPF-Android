package gov.nasa.jpf.android;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.view.LayoutInflater;

/**
 * Native peer of the {@link LayoutInflater}. Makes use of {@link DocumentBuilder} to parse the layout files.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_view_LayoutInflater {
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  /**
   * Used to create an unique name and id field for components that are not named in the R.java file. Window
   * has count 0. TODO what happens with multi-windows come into play?
   */
  private static int count = 1;

  /** Stores the current {@link Element} being inflated */
  private static Element currentNode = null;
  private static LinkedList<Element> nodes = new LinkedList<Element>();

  public static int setup(MJIEnv env, int objref, int fileref) {
    Config conf = env.getConfig();
    String lPath = conf.getString("path") + "/res/layout/"; // TODO if specified in

    String filename = JPF_android_view_WindowManager.getLayoutName(fileref);
    log.fine("Inflating file " + filename);
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      InputStream is = new FileInputStream(lPath + filename + ".xml");
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document dom = builder.parse(is, null);

      Element docRoot = dom.getDocumentElement();
      buildQueue(docRoot);

    } catch (Exception e) {
      log.severe("Could not setup LayoutInflator for file: " + lPath + filename + ".xml");
    }

    return env.newString(lPath + filename + ".xml");

  }

  public static void buildQueue(Element element) {
    log.fine("Adding " + element.getNodeName() + " to queue");
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
    if (currentNode == null) {
      return false;
    }
    log.fine("Current element is:" + currentNode.getNodeName());
    return true;
  }

  public static int getName(MJIEnv env, int objref) {
    String name = "";
    NamedNodeMap list = currentNode.getAttributes();
    Node n = list.getNamedItem("android:id");
    if (n != null) {
      name = n.getNodeValue().substring(5);

    } else {
      name = currentNode.getNodeName().toLowerCase() + "_" + Integer.toString(count);
      count = count + 1;
    }

    return env.newString(name);
  }

  public static int getText(MJIEnv env, int objref) {
    String name = "";
    NamedNodeMap list = currentNode.getAttributes();
    Node n = list.getNamedItem("android:text");
    if (n != null) {
      name = n.getNodeValue();
    }

    return env.newString(name);
  }

  public static int getID(MJIEnv env, int objref, int nameref) {
    int id = -1;
    String name = "$" + env.getStringObject(nameref);
    log.fine("Getting Id of name: " + name);
    id = JPF_android_view_WindowManager.getID(name);
    if (id == -1) {
      return count;
    } else
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
