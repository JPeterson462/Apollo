module ApolloCommon {
	requires io.netty.all;
	requires jetlang;
	requires com.google.gson;
	
	exports net.digiturtle.apollo;
	exports net.digiturtle.apollo.definitions;
	exports net.digiturtle.apollo.match;
	exports net.digiturtle.apollo.match.event;
	exports net.digiturtle.apollo.networking;
	exports net.digiturtle.apollo.packets;
	
	opens net.digiturtle.apollo.match;
	opens net.digiturtle.apollo.match.event;
}