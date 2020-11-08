package net.digiturtle.apollo.networking;

import java.util.ArrayList;
import java.util.function.Consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpClient {
	
	private Consumer<Object> packetConsumer;
	private String ip;
	private int port;
	private Channel channel;
	private ArrayList<DatagramPacket> preConnectBuffer;
	
	public UdpClient (String ip, int port) {
		this.ip = ip;
		this.port = port;
		preConnectBuffer = new ArrayList<>();
	}
	
	public UdpClient listen (Consumer<Object> packetConsumer) {
		this.packetConsumer = packetConsumer;
		return this;
	}
	
	public void send (Object object) {
		if (object instanceof Short) {
			return;
		}
		DatagramPacket packet = NetworkUtils.serialize(object, ip, port);
		if (channel != null) {
			sendOrRetry(packet);
		} else {
			preConnectBuffer.add(packet);
		}
	}
	
	private void sendOrRetry (DatagramPacket packet) {
		channel.writeAndFlush(packet);
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
					if (object instanceof AckPacket) {
						// Would be good to do packet retries for non-ACKed packets
					}
					else {
						send(NetworkUtils.ack(packet));
						packetConsumer.accept(object);
					}
				}
			});

			channel = b.bind(0).sync().channel();
			for (DatagramPacket packet : preConnectBuffer) {
				sendOrRetry(packet);
			}
			preConnectBuffer.clear();
			channel.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}

	}

}
