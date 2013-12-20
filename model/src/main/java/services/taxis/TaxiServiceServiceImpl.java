package services.taxis;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.TaxiService;
import model.dao.TaxiServiceDAO;

@Stateless(name = "TaxiServiceService")
public class TaxiServiceServiceImpl implements TaxiServiceService {

	@EJB(name = "TaxiServiceDAO")
	private TaxiServiceDAO taxiServiceDAO;

	@Override
	public void addTaxiService(TaxiService service) {
		taxiServiceDAO.add(service);
	}

	@Override
	public void removeTaxiService(TaxiService service) {
		taxiServiceDAO.delete(service);
	}

	@Override
	public List<TaxiService> getAllTaxiService() {
		return taxiServiceDAO.findAll();
	}

	@Override
	public TaxiService getTaxiServiceByName(String name) {
		return taxiServiceDAO.find(name);
	}

}
