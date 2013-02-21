//
// Copyright  (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
//  (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
//  (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.util.script;

import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * generic parser for event scripts
 * 
 * <2do> this is still awfully hardwired to StringExpander
 */

public class ESParserE extends ESParser {

  public ESParserE(String fname) throws Exception {
    super(fname);
  }

  public ESParserE(String filename, Reader reader) throws Exception {
    super(filename, reader);
  }

  final public static String K_GROUP = "GROUP";

  @Override
  public StreamTokenizer createScanner (Reader r) {
    StreamTokenizer s = new StreamTokenizer(r);

    // disable number parsing, since it doesn't work in the context of string expansion
    // and we also would have to preserve the number type (int or double)
    s.ordinaryChars('0','9');
    s.wordChars('0','9');
    //s.wordChars('"', '"');

    // those are used to expand events
    s.wordChars('[','[');
    s.wordChars(']',']');
    s.wordChars('|','|');
    s.wordChars('-','-');
    s.wordChars('<','<');
    s.wordChars('>','>');

    // those can be part of Event IDs
    s.wordChars('_','_');
    s.wordChars('#', '#');
    s.wordChars('*','*');
    s.wordChars('@','@');
    s.wordChars('$','$');
    s.wordChars(':',':');
    s.wordChars('~','~');
    s.wordChars('!', '!');

    s.quoteChar('"');

    s.slashSlashComments(true);
    s.slashStarComments(true);

//    s.whitespaceChars(',', ',');
    s.whitespaceChars(';', ';');

    return s;
  }
  protected void alternative(ScriptElementContainer parent) throws Exception {
    // matchKeyword(K_ANY);

    Alternative a = new Alternative(parent, scanner.lineno());
    parent.add(a);

    match('{');
    while (!done && (scanner.ttype != '}')) {
      group(a);
    }
    match('}');
  }

  protected void group(ScriptElementContainer parent) throws Exception {

    Group a = new Group(parent, scanner.lineno());
    parent.add(a);

    while (!done && (scanner.ttype != ',') && (scanner.ttype != '}')) {
      sequence(a);
    }
    isMatch(',');

  }

}
