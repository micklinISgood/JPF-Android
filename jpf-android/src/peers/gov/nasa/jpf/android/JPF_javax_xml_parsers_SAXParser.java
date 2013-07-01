package gov.nasa.jpf.android;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JPF_javax_xml_parsers_SAXParser {
  public final static String TAG = JPF_javax_xml_parsers_SAXParser.class.getSimpleName();
  static Logger log = JPF.getLogger("gov.nasa.jpf.android");

  private final int namespaceURI = 0;
  private final int localname = 1;
  private final int qname = 2;
  private final int attributes = 3;
  private final int content = 4;

  private static DocumentBuilder builder;
  private static Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
  private static Node root = null;

  protected static Map<String, String> URLInput = new HashMap<String, String>();

  public static void $init(MJIEnv env, int robj) {
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    try {
      builder = f.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
    }
  }

  public static int parse__Ljava_lang_String_2__I(MJIEnv env, int objRef, int url) throws SAXException,
      IOException {

    Document doc = builder.parse(AndroidFileUtil.getProjectDir() + "/"
        + URLInput.get(env.getStringObject(url)));
    doc.getDocumentElement().normalize();
    root = doc.getDocumentElement();

    nodeMap.put(root.hashCode(), root);
    //    System.out.println("Root element: " + root.getNodeName());

    return root.hashCode();
  }

  public static int getNodeInfo(MJIEnv env, int objRef, int hashcode) {
    if (hashcode == -1) {
      log.warning(TAG + ": No XML node with hashcode -1");
      return MJIEnv.NULL;
    }

    Node n = nodeMap.get(hashcode);
    if (n == null) {
      log.warning(TAG + ": Could not find XML node with hashcode " + hashcode);
      return -1;
    }

    // we have found the node 
    String[] returnVals = { n.getNamespaceURI(), n.getNodeName(), n.getNodeName(), null, n.getTextContent(),
        "" };
    return env.newStringArray(returnVals);

  }

  public static int getChildren(MJIEnv env, int objref, int hashcode) {

    if (hashcode == -1) {
      log.warning(TAG + ": No XML node with hashcode -1");
      return MJIEnv.NULL;
    }

    Node n = nodeMap.get(hashcode);
    if (n == null) {
      log.warning(TAG + ": Could not find XML node with hashcode " + hashcode);
      return MJIEnv.NULL;
    }

    //get the children
    NodeList list = n.getChildNodes();

    ArrayList<Integer> returnList = null;
    //if has children, return first child
    if (list != null && list.getLength() > 0) {
      returnList = new ArrayList<Integer>();
      for (int i = 0; i < list.getLength(); i++) {
        Node child = list.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          nodeMap.put(child.hashCode(), child);
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

}
