package com.google.gwt.libraryapp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gwt.libraryapp.client.Library;

public class CSVLibraryParser {
	
	public CSVLibraryParser() {
		
	}
	
	public List<Library> parse(Scanner scanner) {
		scanner.nextLine();
		List<Library> libraries = new ArrayList<Library>();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] fields = line.split(",");
			String libraryName = fields[0];
			double latitude = Double.parseDouble(fields[1]);
			double longitude = Double.parseDouble(fields[2]);
			String address = fields[3];
			String urlLink = fields[4];
			
			Library library = new Library(libraryName, address, urlLink,
					latitude, longitude);
			libraries.add(library);
		}
		return libraries;
	}

}
