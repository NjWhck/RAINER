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
import com.whck.msf.ShowView;
import com.whck.msf.ZoneListView;
import com.whck.proto.domain.FFCData;
import com.whck.proto.domain.FlowData;
import com.whck.proto.domain.MSFData;
import com.whck.proto.message.ErrorMessage;
import com.whck.proto.message.FFCMessage;
import com.whck.proto.message.FlowMessage;
import com.whck.proto.message.MSFMessage;

public class MessageHandler implements IoHandler {
	public static Map<String, IoSession> sessions = new HashMap<>();
	private ShowView showView;
	private ZoneListView zoneListView;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		String sessionAddress = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		sessions.remove(sessionAddress);
		notify(sessionAddress, false);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		if (!sessions.containsKey(clientIP)) {
			initIp(clientIP, message, true);
			sessions.put(clientIP, session);
		}
		if (message instanceof ErrorMessage) {
			System.out.println("enter into error message");
			ErrorMessage msg = (ErrorMessage) message;
			if (msg.checkCRC()) {
				System.out.println("MessageReceived-->ERROR_CODE:" + msg.getInfo());
			}
		} else if (message instanceof FlowMessage) {
			System.out.println("enter into flow message");
			FlowMessage msg = (FlowMessage) message;
			if (msg.checkCRC()) {
				FlowData entity = (FlowData) msg.convert();
				entity.setIp(clientIP);
				notify(entity, clientIP);
				notify(clientIP, true);
			} else {
				System.out.println("MessageReceived-->CRC_ERROR");
			}
		}  else if (message instanceof MSFMessage) {
			System.out.println("enter into MSFMessage");
			MSFMessage msg = (MSFMessage) message;
			if (msg.checkCRC()) {
				MSFData entity = (MSFData) msg.convert();
				entity.setIp(clientIP);
				notify(entity, clientIP);
				notify(clientIP, true);
			} else {
				System.out.println("MessageReceived-->CRC_ERROR");
			}
		}else if (message instanceof FFCMessage) {
			System.out.println("enter into FFCMessage");
			FFCMessage msg = (FFCMessage) message;
			if (msg.checkCRC()) {
				FFCData entity = (FFCData) msg.convert();
				entity.setIp(clientIP);
				notify(entity, clientIP);
				notify(clientIP, true);
			} else {
				System.out.println("MessageReceived-->CRC_ERROR");
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
				"Session Closed" + "{sessioId:" + session.getId() + "}" + "{IP:" + session.getRemoteAddress() + "}");
		String sessionAddress = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		sessions.remove(sessionAddress);
		notify(sessionAddress, false);
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
		System.out.println("Session Created{}" + ",IP:" + session.getRemoteAddress());
		// String clientIP = ((InetSocketAddress)
		// session.getRemoteAddress()).getAddress().getHostAddress();
		// session.getConfig().setUseReadOperation(true);
		//// sessions.put(clientIP, session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Idle Session{}" + " SessionId:" + session.getRemoteAddress() + ",status:" + status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// String clientIP = ((InetSocketAddress)
		// session.getRemoteAddress()).getAddress().getHostAddress();
		// notify(clientIP, true);
		System.out.println(
				"Session Opened{}#{}" + " sessionId:" + session.getId() + ",idleCount:" + session.getBothIdleCount());
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

	protected void notify(Object message, String ip) {
		if (showView != null) {
			showView.update(ip, message);
		}
	}

	protected void initIp(String ip, Object msg, boolean state) {
		if (zoneListView != null) {
			zoneListView.initDevIp(ip, msg);
		}
	}

	private void notify(String ip, boolean state) {
		if (zoneListView != null) {
			zoneListView.update(ip, state);
		}
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
	}
}
