package net.digiturtle.apollo.networking;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.function.Consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpClient {

	private Consumer<Object> packetConsumer;
	private String ip;
	private int port;
	private Channel channel;
	private ArrayList<ByteBuf> preConnectBuffer;
	
	public TcpClient (String ip, int port) {
		this.ip = ip;
		this.port = port;
		preConnectBuffer = new ArrayList<>();
	}
	
	public TcpClient listen (Consumer<Object> packetConsumer) {
		this.packetConsumer = packetConsumer;
		return this;
	}
	
	public void send (Object object) {
		if (object instanceof Short) {
			return;
		}
		ByteBuf packet = NetworkUtils.serialize(object);
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
			.channel(NioSocketChannel.class)
			.remoteAddress(new InetSocketAddress(ip, port))
			.handler(new ChannelInitializer<SocketChannel>() {
		        protected void initChannel(SocketChannel socketChannel) throws Exception {
		            socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
						@Override
						protected void channelRead0(ChannelHandlerContext ctx, ByteBuf packet) throws Exception {
							Object object = NetworkUtils.deserialize(packet);
							packetConsumer.accept(object);
						}
					});
		        }
		    })
			.option(ChannelOption.SO_KEEPALIVE, true);

			channel = b.connect().sync().channel();
			for (ByteBuf packet : preConnectBuffer) {
				channel.writeAndFlush(packet);
			}
			preConnectBuffer.clear();
			channel.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}

	}

}
