package com.wl4g.devops.coss.startup;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static java.util.Objects.nonNull;

import javax.net.ssl.SSLException;

import com.wl4g.devops.coss.config.ChannelServerProperties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * Netty channel coss server
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2017年12月19日
 * @since
 */
public class NettyChannelCossServer extends ChannelCossServer {

	public NettyChannelCossServer(ChannelServerProperties config) {
		super(config);
	}

	@Override
	protected void doStartBind() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		// 事件处理器组(masters用来接收客户端连接并分配给slaves，slaves用来处理客户端连接)
		EventLoopGroup masters = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyCossServerMaster", true));
		// Threads set 0, Netty will use availableProcessors () * 2 by default
		EventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("NettyCossServerWorker", true));
		try {
			bootstrap.group(masters, worker);
			// 设置为Nio通道模式
			bootstrap.channel(NioServerSocketChannel.class);
			// 设置通道传输模式，立即传输模式，不需要等待特定大小
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			// 设置重用处于TIME_WAIT但是未完全关闭的socket地址
			// https://www.cnblogs.com/zemliu/p/3692996.html
			bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
			// 设置ByteBuff内存分配器
			// 主要有两种创建方式：UnpooledByteBufAllocator/PooledByteBufAllocator，在netty5.0中后者是默认的，可以重复利用之前分配的内存空间。这个可以有效减少内存的使用
			bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			// 设置worker的socket通道模式，长连接
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			// 设置最大连接数(TCP底层syns队列/accept队列)，是提供给NioServerSocketChannel用来接收进来的连接,也就是boss线程
			// https://www.jianshu.com/p/e6f2036621f4，注意会依赖操作系统的TCP连接队列
			bootstrap.option(ChannelOption.SO_BACKLOG, config.getBacklog());
			// http://www.52im.net/thread-166-1-1.html
			// bootstrap.childOption(ChannelOption.SO_SNDBUF, 32);
			// bootstrap.childOption(ChannelOption.SO_RCVBUF, 32);
			// 设置slaves的处理器队列
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					log.info("Init coss netty channels...");
					configureChannel(ch.config());

					// Channel configure.
					ChannelPipeline p = ch.pipeline();
					// Log handler
					if (config.isEnableChannelLog()) {
						p.addLast(new LoggingHandler(LogLevel.valueOf(config.getChannelLogLevel())));
						log.info("Netty coss channel enable logLevel: {}", config.getChannelLogLevel());
					}

					// Idle handler
					p.addLast(new IdleStateHandler(config.getReadTimeoutSec(), config.getWriteTimeoutSec(),
							config.getAllTimeoutSec()));

					// SSL handler
					if (config.isEnableSslSecure()) {
						SslContext sslContext = getServerSslContext();
						notNull(sslContext, "sslContext should not be null");
						// TODO
						p.addLast(sslContext.newHandler(ch.alloc()));
					}

					// HTTP handler
					p.addLast(new HttpClientCodec());
					p.addLast(new HttpObjectAggregator(config.getMaxContentLength()));
				}
			});

			ChannelFuture f = bootstrap.bind(config.getInetHost(), config.getInetPort()).sync();
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					Throwable t = future.cause();
					if (future.isSuccess()) {
						log.info("Netty coss server started on: {}:{}", config.getInetHost(), config.getInetPort());
					} else {
						log.error(t.getMessage(), t);
					}
				}
			});

			// The thread begins to wait here unless there is a socket event
			// wake-up.
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			Exception ex = null;
			try {
				masters.shutdownGracefully().sync();
			} catch (Exception e) {
				ex = e;
			}
			try {
				worker.shutdownGracefully().sync();
			} catch (Exception e) {
				ex = e;
			}
			if (nonNull(ex)) {
				log.error("Failed to destroy netty server", ex);
			} else {
				log.info("Netty server stop gracefully({}:{}).", config.getInetHost(), config.getInetPort());
			}
		}

	}

	/**
	 * Template method for changing properties on the given
	 * {@link SocketChannelConfig}.
	 * <p>
	 * The default implementation sets the connect timeout based on the set
	 * property.
	 * 
	 * @param sconfig
	 *            the channel configuration
	 */
	protected void configureChannel(SocketChannelConfig sconfig) {

	}

	/**
	 * Gets server sslContext
	 * 
	 * @return
	 */
	private SslContext getServerSslContext() {
		try {
			// TODO
			return SslContextBuilder.forServer(null).build();
		} catch (SSLException ex) {
			throw new IllegalStateException("Could not create default server sslContext", ex);
		}
	}

	static {
		// Sets Sfl4j logger
		InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
	}

}
