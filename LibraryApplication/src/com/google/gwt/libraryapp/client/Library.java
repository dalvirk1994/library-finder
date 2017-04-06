package com.google.gwt.libraryapp.client;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Library implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String name;
	@Persistent
	private String address;
	@Persistent
	private String website;
	@Persistent
	private double latitude;
	@Persistent
	private double longitude;
	
	  public Library() {
	  }

	  public Library(String name, String address, String website, double latitude, 
			  double longitude) {
	    this.name = name;
	    this.address = address;
	    this.website = website;
	    this.latitude = latitude;
	    this.longitude = longitude;
	  }
	  

	  public String getName() {
	    return this.name;
	  }

	  public String getAddress() {
	    return this.address;
	  }

	  public String getWebsite() {
	    return this.website;
	  }

	  public double getLat() {
	    return this.latitude;
	  }
	  
	  public double getLong() {
		    return this.longitude;
		  }

	  public void setName(String name) {
	    this.name = name;
	  }

	  public void setAddress(String address) {
	    this.address = address;
	  }
	  
	  public void setURL(String website) {
		  this.website = website;
	  }

	  public void setLatLong(double lat, double longitude) {
	    this.latitude = lat;
	    this.longitude = longitude;
	  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Library other = (Library) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		return true;
	}
	  
	  

}
