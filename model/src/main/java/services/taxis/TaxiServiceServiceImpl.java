package services.taxis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.RouteType;
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

	public void uploadInfo(String url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(url));
			String line = reader.readLine();
			taxiServiceDAO.removeAll();
			for (line = reader.readLine(); line != null; line = reader
					.readLine()) {
				if (line.trim().length() > 0) {
					String elements[] = line.split("\\,");
					if (elements.length == 2) {
						String company = elements[0];
						String phone = elements[1];
						company = company.replaceAll("\"", "");
						phone = phone.replaceAll("\"", "");
						TaxiService taxiService = new TaxiService();
						taxiService.setName(company);
						taxiService.setPhone(phone);
						taxiServiceDAO.add(taxiService);
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		} catch (IOException e) {
			throw new IllegalArgumentException();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

}
