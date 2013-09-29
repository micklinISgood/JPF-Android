package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JPF_java_net_URL {

  private static Map<String, URL> urls = new HashMap<String, URL>();

  public static void init__V(MJIEnv env, int objref, int urlRef) {
    try {
      String urlString = env.getStringObject(urlRef);
      URL url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      //TODO
    }

  }

  private static byte[] getDataFromURL(String surl) {
    byte[] data = null;

    //    try {
    //      AndroidPathManager.getProjectDir() + "/"  + URLInput.get(env.getStringObject(url));
    //          
    //      InputStream is = url.openStream();
    //
    //      if (is != null) {
    //        ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
    //        byte[] buf = new byte[1024];
    //
    //        for (int n = is.read(buf); n >= 0; n = is.read(buf)) {
    //          os.write(buf, 0, n);
    //        }
    //        is.close();
    //
    //        data = os.toByteArray();
    //        dataCache.put(surl, data);
    //
    //        logger.info("reading contents of ", surl, " from server");
    //
    //        if (cacheDir != null) {
    //          String cacheFileName = getCacheFileName(surl);
    //          File cacheFile = new File(cacheDir, cacheFileName);
    //          try {
    //            FileUtils.setContents(cacheFile, data);
    //            logger.info("storing contents of ", surl, " to file ", cacheFile.getPath());
    //          } catch (IOException iox) {
    //            logger.warning("can't store to cache directory ", cacheFile.getPath());
    //          }
    //        }
    //
    //        return data;
    //      }
    //    } catch (MalformedURLException mux) {
    //      logger.warning("mallformed URL ", surl);
    //    } catch (IOException ex) {
    //      logger.warning("reading URL data ", surl, " failed with ", ex.getMessage());
    //    }
    //
    //    return data;
    return null;
  }
}
