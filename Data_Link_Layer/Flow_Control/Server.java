import java.net.Socket;
import java.net.ServerSocket;

class Server {
	private Socket socket = null;
	private ServerSocket server = null;
	public Server(int port) {
		try {
			server = new ServerSocket(port);
			System.out.println("Server Started on Port " + port);
			System.out.println("Waiting for Client to connect........");
			socket = server.accept();
			System.out.println("Client Connected!!");
			System.out.println("IP Address of Client.......: " + socket.getRemoteSocketAddress());
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server serverObj = new Server(5000);
	}
}