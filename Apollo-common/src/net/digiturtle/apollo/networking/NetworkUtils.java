package net.digiturtle.apollo.networking;

import java.util.Arrays;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.SocketUtils;
@SuppressWarnings("exports")
public class NetworkUtils {

	public static DatagramPacket serialize (Object object, Kryo kryo, String ip, int port) {
		Output output = new Output(1024, -1);
		kryo.writeClassAndObject(output, object);
		byte[] data = Arrays.copyOf(output.getBuffer(), output.position());
		return new DatagramPacket(Unpooled.copiedBuffer(data), SocketUtils.socketAddress(ip, port));
	}
	
	public static Object deserialize (DatagramPacket packet, Kryo kryo) {
		ByteBuf buf = packet.content();
		byte[] data = new byte[buf.readableBytes()];
		buf.getBytes(buf.readerIndex(), data);
		Input input = new Input(data);
		return kryo.readClassAndObject(input);
	}
	
}
