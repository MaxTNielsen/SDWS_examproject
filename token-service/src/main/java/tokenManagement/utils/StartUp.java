package tokenManagement.utils;


import tokenManagement.messaging.RabbitMqListener;

public class StartUp {
    public static void main(String[] args) throws Exception {
    	new StartUp().startUp();
    }

	private void startUp() throws Exception {
		//EventSender s = new RabbitMqSender();
		//TokenManager service = TokenManager.getInstance();
		new RabbitMqListener().listenWithRPCPattern();
	}
}
