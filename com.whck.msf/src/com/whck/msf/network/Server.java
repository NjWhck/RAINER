package com.whck.msf.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import com.whck.proto.codec.FlowMessageDecoder;
import com.whck.proto.codec.FlowMessageEncoder;
import com.whck.proto.codec.MSFMessageDecoder;
import com.whck.proto.codec.MSFMessageEncoder;
import com.whck.proto.handler.MessageHandler;
import com.whck.proto.message.FlowMessage;
import com.whck.proto.message.MSFMessage;

public class Server {
	public static final int PORT=5000;
	public static Server instance=new Server();
	private SocketAcceptor acceptor;

    private Server() {
        acceptor = new NioSocketAcceptor();
    }
    public boolean start() {
    	System.out.println("Server Started........");
        acceptor.setHandler(new MessageHandler());
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        acceptor.getFilterChain().addLast("logger", new LoggingFilter()); 
        DemuxingProtocolCodecFactory protoCodecFactory=new DemuxingProtocolCodecFactory();
        
        protoCodecFactory.addMessageEncoder(MSFMessage.class,MSFMessageEncoder.class);
        protoCodecFactory.addMessageDecoder(new MSFMessageDecoder());
        protoCodecFactory.addMessageEncoder(FlowMessage.class,FlowMessageEncoder.class);
        protoCodecFactory.addMessageDecoder(new FlowMessageDecoder());
        acceptor.getFilterChain().addLast("codec",  
                new ProtocolCodecFilter(protoCodecFactory));
        try {
            acceptor.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Server getInstance(){
    	return instance;
    }
   
    public SocketAcceptor getAcceptor(){
    	return acceptor;
    }
}
