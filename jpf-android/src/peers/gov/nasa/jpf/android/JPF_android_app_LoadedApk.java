package gov.nasa.jpf.android;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

public class JPF_android_app_LoadedApk extends DefaultHandler {
  PackageInfo packageInfo = new PackageInfo();

  String XmlFileName;
  String tmpValue;
  PackageItemInfo itemTmp;
  PackageItemInfo filterTmp;
  ArrayList<ActivityInfo> activityInfos = new ArrayList<ActivityInfo>();
  ArrayList<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
  ArrayList<ProviderInfo> providerInfos = new ArrayList<ProviderInfo>();
  ArrayList<ActivityInfo> receiverInfos = new ArrayList<ActivityInfo>();

  public JPF_android_app_LoadedApk(String bookXmlFileName) {
    this.XmlFileName = bookXmlFileName;
    parseDocument();
    System.out.println("successful");
  }

  private void parseDocument() {
    // parse
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      SAXParser parser = factory.newSAXParser();
      parser.parse(XmlFileName, this);
    } catch (ParserConfigurationException e) {
      System.out.println("ParserConfig error");
    } catch (SAXException e) {
      System.out.println("SAXException : xml not well formed");
    } catch (IOException e) {
      System.out.println("IO error");
    }
  }

  @Override
  public void startElement(String s, String s1, String elementName, Attributes attributes)
      throws SAXException {

    if (elementName.equalsIgnoreCase("manifest")) {
      packageInfo.packageName = attributes.getValue("package");
    }

    if (elementName.equalsIgnoreCase("application")) {
      packageInfo.applicationInfo = new ApplicationInfo();
      packageInfo.applicationInfo.className = attributes.getValue("android:name");
    }

    if (elementName.equalsIgnoreCase("activity")) {
      itemTmp = new ActivityInfo();
      setName(attributes);
      // android:screenOrientation
      // android:launchMode=["multiple" | "singleTop" |
      // "singleTask" | "singleInstance"]
    }
    if (elementName.equalsIgnoreCase("service")) {
      itemTmp = new ServiceInfo();
      setName(attributes);
    }
    if (elementName.equalsIgnoreCase("receiver")) {
      itemTmp = new ActivityInfo();
      setName(attributes);
    }
    if (elementName.equalsIgnoreCase("provider")) {
      itemTmp = new ProviderInfo();
      setName(attributes);
    }
    if (elementName.equalsIgnoreCase("intent-filter")) {

    }
    if (elementName.equalsIgnoreCase("action")) {
    }

    if (elementName.equalsIgnoreCase("category")) {
    }

    if (elementName.equalsIgnoreCase("data")) {
    }

  }

  public void setName(Attributes attributes) {
    itemTmp.name = attributes.getValue("android:name");
    String[] name = itemTmp.name.split("\\.");
    if (name.length > 2) {
      itemTmp.packageName = itemTmp.name.substring(0, itemTmp.name.lastIndexOf("."));
      if (itemTmp.packageName.length() == 0) {
        itemTmp.packageName = attributes.getValue("android:process");
        if (itemTmp.packageName == null) {
          itemTmp.packageName = packageInfo.packageName;
        }
      }
    }
    itemTmp.name = name[name.length - 1]; // get the string after the last ".", this will be the name
  }

  @Override
  public void endElement(String s, String s1, String element) throws SAXException {
    // if end of book element add to list
    if (element.equals("activity")) {
      activityInfos.add((ActivityInfo) itemTmp);
    }
    if (element.equalsIgnoreCase("service")) {
      serviceInfos.add((ServiceInfo) itemTmp);
    }
    if (element.equalsIgnoreCase("provider")) {
      providerInfos.add((ProviderInfo) itemTmp);
    }
    if (element.equalsIgnoreCase("receiver")) {
      receiverInfos.add((ActivityInfo) itemTmp);
    }
    if (element.equalsIgnoreCase("application")) {
    }
    if (element.equalsIgnoreCase("intent-filter")) {
    }
  }

  @Override
  public void characters(char[] ac, int i, int j) throws SAXException {
    tmpValue = new String(ac, i, j);
  }

  public static void main(String[] args) {
    new JPF_android_app_LoadedApk("AndroidManifest.xml");
  }
}
