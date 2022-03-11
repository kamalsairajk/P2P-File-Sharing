import java.net.ServerSocket;

import java.io.IOException;
import java.net.Socket;


public class TriggerInteraction implements Runnable {

	Thread remoteSendingPeerProcess;
	private ServerSocket serverSock;

	Socket parentRemSoc;
	private String pid;

	

	public Thread getRemoteSendingPeerProcess() {
		return remoteSendingPeerProcess;
	}

	public void setRemoteSendingPeerProcess(Thread remoteSendingPeerProcess) {
		this.remoteSendingPeerProcess = remoteSendingPeerProcess;
	}

	public ServerSocket getServerSock() {
		return serverSock;
	}

	public void setServerSock(ServerSocket serverSock) {
		this.serverSock = serverSock;
	}

	public Socket getParentRemSoc() {
		return parentRemSoc;
	}

	public void setParentRemSoc(Socket parentRemSoc) {
		this.parentRemSoc = parentRemSoc;
	}

	public String getPid() {
		return pid;
	}

	public void releaseSocket() {
		try {
			if (!parentRemSoc.isClosed())
				parentRemSoc.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}

	public TriggerInteraction(ServerSocket socket, String pid) {
		setServerSock(socket);
		setPid(pid);
	}

	public void run() {
		while (P2PUtility.TRUE) {
			try {
				parentRemSoc = serverSock.accept();
				remoteSendingPeerProcess = new Thread(new RemotePeerManager(parentRemSoc, P2PUtility.ZERO, pid));
				PeerProcess.processVectorForSending.add(remoteSendingPeerProcess);
				remoteSendingPeerProcess.start();
			} catch (Exception excpt) {
				PeerProcess.writeToLogFile(getPid() + P2PUtility.TRIGGER_EXCPT + excpt.toString());
			}
		}
	}

	
}
