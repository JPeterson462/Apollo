package net.digiturtle.server.manager;

import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.networking.TcpClient;
import net.digiturtle.apollo.networking.TcpServer;

public class NetworkTests {
	
	public static void main(String[] args) throws InterruptedException {
		FiberPool pool1 = new FiberPool(2);
		TcpServer server = new TcpServer(9812);
		server.listen((packet, ip) -> {
			System.out.println("[Server] " + packet);
			if (((String) packet).equalsIgnoreCase("Hello world")) {
				server.send("What's up?", ip);
			}
		});
		TcpClient client = new TcpClient("localhost", 9812);
		client.listen((packet) -> {
			System.out.println("[Client] " + packet);
			client.send("Not much, bro!");
		});
		pool1.scheduleTask(() -> {
			try {
				server.connect();
				System.out.println("server done");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		pool1.scheduleTask(() -> {
			try {
				client.connect();
				System.out.println("client done");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		Thread.sleep(1000);
		client.send("Hello world");
	}

}
