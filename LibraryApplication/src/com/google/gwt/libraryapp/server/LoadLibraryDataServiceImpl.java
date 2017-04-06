package com.google.gwt.libraryapp.server;

import com.google.gwt.libraryapp.client.Library;
import com.google.gwt.libraryapp.client.LoadLibraryDataService;
import com.google.gwt.libraryapp.client.NotLoggedInException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoadLibraryDataServiceImpl extends RemoteServiceServlet implements LoadLibraryDataService{
	private static final Logger LOG = Logger.getLogger(LoadLibraryDataServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
		      JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private boolean loaded = false;

	
	@Override
	public void loadLibraries(String url) throws NotLoggedInException {
		
		if (loaded) return;
		PersistenceManager pm = getPersistenceManager();
		List<Library> libraries = readLibsFromUrl(url);
		pm.makePersistentAll(libraries);
		loaded = true;
	}
	
	public void removeAllLibraries() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
	    try {
	      Query q = pm.newQuery(Library.class);
	      List<Library> libraries = (List<Library>) q.execute();
	      for (Library library : libraries) {
	    	  pm.deletePersistent(library);
	      }
	      loaded = false;
	    } finally {
	      pm.close();
	    }
	}
	
	private List<Library> readLibsFromUrl(String url)  {
		try {
			URL parseUrl = new URL(url);
			URLConnection urlc = parseUrl.openConnection();
			InputStream is = urlc.getInputStream();
			Scanner scanner = new Scanner(is);
			CSVLibraryParser csvLibParser = new CSVLibraryParser();
			List<Library> libs = csvLibParser.parse(scanner);
			scanner.close();
			return libs;
		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
		}
		return null;
	}
	

	@Override
	public List<Library> getAllLibraries() throws NotLoggedInException {
		
		PersistenceManager pm = getPersistenceManager();
	    List<Library> libs = new ArrayList<Library>();
	    try {
	      Query q = pm.newQuery(Library.class);
	      List<Library> libraries = (List<Library>) q.execute();
	      for (Library library : libraries) {
	    	  libs.add(library);
	      }
	    } finally {
	      pm.close();
	    }
	    return libs;
	    
	}
	
	@Override
	public Library getLibrary(String library) throws NotLoggedInException {
	  
		PersistenceManager pm = getPersistenceManager();
	    List<Library> libs = new ArrayList<Library>();
	    try {
	    	Query q = pm.newQuery(Library.class);
	    	q.declareParameters("com.google.gwt.libraryfinder.client.Library.name libName");
	    	List<Library> libraries = (List<Library>) q.execute(library);
	    	for (Library lib : libraries) {
	    		libs.add(lib);
	      }
	    } finally {
	      pm.close();
	    }
	    
	    if (libs.size() == 0) {
	        LOG.log(Level.WARNING, "Couldn't find " + library);
	        return null;
	    }
	    
	    else if (libs.size() != 1) {
	    	LOG.log(Level.WARNING, "Found " + libs.size() + " libraries matching the name:" + library);
	        return libs.get(0);
	    }
	    
	    else return (Library) libs.get(0);
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}
	
	private void checkLoggedIn() throws NotLoggedInException {
	    if (getUser() == null) {
	      throw new NotLoggedInException("Not logged in.");
		    }
		  }

	private User getUser() {
	    UserService userService = UserServiceFactory.getUserService();
	    return userService.getCurrentUser();
	  }
}
