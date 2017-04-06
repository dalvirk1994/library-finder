package com.google.gwt.libraryapp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.libraryapp.client.FavoriteLibraryService;
import com.google.gwt.libraryapp.client.Library;
import com.google.gwt.libraryapp.client.NotLoggedInException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FavoriteLibraryServiceImpl extends RemoteServiceServlet implements
FavoriteLibraryService {

	private static final Logger LOG = Logger.getLogger(FavoriteLibraryServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
      JDOHelper.getPersistenceManagerFactory("transactions-optional");

@Override
public void addFavLibrary(Library favLibrary) throws NotLoggedInException {
	checkLoggedIn();
    PersistenceManager pm = getPersistenceManager();
    try {
      pm.makePersistent(new FavoriteLibrary(favLibrary, getUser()));
    } finally {
      pm.close();
    }
}

@Override
public void removeFavLibrary(Library library)
		throws NotLoggedInException {
	 checkLoggedIn();
     PersistenceManager pm = getPersistenceManager();
	    try {
	      long deleteCount = 0;
	      Query q = pm.newQuery(FavoriteLibrary.class, "user == u");
	      q.declareParameters("com.google.appengine.api.users.User u");
	      List<FavoriteLibrary> favoriteLibraries = (List<FavoriteLibrary>) q.execute(getUser());
	      for (FavoriteLibrary favLibrary : favoriteLibraries) {
	        if (favLibrary.getName().equals(library.getName())) {
	          deleteCount++;
	          pm.deletePersistent(favLibrary);
	        }
	      }
	      if (deleteCount != 1) {
	        LOG.log(Level.WARNING, "removeStock deleted "+deleteCount+" Stocks");
	      }
	    } finally {
	      pm.close();
	    }
	
}

@Override
public List<Library> getFavLibraries() throws NotLoggedInException {
    checkLoggedIn();
    PersistenceManager pm = getPersistenceManager();
    List<Library> favLibraries = new ArrayList<Library>();
    try {
      Query q = pm.newQuery(FavoriteLibrary.class, "user == u");
      q.declareParameters("com.google.appengine.api.users.User u");
      List<FavoriteLibrary> libraries = (List<FavoriteLibrary>) q.execute(getUser());
      for (FavoriteLibrary library : libraries) {
        Library libraryToReturn = new Library (library.getName(), library.getAddress(), 
        		library.getWebsite(), library.getLat(), library.getLong());
        favLibraries.add(libraryToReturn);
      }
    } finally {
      pm.close();
    }
    return favLibraries;
}


@Override
public void removeFavLibraries() throws NotLoggedInException {
	checkLoggedIn();
	  PersistenceManager pm = getPersistenceManager();
	    try {
	      Query q = pm.newQuery(FavoriteLibrary.class);
	      List<FavoriteLibrary> favoriteLibraries = (List<FavoriteLibrary>) q.execute();
	      for (FavoriteLibrary favLibrary : favoriteLibraries) {
	         pm.deletePersistent(favLibrary);
	      }
	    } finally {
	      pm.close();
	    }
	
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

  private PersistenceManager getPersistenceManager() {
    return PMF.getPersistenceManager();
  }	

}
