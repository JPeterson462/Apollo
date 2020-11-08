package net.digiturtle.apollo.networking;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServer {

	private int port;
	private Channel channel;
	private BiConsumer<Object, InetSocketAddress> packetConsumer;
	private HashMap<InetSocketAddress, SocketChannel> clients;

	public TcpServer (int port) {
		this.port = port;
		clients = new HashMap<>();
	}

	public TcpServer listen (BiConsumer<Object, InetSocketAddress> packetConsumer) {
		this.packetConsumer = packetConsumer;
		return this;
	}

	public void send (Object object, InetSocketAddress address) {
		ByteBuf buf = NetworkUtils.serialize(object);
		clients.get(address).writeAndFlush(buf);
	}

	public void broadcast (Object object) {
		for (InetSocketAddress client : clients.keySet()) {
			send(object, client);
		}
	}
	
	public void forward (Object object, InetSocketAddress ip) {
		for (InetSocketAddress client : clients.keySet()) {
			if (client != ip) {
				send(object, client);
			}
		}
	}

	public void connect () throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(group)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
		        protected void initChannel(SocketChannel socketChannel) throws Exception {
					clients.put(socketChannel.remoteAddress(), socketChannel);
		            socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
		            	@Override
						protected void channelRead0(ChannelHandlerContext ctx, ByteBuf packet) throws Exception {
							//Object object = NetworkUtils.deserialize(packet);
		            		ArrayList<Object> objects = NetworkUtils.deserializeOneOrMore(packet);
		            		for (Object object : objects)
		            				packetConsumer.accept(object, socketChannel.remoteAddress());
						}
		            });
		        }
		    })
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			channel = b.bind(port).sync().channel();
			channel.closeFuture().await();
		} finally {
			group.shutdownGracefully();
		}
	}
}
