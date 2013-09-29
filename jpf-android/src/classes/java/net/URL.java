package java.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class URL {

  public URL(String spec) throws MalformedURLException {
    // to be intercepted
  }

  public InputStream openStream() throws IOException {
    //TODO if no network connection 
    //throw new IOException("Could not open Url");
    byte[] arr = new byte[4];
    return new ByteArrayInputStream(arr);

  }

  private native byte[] getURLInput();

}
