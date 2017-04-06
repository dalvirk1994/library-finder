package com.google.gwt.libraryapp.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FavoriteLibraryServiceAsync {

	void addFavLibrary(Library favLibrary, AsyncCallback<Void> callback);

	void getFavLibraries(AsyncCallback<List<Library>> callback);

	void removeFavLibrary(Library favLibrary, AsyncCallback<Void> callback);

	void removeFavLibraries(AsyncCallback<Void> callback);

}
