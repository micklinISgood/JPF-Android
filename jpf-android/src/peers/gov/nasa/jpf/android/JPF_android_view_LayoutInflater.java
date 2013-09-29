package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Native peer of the LayoutInflater. Makes use of {@link DocumentBuilder} to
 * parse the layout files.
 * 
 * @author "Heila van der Merwe"
 * 
 */
public class JPF_android_view_LayoutInflater {
  public final static String TAG = JPF_android_view_LayoutInflater.class.getSimpleName();
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  private static class LayoutInfo {
    Map<Integer, Node> viewMap;
    Node root;
    String filename;

    public LayoutInfo() {
      viewMap = new HashMap<Integer, Node>();
    }
  }



  /** Stores a Map of View items for each layout file id */
  private static Map<Integer, LayoutInfo> layoutMap = new HashMap<Integer, LayoutInfo>();

  public static int loadLayout(MJIEnv env, int objref, int resourceID) {
    // retrieve the file name of the layout resource
    String filename = JPF_android_view_WindowManager.getLayoutFileName(resourceID);


    // check if the layout file has been parsed before
    LayoutInfo layoutInfo = layoutMap.get(resourceID);

    if (layoutInfo == null) {
      //if file was not jet loaded, try to load it now.
      try {
        layoutInfo = load(resourceID, filename);
        log.info("Loaded layout file " + filename);
      } catch (Exception e) {
        log.severe("LayoutInflator could not parse file: " + filename);
        throw new RuntimeException("LayoutInflator could not parse file: " + filename);
      }
    }
    assert layoutInfo != null;

    // now the layout file is loaded and we have its LayoutInfo
    return env.newString(filename);
  }

  public static int getRootHash(MJIEnv env, int objref, int resourceID) {
    // retrieve the file name of the layout resource
    return layoutMap.get(resourceID).root.hashCode();
  }

  private static LayoutInfo load(int resourceID, String filename) throws ParserConfigurationException, SAXException,
      IOException {
    //parse the document
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    InputStream is = new FileInputStream(filename);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document dom = builder.parse(is, null);
    Element docRoot = dom.getDocumentElement();

    //create new LayoutInfo entry
    LayoutInfo info = new LayoutInfo();
    info.root = docRoot;
    info.filename = filename;
    insertViewInMap(info, info.root);
    layoutMap.put(resourceID, info);
    return info;
  }

  /**
   * Recursively inserts all child nodes into a hash map of all the views in the
   * layout.
   * 
   * @param info
   *          the info on the Layout
   * @param n
   *          the node to insert
   */
  private static void insertViewInMap(LayoutInfo info, Node n) {
    if (n == null) {
      return;
    }
    //put node in hashmap
    info.viewMap.put(n.hashCode(), n);

    //get the children
    NodeList list = n.getChildNodes();

    //if has children, call insert for each child
    if (list != null && list.getLength() > 0) {
      for (int i = 0; i < list.getLength(); i++) {
        Node child = list.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          insertViewInMap(info, child);
        }
      }
    }
  }

  public static int getNodeInfo(MJIEnv env, int objref, int hashcode, int resourceID) {
    if (hashcode == -1) {
      log.warning(TAG + ": No view node with hashcode -1");
      return MJIEnv.NULL;
    }

    LayoutInfo info = layoutMap.get(resourceID);
    Node n = info.viewMap.get(hashcode);
    if (n == null) {
      log.warning(TAG + ": Could not find XML node with hashcode " + hashcode);
      return -1;
    }

    // we have found the node 
    String[] returnVals = { getType(n), Integer.toString(getID(n)), getName(n), Integer.toString(hashcode),
        getText(n) };
    return env.newStringArray(returnVals);

  }

  public static int getChildren(MJIEnv env, int objref, int hashcode, int resourceID) {

    if (hashcode == -1) {
      log.warning(TAG + ": No view node with hashcode -1");
      return MJIEnv.NULL;
    }

    LayoutInfo info = layoutMap.get(resourceID);
    Node n = info.viewMap.get(hashcode);

    if (n == null) {
      log.warning(TAG + ": Could not find XML node with hashcode " + hashcode);
      return MJIEnv.NULL;
    }

    //get the children
    NodeList list = n.getChildNodes();

    ArrayList<Integer> returnList = null;
    //if has children, return  children
    if (list != null && list.getLength() > 0) {
      returnList = new ArrayList<Integer>();
      for (int i = 0; i < list.getLength(); i++) {
        Node child = list.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          returnList.add(child.hashCode());
        }
      }

      int[] returns = new int[returnList.size()];
      for (int i = 0; i < returnList.size(); i++) {
        returns[i] = returnList.get(i);
      }
      return env.newIntArray(returns);
    }
    return MJIEnv.NULL;

  }

  public static String getType(Node node) {
    return node.getNodeName();

  }

  public static String getName(Node node) {
    String name = "";
    NamedNodeMap list = node.getAttributes();
    Node n = list.getNamedItem("android:id");
    if (n != null) {
      name = n.getNodeValue().substring(5);

    } else {
      name = node.getNodeName().toLowerCase() + "_" + Integer.toString(node.hashCode());
    }

    return name;
  }

  public static String getText(Node node) {
    String name = "";
    NamedNodeMap list = node.getAttributes();
    Node n = list.getNamedItem("android:text");
    if (n != null) {
      name = n.getNodeValue();
      if(name.startsWith("@string/")){
        name = JPF_android_content_res_Resources.getString(name.substring(8));
      }
    }

    return name;
  }

  public static int getID(Node node) {
    int id = -1;
    String name = "$" + getName(node);
    log.fine("Getting Id of name: " + name);
    id = JPF_android_view_WindowManager.getID(name);
    if (id == -1) {
      return node.hashCode();
    } else
      return id;

  }
}
