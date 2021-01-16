package org.tokenManagement.service;
import org.tokenManagement.messaging.*;

public class TokenManagerFactory {
	static TokenManager service = null;

	public TokenManager getService() {
		// The singleton pattern.
		// Ensure that there is at most
		// one instance of a PaymentService
		if (service != null) {
			return service;
		}
		
		// Hookup the classes to send and receive
		// messages via RabbitMq, i.e. RabbitMqSender and
		// RabbitMqListener. 
		// This should be done in the factory to avoid 
		// the PaymentService knowing about them. This
		// is called dependency injection.
		// At the end, we can use the PaymentService in tests
		// without sending actual messages to RabbitMq.
		EventSender b = new RabbitMqSender();
		service = new TokenManager(b);
		RabbitMqListener r = new RabbitMqListener(service);
		try {
			r.listen();
		} catch (Exception e) {
			throw new Error(e);
		}
		return service;
	}
}
