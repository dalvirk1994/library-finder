package com.google.gwt.libraryapp.client;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.libraryapp.client.Library;
import com.google.gwt.libraryapp.client.LoadLibraryDataService;
import com.google.gwt.libraryapp.client.LoadLibraryDataServiceAsync;
import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.libraryapp.client.LoginInfo;
import com.google.gwt.libraryapp.client.LoginService;
import com.google.gwt.libraryapp.client.LoginServiceAsync;
import com.google.gwt.libraryapp.client.NotLoggedInException;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerImage;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.Size;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LibraryApplication implements EntryPoint {
   
   // Sizes for map icons 
   private static double ICON_MAX_WIDTH = 20;
   private static  int INFOWINDOW_MAX_WIDTH = 400;
   private static  double ICON_MAX_HEIGHT = 20;
   
   // link to library data
   final private String URL =  "http://m.uploadedit.com/b042/1415171870700.txt";

   // Lists to store all libraries, and users favorite libraries
   private List<Library> libraries = new ArrayList<Library>();
   private List<Library> favLibraries = new ArrayList<Library>();	
   
   // Services to load library info, user information, login information
   private final LoadLibraryDataServiceAsync loadLibDataService = GWT.create(LoadLibraryDataService.class);
   private final FavoriteLibraryServiceAsync favLibService = GWT.create(FavoriteLibraryService.class);
   private final LoginServiceAsync loginService = GWT.create(LoginService.class);
   
   // map related
   private InfoWindowOptions infowindopt;
   private InfoWindow infowind;
   private GoogleMap map; 

   private Button loadButton = new Button("Load");
   private Button deleteButton = new Button("Delete");
  
   //login related 
   private LoginInfo loginInfo = null;
   private VerticalPanel loginPanel = new VerticalPanel();
   private Label loginLabel = new Label (
		"Please sign in to your Google Account to access the Library Finder application");
   private Anchor signInLink = new Anchor("Sign in");
   private Anchor signOutLink = new Anchor("Sign Out");

   // Google sharing link
   private Anchor shareLink = new Anchor("Post this to Google+ !");

   //library table of all loaded libraries
   private HorizontalPanel libraryPanel = new HorizontalPanel();
   private CellTable<Library> libraryTable = new CellTable<Library>();
 
   //favorite list table of users favorite libraries
   private VerticalPanel favouritePanel = new VerticalPanel();
   private FlexTable favouriteTable = new FlexTable();

   //map panel
   VerticalPanel mapPanel = new VerticalPanel();
  
   // main panel
   VerticalPanel mainPanel = new VerticalPanel();
   
   // Panel for map and favorite library panel
   HorizontalPanel horizontalSubPanel = new HorizontalPanel();
   
   // Header 
   Label header = new Label("Library Finder");
   HorizontalPanel headerPanel = new HorizontalPanel();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Check login status using login service.
	    loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
	      public void onFailure(Throwable error) {
	    	  handleError(error);
	      }

	      public void onSuccess(LoginInfo result) {
	    	  loginInfo = result;
	    	  if(loginInfo.isLoggedIn()) {
	    			if (loginInfo != null) {
	    				if (loginInfo.getEmailAddress() == "sikander_94@hotmail.com"
	    						|| loginInfo.getEmailAddress() == "AngadKalra94@gmail.com"
	    						|| loginInfo.getEmailAddress() == "Dalvir.Khaira@gmail.com"
	    						|| loginInfo.getEmailAddress() == "asif.mammadovv@gmail.com"
	    						|| loginInfo.getEmailAddress() == "amanroop.rosode@gmail.com") {
	    					// If admin, load the load and delete buttons
	    					loadAndDeleteButtons();
	    				}
	    			}

	    		  loadLibraryFinder();
	    	  } else {
	    		  loadLogin();
	    	  }
	      	}
	    });
		
	}
	
	private void loadLogin() {
		// Assemble login panel.
		createHeader();
	    signInLink.setHref(loginInfo.getLoginUrl());
	    loginPanel.add(loginLabel);
	    loginPanel.add(signInLink);
	    RootPanel.get().add(headerPanel);
	    RootPanel.get().add(loginPanel);
		
	}
	
	private void loadLibraryFinder() {
		// set up tables etc
		createHeader();
		createSignOutLink();
		createShareLink();
		createCellTable();
		createFavListTable();
		setUpMap();
		layout();
		// load libraries into local copy to be displayed on UI
		loadAllLibraries();
		// load users fav libraries to local copy to be displayed on UI
		getFavLibraries();
	}
	
	private void createHeader() {
		header.getElement().getStyle().setColor("purple");
		header.getElement().getStyle().setFontSize(8, Unit.EM);
		header.getElement().getStyle().setFontStyle(FontStyle.OBLIQUE);
		header.getElement().getStyle().setFontWeight(FontWeight.BOLDER);
		headerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		headerPanel.add(header);
	}
	
	private void createSignOutLink() {
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("signOutLink").add(signOutLink);
	}

	/**
	 * 
	 */
	private void createShareLink() {
		shareLink.setTarget("_blank");
		shareLink.setHref("https://plus.google.com/share?url=http://tinyurl.com/qd5ydvd");
		RootPanel.get("shareLink").add(shareLink);
	}
	
	private void loadAllLibraries() {
		loadLibDataService.loadLibraries(URL, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				// if libraries succesfully loaded into data store, then store them in a local 
				// copy so they can be displayed
				getAllLibraries();
			}
			
		});
	}

	private void getAllLibraries() {
		
		loadLibDataService.getAllLibraries(new AsyncCallback<List<Library>>() {

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);	
			}

			@Override
			public void onSuccess(List<Library> result) {
				// store all libraries, then update the cell table to reflect new loaded libraries
				libraries = result;
				updateCellTable();
			}

		});
		
	}
	
	private void getFavLibraries() {
		favLibService.getFavLibraries(new AsyncCallback<List<Library>> () {

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);
			}

			@Override
			public void onSuccess(List<Library> result) {
				// locally store users favorite libraries and then update map and fav table
				// to reflect these changes
				favLibraries = result;
				updateFavTable();
				populateMap();
			}
			
		});
	}

	private void updateFavTable() { 
		favouriteTable.clear();
		Label widget = new Label(); 
		widget.setText("Favourite Library List"); 
		favouriteTable.setWidget(0, 65, widget);
		favouriteTable.getWidget(0, 65).getElement().getStyle().setFontSize(20, Unit.PX);
		displayFavLibsOnTable();
	}

	private void displayFavLibsOnTable() {
		for (Library lib : favLibraries) {
			addLibraryToFavTable(lib);
		}
		
	}

	private void setUpMap() {
		AjaxLoaderOptions options = AjaxLoaderOptions.newInstance();
		options.setOtherParms("sensor=false");
		AjaxLoader.init();
		Runnable callback = new Runnable() {
			public void run() {
				// redraw map with the current favorite libraries appearing on map
				populateMap();
			}
		};
		AjaxLoader.loadApi("maps", "3", callback, options);
	}
	
	private void populateMap() {
		LatLng Vancouver = LatLng.create(49.2496600, -123.1193400); 
		MapOptions opt = MapOptions.create();
		opt.setMapTypeId(MapTypeId.HYBRID);
		opt.setCenter(Vancouver);
		opt.setZoom(11);
		mapPanel.setWidth("500px");
		mapPanel.setHeight("800px");
		
		map = GoogleMap.create(mapPanel.getElement(), opt);
		
		for(Library favLibrary : favLibraries) {
			addLibraryToMap(favLibrary, map);
		}
	}

	private void addLibraryToMap(final Library favLibrary, GoogleMap map2) {
		// use favLibrary to set position for this map icon to place on map
		LatLng position = LatLng.create(favLibrary.getLat(), favLibrary.getLong());
	    
		// Changes map icon 
	    MarkerImage icon = MarkerImage.create("http://icons.iconarchive.com/icons/iconfactory/copland-1/32/Red-Book-icon.png");
	    Size scaledSize = Size.create( ICON_MAX_WIDTH, ICON_MAX_HEIGHT);
		icon.setScaledSize(scaledSize);
	    MarkerOptions options = MarkerOptions.create();
	    options.setIcon(icon);
	    options.setTitle(favLibrary.getName());
	    options.setPosition(position); 
	    options.setMap(map);
	  
	    final Marker z = Marker.create(options);
	    
	    // contents of the infobox
	    
	    infowindopt = InfoWindowOptions.create(); 
	    infowindopt.setMaxWidth(INFOWINDOW_MAX_WIDTH);
	    infowind = InfoWindow.create(infowindopt);  
	    z.setMap(map); 
	    
	    z.addClickListener(new com.google.maps.gwt.client.Marker.ClickHandler() {
	    	
	    	@Override
	    	public void handle(MouseEvent event) {
	    String content =  
	     "<b>Library Name</b>:<br>" + favLibrary.getName() + "<br>" + 
	     "<b>Library Address</b>:<br>" + favLibrary.getAddress() +"<br>" + 
	     "<b>Library URL</b>:<br>" + favLibrary.getWebsite();

          infowind.setContent(content);
          infowindopt.setMaxWidth(INFOWINDOW_MAX_WIDTH);
          infowind.open(map, z);} 
	    	  });
	}
	
	private void createCellTable() { 
		
		// sets columns appropriately according to each libraries fields
	    setColumns();  
		
		// Add a selection model to handle user selecting a library as a favorite library.
	    final SingleSelectionModel<Library> selectionModel = new SingleSelectionModel<Library>();
	    libraryTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
		     
			public void onSelectionChange(SelectionChangeEvent event) {
				final Library selected = selectionModel.getSelectedObject();
		        // dont add this library if fav lib list already contains it!  
		        if (favLibraries.contains(selected)){
		        	  Window.alert("This library was already added to your favorite library list!");
		          }
				  else { 
		        	String content = "Added " + selected.getName() + " to your Favourite Library List" 
		        		+ ". To remove a library double click the remove button";
		        	Window.alert(content);
		        	// add favorite library under current users username to datastore
		        	favLibService.addFavLibrary(selected, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							handleError(caught);
						}
						
						// after successfully adding to datastore, update UI
						@Override
						public void onSuccess(Void result) {
							favLibraries.add(selected);
							populateMap();
							addLibraryToFavTable(selected);
						}
		        	});
		          }     
		   selectionModel.clear(); 
		          } 
		    
		    });
	    
		libraryTable.getElement().getStyle().setFloat(Float.LEFT);
		libraryTable.setWidth("1200px");
		
		libraryPanel.add(libraryTable);
    }

	/**
	 * 
	 */
	private void setColumns() {
		// sets columns appropriately according to each libraries fields
		TextColumn<Library> nameColumn = new TextColumn<Library>() {
			@Override
			public String getValue(Library object) {
				return object.getName();
			}}; 
		
		libraryTable.addColumn(nameColumn, "NAME");
			
		TextColumn<Library> addressColumn = new TextColumn<Library>() {

			@Override
			public String getValue(Library object) {
				return object.getAddress();
			} }; 
			libraryTable.addColumn(addressColumn, "ADDRESS"); 
			
		TextColumn<Library> websiteColumn = new TextColumn<Library>() {

			@Override
			public String getValue(Library object) {
				return object.getWebsite();
			}};  
			
		libraryTable.addColumn(websiteColumn, "WEBSITE");
	}
	

	/**
	 * 
	 */
	private void updateCellTable() {
		// Set the total row count. This isn't strictly necessary, but it affects
	    // paging calculations, so its good habit to keep the row count up to date.
	    libraryTable.setPageSize(libraries.size());
	    libraryTable.setRowCount(libraries.size(), true);
    	// Push the data into the widget.
    	libraryTable.setRowData(0, libraries);
	} 
	private void addLibraryToFavTable(final Library selected) {
		
		// Add the library to the table.
	    int row = favouriteTable.getRowCount();
	    Anchor anchor = new Anchor(selected.getName(), false, selected.getWebsite());
	    anchor.setTarget("_blank");
	    VerticalPanel vp = new VerticalPanel();
	    vp.add(anchor);
	    favouriteTable.setWidget(row, 30, vp);
	    

	    // Add a button to remove this library from the table.
	    Button removeLibraryButton = new Button("x");
	    
	    removeLibraryButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final int removedIndex = favLibraries.indexOf(selected);
				favLibService.removeFavLibrary(selected, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						handleError(caught);
						
					}

					@Override
					public void onSuccess(Void result) {
						//This gets rid of the library from fav-lib list
						favLibraries.remove(removedIndex);   	    
			    	    //This removes the row from the table
			    	    favouriteTable.removeRow(removedIndex + 2);
			    	    // update map
			    	    populateMap();
					}
					
				});	
			}
		});
		    	
	    //This creates the button at the end of the row
	    favouriteTable.setWidget(row, 100, removeLibraryButton);

	    }
	private void createFavListTable() {
		
		// Create table titles for ALL libraries
		Label title = new Label(); 
		title.setText("Favourite Library List");
		
		favouriteTable.setWidget(0, 65, title);
		favouriteTable.getWidget(0, 65).getElement().getStyle().setFontSize(20, Unit.PX);
		favouriteTable.setText(1, 30, "NAME");
		favouriteTable.setText(1, 100, "REMOVE");
		
	    // Associate to fav List panel.
	    favouritePanel.add(favouriteTable);

	}

	/**
	 * 
	 */
	private void layout() {
		// add fav table and map to horizontal sub panel
		horizontalSubPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalSubPanel.add(mapPanel);
		horizontalSubPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalSubPanel.add(favouritePanel);
		
		// add sub panels to main panel
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		mainPanel.add(headerPanel);
		mainPanel.add(horizontalSubPanel);
		mainPanel.add(libraryPanel);
		
		// associate main panel to root panel
		RootPanel.get().add(mainPanel);
		
		
	    }

	private void loadAndDeleteButtons() {
		// if you are an admin, then you will buttons that can load and
		// delete all library and user (favorite library) data
		loadButton.addClickHandler (new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// reload, store locally and display libraries and favorite libraries
				loadAllLibraries();
				getFavLibraries();
			}
			
		});
	
		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// remove from datastore, locally, and UI
				removeAllLibraries();
				removeFavLibraries();
				
			}
		});
		
		RootPanel.get().add(loadButton);
		RootPanel.get().add(deleteButton);
	}
	
	private void removeAllLibraries() {
		loadLibDataService.removeAllLibraries(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);
				
			}

			@Override
			public void onSuccess(Void result) {
				// once we remove all from data store, get the empty list back
				// to update local copy and UI
				getAllLibraries();
				
			}

		});
		
	}
	
	private void removeFavLibraries() {
		favLibService.removeFavLibraries(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);
				
			}

			@Override
			public void onSuccess(Void result) {
				// once we remove all favorite libraries from data store
				// get empty list back to update local copy and UI
				getFavLibraries();
			}
			
		});
		
	}
	
	private void handleError(Throwable error) {
	    Window.alert(error.getMessage());
	    if (error instanceof NotLoggedInException) {
	      Window.Location.replace(loginInfo.getLogoutUrl());
	    }
	  }

	
}
