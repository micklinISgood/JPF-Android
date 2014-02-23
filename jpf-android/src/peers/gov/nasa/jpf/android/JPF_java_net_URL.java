package gov.nasa.jpf.android;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JPF_java_net_URL extends NativePeer {

  private static Map<String, String> urls = new HashMap<String, String>();

  String urlString;

  @MJI
  public void $init__Ljava_lang_String_2(MJIEnv env, int objref, int urlRef) {
    try {
      urlString = env.getStringObject(urlRef);
      URL url = new URL(urlString);
    } catch (MalformedURLException e) {
      env.throwException("java.net.MalformedURLException", e.getMessage());
    }
  }

  @MJI
  public int getURLInput(MJIEnv env, int objRef) {
    byte[] data = new byte[1];
    String filename = urls.get(urlString);
    if (filename == null) {
      env.throwException("java.io.IOException", "No file exists for URL " + urlString);
      return env.newByteArray(data);
    }
    try {
      String projectDir = AndroidPathManager.getProjectDir();
      Path path = Paths.get(((projectDir != null) ? projectDir + "/" : "") + filename);
      data = Files.readAllBytes(path);
      return env.newByteArray(data);
    } catch (IOException e) {
      env.throwException("java.io.IOException", "Could not read file " + filename);

    }
    return env.newByteArray(data);
  }

  public static void mapURLToFile(String url, String filename) {
    urls.put(url, filename);
  }

}