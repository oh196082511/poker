package client;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import test.test;

import java.net.URI;

public class WebSocketClient {

    public void start(String url) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        URI uri = new URI(url);

        try {
            WebSocketClientHandler handler = new WebSocketClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(
                            uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(handler);
                        }
                    });

            Channel ch = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            handler.handshakeFuture().sync();
            //String json = new Gson().toJson(test.getStartEvent());
            //ActionEvent actionEvent = test.getActionEvent();
            String json = new Gson().toJson(test.getActionEvent());
            //String json = new Gson().toJson(test.getEndEvent());
            //EndEvent endEvent = test.getEndEvent();
            ch.writeAndFlush(new TextWebSocketFrame(json));
            ch.closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new WebSocketClient().start("ws://localhost:8080/ws");
    }
}