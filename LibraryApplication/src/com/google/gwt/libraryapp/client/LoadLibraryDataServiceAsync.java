package com.google.gwt.libraryapp.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoadLibraryDataServiceAsync {

	void getAllLibraries(AsyncCallback<List<Library>> callback);

	void getLibrary(String library, AsyncCallback<Library> callback);

	void loadLibraries(String url, AsyncCallback<Void> callback);

	void removeAllLibraries(AsyncCallback<Void> callback);

}
