package services;

import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless(name = "UserService")
@WebService(portName = "UserServicePort", serviceName = "UserServiceWebService", targetNamespace = "http://lazarus.org/wsdl")
public class UserService {

	public int sum(int a, int b) {
		return a + b;
	}
}
