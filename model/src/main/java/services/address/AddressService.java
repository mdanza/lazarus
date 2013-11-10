package services.address;

import java.util.List;

import javax.ejb.Local;

import com.vividsolutions.jts.geom.Coordinate;

@Local
public interface AddressService {
	
	public Coordinate parseAddressToCoordinates(String streetName,int addressNumber,String letter);
	
	public Coordinate parseAddressToCoordinates(String mainStreet,String cornerStreet);
	
	/**
	 * Get all possible street names that contain words like words contained on approximate, the comparison to
	 * street names is done by the LIKE operator, and must be true for all the words contained in approximate
	 * separated by a space. If the number of words of approximate is bigger than 5, the order of appearance 
	 * is taken into account.
	 * @param approximate Approximate name of the street. Case insensitive.
	 * @return List of street names
	 */
	public List<String> getPossibleStreets(String approximate);
    
	public CloseLocationData getCloseLocationData(Coordinate Coordinate);


}
