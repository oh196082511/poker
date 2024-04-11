package server;

import com.google.gson.Gson;
import event.Event;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;
import room.RoomManager;

@Slf4j
public class WebSocketServer {

    private RoomManager roomManager = new RoomManager();

    private Gson gson = new Gson();

    public void start(int port) throws Exception {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            server.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new SimpleChannelInboundHandler<WebSocketFrame>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
                                    if (frame instanceof CloseWebSocketFrame) {
                                        log.info("received close frame");
                                        roomManager.removeRoomByChannel(ch);
                                        ctx.close();
                                        return;
                                    }
                                    if (frame instanceof TextWebSocketFrame) {
                                        TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                                        log.info("received: {}", textFrame.text());
                                        Event event = gson.fromJson(textFrame.text(), Event.class);
                                        roomManager.onNettyReceiveEvent(event, ctx.channel());
                                    }

                                }
                            });
                        }
                    });

            ChannelFuture future = server.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        new WebSocketServer().start(8080);
    }
}
