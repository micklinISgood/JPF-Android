package gov.nasa.jpf.android;

import gov.nasa.jpf.jvm.MJIEnv;

import java.io.File;
import java.util.Scanner;

public class JPF_android_view_LayoutInflater {

	public static String getFileContents(MJIEnv env, int objref, int fileref) {
		String filename = env.getStringObject(fileref);
		Scanner scanner = new Scanner(new File("filename"))
				.useDelimiter("\u001a");
		String contents = scanner.next();
		System.out.println(contents);
		scanner.close();

		return null;
	}

}
