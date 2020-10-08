package net.digiturtle.apollo.networking;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
@SuppressWarnings("exports")
public class NetworkUtils {
	
	private static Gson gson = new Gson();

	public static DatagramPacket serialize (Object object, String ip, int port) {
		String json = gson.toJson(object);
		String output = object.getClass().getName() + ":" + json;
		return new DatagramPacket(Unpooled.copiedBuffer(output, CharsetUtil.UTF_8), SocketUtils.socketAddress(ip, port));
	}
	
	public static Object deserialize (DatagramPacket packet) {
		String input = packet.content().toString(CharsetUtil.UTF_8);
		String typeName = input.substring(0, input.indexOf(':')), json = input.substring(input.indexOf(':') + 1);
		try {
			return gson.fromJson(json, Class.forName(typeName));
		} catch (JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
