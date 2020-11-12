package net.digiturtle.apollo.networking;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
@SuppressWarnings("exports")
public class NetworkUtils {
	
	private static Gson gson = new Gson();

	public static AckPacket ack (DatagramPacket packet) {
		String input = packet.content().toString(CharsetUtil.UTF_8);
		int messageHash = input.hashCode();
		AckPacket ack = new AckPacket();
		ack.messageHash = messageHash;
		return ack;
	}
	
	public static DatagramPacket serialize (Object object, String ip, int port) {
		ByteBuf buf = serialize(object);
		return new DatagramPacket(buf, SocketUtils.socketAddress(ip, port));
	}
	
	public static ByteBuf serialize (Object object) {
		String json = gson.toJson(object);
		String output = object.getClass().getName() + "|" + json + "^";
		return Unpooled.copiedBuffer(output, CharsetUtil.UTF_8);
	}
	
	public static Object deserialize (DatagramPacket packet) {
		return deserialize(packet.content());
	}
	
	public static ArrayList<Object> deserializeOneOrMore (ByteBuf buf) {
		String input = buf.toString(CharsetUtil.UTF_8);
		ArrayList<Object> objects = new ArrayList<>();
		while (input.length() > 0) {
			System.out.println(input);
			String pkt = input.substring(0, input.indexOf('^'));
			String typeName = input.substring(0, input.indexOf('|')), json = input.substring(input.indexOf('|') + 1, input.indexOf('^'));
			try {
				objects.add(gson.fromJson(json, Class.forName(typeName)));
			} catch (JsonSyntaxException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			input = input.substring(pkt.length() + 1);
		}
		return objects;
	}
	
	public static Object deserialize (ByteBuf buf) {
		String input = buf.toString(CharsetUtil.UTF_8);
		System.out.println(input);
		String typeName = input.substring(0, input.indexOf('|')), json = input.substring(input.indexOf('|') + 1, input.indexOf('^'));
		try {
			return gson.fromJson(json, Class.forName(typeName));
		} catch (JsonSyntaxException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
