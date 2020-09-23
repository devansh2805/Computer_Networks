import java.net.Socket;

class Client {
	private Socket socket = null;
	public Client(String ipAddress, int port) {
		try {
			socket = new Socket(ipAddress, port);
			System.out.println("Connected To Server");
			System.out.println("IP Address of Server.......: " + socket.getRemoteSocketAddress());
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 5000);
	}
}