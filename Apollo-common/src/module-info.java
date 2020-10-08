module ApolloCommon {
	requires io.netty.all;
	requires jetlang;
	requires com.google.gson;
	
	exports net.digiturtle.apollo;
	exports net.digiturtle.apollo.networking;
	exports net.digiturtle.apollo.packets;
}