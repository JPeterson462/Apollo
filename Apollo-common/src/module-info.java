module ApolloCommon {
	requires com.esotericsoftware.kryo;
	requires io.netty.all;
	requires jetlang;
	
	exports net.digiturtle.apollo;
	exports net.digiturtle.apollo.networking;
	exports net.digiturtle.apollo.packets;
}