package net.digiturtle.apollo.networking;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.function.BiConsumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpServer {

	private int port;
	private Channel channel;
	private BiConsumer<Object, InetSocketAddress> packetConsumer;
	private HashSet<InetSocketAddress> clients;

	public UdpServer (int port) {
		this.port = port;
		clients = new HashSet<>();
	}

	public UdpServer listen (BiConsumer<Object, InetSocketAddress> packetConsumer) {
		this.packetConsumer = packetConsumer;
		return this;
	}

	public void send (Object object, InetSocketAddress address) {
		DatagramPacket packet = NetworkUtils.serialize(object, address.getHostName(), address.getPort());
		channel.writeAndFlush(packet);
	}

	public void broadcast (Object object) {
		for (InetSocketAddress client : clients) {
			send(object, client);
		}
	}
	
	public void forward (Object object, InetSocketAddress ip) {
		for (InetSocketAddress client : clients) {
			if (client != ip) {
				send(object, client);
			}
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
					Object object = NetworkUtils.deserialize(packet);
					clients.add(packet.sender()); //NOTE: If I'm going to update for EVERY packet, must use a Set NOT a List
					packetConsumer.accept(object, packet.sender());
				}
			});
			channel = b.bind(port).sync().channel();
			channel.closeFuture().await();
		} finally {
			group.shutdownGracefully();
		}
	}

}
