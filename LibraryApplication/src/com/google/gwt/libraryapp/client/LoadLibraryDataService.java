package com.google.gwt.libraryapp.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("loadLibraries")
public interface LoadLibraryDataService  extends RemoteService {

	void loadLibraries(String url) throws Exception;

	List<Library> getAllLibraries() throws Exception;

	Library getLibrary(String library) throws Exception;
	
	void removeAllLibraries() throws Exception;

}
