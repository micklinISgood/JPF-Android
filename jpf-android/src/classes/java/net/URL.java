package java.net;

import gov.nasa.jpf.vm.Verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public final class URL implements Serializable {
  
  public URL(String spec) throws MalformedURLException {
    // to be intercepted
  }

  public InputStream openStream() throws IOException {
    boolean b = Verify.getBoolean();

    if (b) {
      throw new IOException("Could not open Url");
    } else {
      return new ByteArrayInputStream(getURLInput());
    }

  }

  protected native byte[] getURLInput();

}
