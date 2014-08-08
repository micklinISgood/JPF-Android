package za.vdm.translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import za.vdm.translator.script.impl.Script;
import za.vdm.translator.script.parser.ScriptParser;
import za.vdm.translator.script.visitor.AllExpandVisitor;
import za.vdm.translator.script.visitor.ExpandVisitor;
import za.vdm.translator.script.visitor.NumberExpandVisitor;
import za.vdm.translator.script.visitor.RandomExpandVisitor;

public class Main {

  private final static String header = "Expands a JPF-Android input script. By default a script for each choice combination is printed to System.out.\n\n";
  private final static String footer = "\nPlease report issues to heila@ml.sun.ac.za";

  /**
   * Main entry point of the ScriptTranslator
   * 
   * @param args
   */
  public static void main(String[] args) {
    ExpandVisitor scriptExpander = null;

    // create the command line parser
    CommandLineParser parser = new GnuParser();
    Options options = setupCommandlineOptions();

    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("h")) {
        // print help
        (new HelpFormatter()).printHelp("expander", header, options, footer, true);
        return;

      }
      if (line.hasOption("r")) {
        // choose random choice
        scriptExpander = new RandomExpandVisitor();

      } else if (line.hasOption("n")) {

        // choice specific choice
        int choiceNum = Integer.parseInt(getOptionValue(line, "n"));
        scriptExpander = new NumberExpandVisitor(choiceNum-1);

      } else {
        // write all options
        scriptExpander = new AllExpandVisitor();
      }

      if (line.hasOption("repeat")) {
        // expand repeat structures
        scriptExpander.setRepeat(true);
      } else
        scriptExpander.setRepeat(false);

      String filename = getOptionValue(line, "f");

      String expandedScript = expand(scriptExpander, filename);

      writeOutput(expandedScript,getOptionValue(line, "o"), filename);

    } catch (ParseException exp) {
      System.err.println("Unexpected exception:" + exp.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected exception:" + e.getMessage());
    }
  }

  private static void writeOutput(String expandedScript, String optionValue, String filename) {

    if (optionValue == null) {
      // no option specified - write to stdout

      System.out.println(expandedScript);

    } else if (optionValue.contains("files")) {
      // write each script in its own file

      String[] scripts = expandedScript.split("#Traversal\\ ");

      Writer writer = null;
      File f;
      String s;
      for (int i = 0; i < scripts.length; i++) {
        s = scripts[i];

        try {
          f = new File(filename.substring(0, filename.length() - 4) + "_" + (i + 1) + ".es");
          writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
          writer.write("#Traversal " + s);
        } catch (IOException ex) {
          throw new RuntimeException("Could not write traversal " + (i + 1) + " to file: " + ex.getMessage());
        } finally {
          if (writer != null) {
            try {
              writer.close();
            } catch (Exception ex) {
            }
          }
        }
      }
    } else if (optionValue.contains("file")) {
      // write all scripts to one file

      Writer outputWriter = null;
      try {

        File f = new File(filename.substring(0, filename.length() - 4) + "_expanded" + ".es");
        outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
        outputWriter.write(expandedScript);

      } catch (IOException ex) {
        throw new RuntimeException("Could not write traversal to file: " + ex.getMessage());
      } finally {
        if (outputWriter != null) {
          try {
            outputWriter.close();
          } catch (Exception ex) {
          }
        }
      }
    } else {
      System.out.println(expandedScript);

    }

  }

  @SuppressWarnings("static-access")
  public static Options setupCommandlineOptions() {

    // create the Options
    Options options = new Options();
    options.addOption("f", "FILE", false, "path of scriptfile");

    options.addOption("r", "random", false, "select random choices while expanding");
    options.addOption("n", "choice-number", false, "specific choice number that should be choosen.");
    options.addOption("o", "output", false, "output exapnded  scripts to individual files.");

    options.addOption(OptionBuilder.withLongOpt("help").create('h'));

    return options;
  }

  public static Reader getFileReader(String filename) throws FileNotFoundException {
    File file = new File(filename);
    Reader fileReader = new FileReader(file);
    return fileReader;
  }

  public static String expand(ExpandVisitor expander, String file) throws FileNotFoundException,
      za.vdm.translator.script.parser.ParseException, Exception {

    Reader r = getFileReader(file);
    ScriptParser parser = new ScriptParser(r);
    Script s = parser.parse();
    expander.visit(s);
    return expander.getScriptsOutput();

  }

  private static String getOptionValue(CommandLine line, String option) {
    Option[] options = line.getOptions();
    String[] args = line.getArgs();
    int i = 0;
    for (Option o : options) {
      if (o.getOpt().equals(option)) {
        return args[i];
      }
      i++;
    }
    return null;
  }
}
