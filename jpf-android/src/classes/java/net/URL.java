package java.net;

import java.io.IOException;
import java.io.InputStream;

public class URL {
  public URL(String spec) throws MalformedURLException {
  }

  public InputStream openStream() throws IOException {
   //if no network connection 
//    throw exception
    throw new IOException("Could not open Url");
  }

}
