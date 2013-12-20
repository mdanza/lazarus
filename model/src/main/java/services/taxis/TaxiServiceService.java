package services.taxis;

import java.util.List;

import javax.ejb.Local;

import model.TaxiService;

@Local
public interface TaxiServiceService {

	public void addTaxiService(TaxiService service);

	public void removeTaxiService(TaxiService service);

	public List<TaxiService> getAllTaxiService();

	public TaxiService getTaxiServiceByName(String name);

}
