package com.whck.rainer.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.whck.proto.codec.DefaultMessageDecoder;
import com.whck.proto.codec.ReceiveErrorMessageDecoder;
import com.whck.proto.codec.ReceiveMessageDecoder;
import com.whck.proto.codec.ReceiveMultiMessageDecoder;
import com.whck.proto.codec.ReceiveSingleMessageDecoder;
import com.whck.proto.codec.SendMessageEncoder;
import com.whck.proto.codec.SendMultiMessageEncoder;
import com.whck.proto.codec.SendSingleMessageEncoder;
import com.whck.proto.handler.MessageHandler;
import com.whck.proto.model.Constants;
import com.whck.proto.model.DefaultMessage;
import com.whck.proto.model.Send;
import com.whck.proto.model.SendMulti;
import com.whck.proto.model.SendSingle;

public class Server {

	public static Server instance=new Server();
	private SocketAcceptor acceptor;

    private Server() {
        // 创建非阻塞的server端的Socket连接
        acceptor = new NioSocketAcceptor();
    }
    public boolean start() {
    	System.out.println("Server Started........");
        acceptor.setHandler(new MessageHandler());
        // 设置session配置，30秒内无操作进入空闲状态
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        acceptor.getFilterChain().addLast("logger", new LoggingFilter()); 
        DemuxingProtocolCodecFactory protoCodecFactory=new DemuxingProtocolCodecFactory();
        
        protoCodecFactory.addMessageEncoder(Send.class,SendMessageEncoder.class);
        protoCodecFactory.addMessageEncoder(SendMulti.class,SendMultiMessageEncoder.class);
        protoCodecFactory.addMessageEncoder(SendSingle.class, SendSingleMessageEncoder.class);
        
        protoCodecFactory.addMessageDecoder(new ReceiveMessageDecoder(Constants.READ_REGISTER));
        protoCodecFactory.addMessageDecoder(new ReceiveSingleMessageDecoder(Constants.SINGLE_REGISTER));
        protoCodecFactory.addMessageDecoder(new ReceiveMultiMessageDecoder(Constants.MULTI_REGISTER));
        protoCodecFactory.addMessageDecoder(new ReceiveErrorMessageDecoder(Constants.MULTI_RESPONSE_ERROR));
        protoCodecFactory.addMessageDecoder(new ReceiveErrorMessageDecoder(Constants.SINGLE_RESPONSE_ERROR));
        protoCodecFactory.addMessageDecoder(new DefaultMessageDecoder(Constants.DEFAULT_CODE));
        
        acceptor.getFilterChain().addLast("codec",  
                new ProtocolCodecFilter(protoCodecFactory));
        try {
            acceptor.bind(new InetSocketAddress(5000));
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
