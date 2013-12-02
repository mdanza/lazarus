package helpers;

import javax.ejb.Local;

@Local
public interface RestResultsHelper {
	public String resultWrapper(boolean success, String data);
}
