package com.whck.proto.handler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.DefaultMessage;
import com.whck.proto.model.RainerData;
import com.whck.proto.model.ReceiveError;
import com.whck.proto.model.ReceiveMulti;
import com.whck.rainer.ShowView;
import com.whck.rainer.ZoneListView;
import com.whck.rainer.model.ArgsCache;

public class MessageHandler implements IoHandler{
	public static Map<String, IoSession> sessions = new HashMap<>();
	private ShowView showView ;
	private ZoneListView zoneListView;
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		String sessionAddress= ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		sessions.remove(sessionAddress);
		notify(sessionAddress,false);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		sessions.put(clientIP, session);
		if(message instanceof ReceiveError){      //下位机返回的错误码
			ReceiveError msg=(ReceiveError)message;
			System.out.println("MessageReceived?MsgType=ReceiveError&Ip=" + session.getRemoteAddress()+")");
			if(msg.checkCRC()){
				System.out.println("MessageReceived-->ERROR_CODE:"+msg.getInfoCode());
			}
		}else{
			if(message instanceof DefaultMessage){
				DefaultMessage msg=(DefaultMessage)message; 
				if(msg.checkCRC()){
					RainerData rData=msg.convert();
					rData.setIp(clientIP);
					notify(rData,clientIP);			//广播：刷新显示区以及控制区设备状态
					notify(clientIP,true);       	//广播：刷新树状列表中区域的在线状态
				}else{
					System.out.println("MessageReceived-->CRC_ERROR");
				}
			}
			if(message instanceof ReceiveMulti){
				ArgsCache.latest=true;
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("MessageSent:" + message.toString());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println(
				"Session Closed：" + "{sessioId:" + session.getId() + "}" + "{IP:" + session.getRemoteAddress() + "}");
		String sessionAddress =  ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		sessions.remove(sessionAddress);
		notify(sessionAddress,false);
		CloseFuture closeFuture = session.close(true);
		closeFuture.addListener(new IoFutureListener<IoFuture>() {
			public void operationComplete(IoFuture future) {
				if (future instanceof CloseFuture) {
					((CloseFuture) future).setClosed();
					System.out.println(
							"sessionClosed CloseFuture setClosed-->{}," + "sessionId: " + future.getSession().getId());
				}
			}
		});
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("Session Created：{}" + ",IP:" + session.getRemoteAddress());
		String clientIP =((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		session.getConfig().setUseReadOperation(true);
		sessions.put(clientIP, session);
		notify(clientIP,true);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Idle Session：{}" + " SessionId:" + session.getRemoteAddress() + ",status:" + status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		String clientIP =((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		notify(clientIP,true);
		System.out.println(
				"Session Opened：{}#{}" + " sessionId:" + session.getId() + ",idleCount:" + session.getBothIdleCount());
	}

	public void setShowView(ShowView showView) {
		this.showView = showView;
	}

	public ShowView getShowView() {
		return showView;
	}
	public void setZoneListView(ZoneListView zoneListView) {
		this.zoneListView = zoneListView;
	}
	public ZoneListView getZoneListView() {
		return zoneListView;
	}
	public void notify(Object message,  String ip) {
		if (showView != null) {
			showView.update(ip, message);
		}
	}
	
	public void notify(String ip,boolean state){
		if(zoneListView!=null){
			zoneListView.update(ip,state);
		}
	}
	@Override
	public void inputClosed(IoSession session) throws Exception {}
}
