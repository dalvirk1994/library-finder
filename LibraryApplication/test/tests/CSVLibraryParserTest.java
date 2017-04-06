package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.libraryapp.client.Library;
import com.google.gwt.libraryapp.server.CSVLibraryParser;

public class CSVLibraryParserTest {
	
	private CSVLibraryParser parser = new CSVLibraryParser();
	List<Library> libraries;
	
	@Before
	public void readLibsFromUrl()  {
		try {
			URL parseUrl = new URL("http://m.uploadedit.com/b042/1415171870700.txt");
			URLConnection urlc = parseUrl.openConnection();
			InputStream is = urlc.getInputStream();
			Scanner scanner = new Scanner(is);
			libraries = parser.parse(scanner);
			scanner.close();
		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
			fail();
		}
	}
	
	@Test
	public void testLibrariesSize() {
		assertEquals(22, libraries.size());
	}
	
	@Test
	public void testLibrariesContents() {
		Library renfrew = new Library("Renfrew", "2969 E 22nd Av", "http://www.vpl.ca/"
				+ "branches/details/renfrew_branch", 49.2524249068635, -123.042958058734);
		
		Library cHeights = new Library("Champlain Heights", "7110 Kerr St", "http://www.vpl.ca"
				+ "/branches/details/champlain_heights_branch", 49.219088065007, -123.040183232883);
		
		Library oakridge = new Library("Oakridge", "191-650 W 41st Av", "http://www.vpl.ca/branches/"
				+ "details/oakridge_branch", 49.2326967118593, -123.117186318925);
		
		assertTrue(libraries.contains(oakridge));
		assertTrue(libraries.contains(cHeights));
		assertTrue(libraries.contains(renfrew));
	}
	
	@Test
	public void testLibrariesPositions() {
		assertEquals("Britannia", libraries.get(5).getName());
		assertEquals(49.2756486033504, libraries.get(5).getLat(), 0.00000000000005);
		assertEquals(-123.073771672447, libraries.get(5).getLong(), 0.00000000000005);
		assertEquals("1661 Napier St", libraries.get(5).getAddress());
		assertEquals("http://www.vpl.ca/branches/details/britannia_branch", libraries.get(5).getWebsite());
		
		assertEquals("Firehall", libraries.get(11).getName());
		assertEquals(49.2628710568171, libraries.get(11).getLat(), 0.00000000000005);
		assertEquals(-123.137578980719, libraries.get(11).getLong(), 0.00000000000005);
		assertEquals("1455 W 10th Av", libraries.get(11).getAddress());
		assertEquals("http://www.vpl.ca/branches/details/firehall_branch", libraries.get(11).getWebsite());
		
		assertEquals("Kitsilano", libraries.get(17).getName());
		assertEquals(49.2647182072995, libraries.get(17).getLat(), 0.00000000000005);
		assertEquals(-123.168698922535, libraries.get(17).getLong(), 0.00000000000005);
		assertEquals("2425 Macdonald St", libraries.get(17).getAddress());
		assertEquals("http://www.vpl.ca/branches/details/kitsilano_branch", libraries.get(17).getWebsite());
		
	}
	

}
