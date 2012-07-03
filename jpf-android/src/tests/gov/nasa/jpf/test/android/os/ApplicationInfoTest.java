package gov.nasa.jpf.test.android.os;

import gov.nasa.jpf.android.ApplicationInfo;
import gov.nasa.jpf.android.ApplicationInfo.ComponentEntry;
import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Before;
import org.junit.Test;

import android.content.IntentFilter;

public class ApplicationInfoTest extends TestJPF {

	static ApplicationInfo info = new ApplicationInfo();

	public static void main(String[] args) {
		runTestsOfThisClass(args);
	}

	@Before
	public void setUp() {
		info.parseManifestFile("src/tests");

	}

	@Test
	public void testGetPackageName() {
		String name = info.getPackageName();
		assertEquals("com.example.com", name);
	}

	@Test
	public void testGetActivityName() {
		ComponentEntry c = info.getActivity("SampleProjectActivity");
		assertEquals("com.example.com.SampleProjectActivity", c.name);
	}
	
	@Test
	public void testGetActivityFilters() {
		ComponentEntry c = info.getActivity("SampleProjectActivity");
		IntentFilter i =  c.intentFilters.get(0);
		
		assertEquals("android.intent.action.MAIN",i.getAction(0));
		assertEquals("android.intent.category.LAUNCHER",i.getCategory(0));
	}
	

}
