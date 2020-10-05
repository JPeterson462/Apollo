package net.digiturtle.apollo.networking;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import com.esotericsoftware.kryo.Kryo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import net.digiturtle.apollo.UUIDSerializer;
import net.digiturtle.apollo.packets.Packets;

public class UdpClient {
	
	private Consumer<Object> packetConsumer;
	private String ip;
	private int port;
	private Kryo kryo;
	private Channel channel;
	private ArrayList<DatagramPacket> preConnectBuffer;
	
	public UdpClient (String ip, int port) {
		this.ip = ip;
		this.port = port;
		kryo = new Kryo();
		for (Class<?> clazz : Packets.ALL) {
			kryo.register(clazz);
		}
		kryo.register(UUID.class, new UUIDSerializer());
		preConnectBuffer = new ArrayList<>();
	}
	
	public UdpClient listen (Consumer<Object> packetConsumer) {
		this.packetConsumer = packetConsumer;
		return this;
	}
	
	public void send (Object object) {
		DatagramPacket packet = NetworkUtils.serialize(object, kryo, ip, port);
		if (channel != null) {
			channel.writeAndFlush(packet);
		} else {
			preConnectBuffer.add(packet);
		}
	}
	
	public void connect () throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new SimpleChannelInboundHandler<DatagramPacket>() {
				@Override
				protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
					Object object = NetworkUtils.deserialize(packet, kryo);
					packetConsumer.accept(object);
				}
			});

			channel = b.bind(0).sync().channel();
			for (DatagramPacket packet : preConnectBuffer) {
				channel.writeAndFlush(packet);
			}
			preConnectBuffer.clear();
			channel.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}

	}

}
