package com.google.gwt.libraryapp.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("favLibs")
public interface FavoriteLibraryService extends RemoteService {
	 public void addFavLibrary(Library favLibrary) throws NotLoggedInException;
	 public void removeFavLibrary(Library favLibrary) throws NotLoggedInException;
	 public List<Library> getFavLibraries() throws NotLoggedInException;
	 public void removeFavLibraries() throws NotLoggedInException;
}
