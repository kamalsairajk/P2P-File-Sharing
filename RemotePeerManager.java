import java.net.*;
import java.io.*;

public class RemotePeerManager implements Runnable {

	public RemotePeerManager(Socket soc, int contype, String mypid) {

		this.peersockinfo = soc;
		this.myconTypedetails = contype;
		this.mypeeridlocal = mypid;
		try {
			iostrm = soc.getInputStream();
			ostream = soc.getOutputStream();
		} catch (Exception ex) {
			PeerProcess.writeToLogFile(this.mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG9 + ex.getMessage());
		}
	}

	private Socket peersockinfo = null;
	private PeerHandshake peerHandshake;

	private InputStream iostrm;

	public Socket getPeersockinfo() {
		return peersockinfo;
	}

	public void setPeersockinfo(Socket peersockinfo) {
		this.peersockinfo = peersockinfo;
	}

	public PeerHandshake getPeerHandshake() {
		return peerHandshake;
	}

	public void setPeerHandshake(PeerHandshake peerHandshake) {
		this.peerHandshake = peerHandshake;
	}

	public InputStream getIostrm() {
		return iostrm;
	}

	public void setIostrm(InputStream iostrm) {
		this.iostrm = iostrm;
	}

	public OutputStream getOstream() {
		return ostream;
	}

	public void setOstream(OutputStream ostream) {
		this.ostream = ostream;
	}

	public int getMyconTypedetails() {
		return myconTypedetails;
	}

	public void setMyconTypedetails(int myconTypedetails) {
		this.myconTypedetails = myconTypedetails;
	}

	public String getMypeeridlocal() {
		return mypeeridlocal;
	}

	public void setMypeeridlocal(String mypeeridlocal) {
		this.mypeeridlocal = mypeeridlocal;
	}

	public String getPeeridofRemote() {
		return peeridofRemote;
	}

	public void setPeeridofRemote(String peeridofRemote) {
		this.peeridofRemote = peeridofRemote;
	}

	private OutputStream ostream;
	private int myconTypedetails;

	String mypeeridlocal, peeridofRemote;

	public void run() {
		byte[] handshakebyte = new byte[P2PUtility.THIRTY_TWO];
		byte[] handshakelen;
		PeerInteraction peerintearctionfinalmsg = new PeerInteraction();

		byte[] contentNoPayload = new byte[P2PUtility.FIVE];
		byte[] messageDetailsType;

		try {
			if (this.myconTypedetails == P2PUtility.ONE) {
				boolean temp;

				try {
					ostream.write(PeerHandshake
							.packageTheMessage(new PeerHandshake(P2PUtility.REMOTE_MNGR_MSG3, this.mypeeridlocal)));
				} catch (IOException e) {
					PeerProcess.writeToLogFile(this.mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG10 + e.getMessage());
					temp = P2PUtility.FALSE;
				}
				temp = P2PUtility.TRUE;

				if (temp) {
					PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG1);

				} else {
					PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG2);
					System.exit(P2PUtility.ZERO);
				}
				while (P2PUtility.TRUE) {
					iostrm.read(handshakebyte);
					peerHandshake = PeerHandshake.fillTheMessage(handshakebyte);
					if (peerHandshake.getHeaderString().equals(P2PUtility.REMOTE_MNGR_MSG3)) {

						peeridofRemote = peerHandshake.getStringPeerId();
						PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG4 + peeridofRemote);
						PeerProcess.writeToLogFile(
								mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG5 + peeridofRemote);
						PeerProcess.socketConnectionTables.put(peeridofRemote, this.peersockinfo);
						PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG6 + peeridofRemote);
						break;
					} else {
						continue;
					}
				}
				byte[] byteArray = PeerMessage.convertMsgToByteArray(new PeerMessage(P2PUtility.BITFIELD,
						PeerProcess.getLocalPayload().convertMeToBytes()));
				ostream.write(byteArray);
				PeerProcess.tableOfPeerDetails.get(peeridofRemote).peerstatus = P2PUtility.EIGHT;
			} else {
				while (P2PUtility.TRUE) {
					iostrm.read(handshakebyte);
					peerHandshake = PeerHandshake.fillTheMessage(handshakebyte);
					if (peerHandshake.getHeaderString().equals(P2PUtility.REMOTE_MNGR_MSG3)) {
						peeridofRemote = peerHandshake.getStringPeerId();

						PeerProcess.writeToLogFile(
								mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG5 + peeridofRemote);
						PeerProcess.socketConnectionTables.put(peeridofRemote, this.peersockinfo);
						PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG6 + peeridofRemote);
						break;
					} else {
						continue;
					}
				}

				boolean temp;

				try {
					ostream.write(PeerHandshake
							.packageTheMessage(new PeerHandshake(P2PUtility.REMOTE_MNGR_MSG3, this.mypeeridlocal)));
				} catch (IOException e) {
					PeerProcess.writeToLogFile(this.mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG10 + e.getMessage());
					temp = P2PUtility.FALSE;
				}
				temp = P2PUtility.TRUE;

				if (temp) {
					PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG7);

				} else {
					PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.REMOTE_MNGR_MSG8);
					System.exit(P2PUtility.ZERO);
				}

				PeerProcess.tableOfPeerDetails.get(peeridofRemote).peerstatus = P2PUtility.TWO;
			}

			while (P2PUtility.TRUE) {

				int headerBytes = iostrm.read(contentNoPayload);

				if (headerBytes == P2PUtility.MINUS_ONE)
					break;

				handshakelen = new byte[P2PUtility.FOUR];
				messageDetailsType = new byte[P2PUtility.ONE];
				System.arraycopy(contentNoPayload, P2PUtility.ZERO, handshakelen, P2PUtility.ZERO, P2PUtility.FOUR);
				System.arraycopy(contentNoPayload, P2PUtility.FOUR, messageDetailsType, P2PUtility.ZERO,
						P2PUtility.ONE);
				PeerMessage peermsg = new PeerMessage();
				peermsg.setpeerMsgType(messageDetailsType);
				peermsg.setMessageLength(handshakelen);

				if (peermsg.peerMsgType.equals(P2PUtility.CHOKE)
						|| peermsg.peerMsgType.equals(P2PUtility.UNCHOKE)
						|| peermsg.peerMsgType.equals(P2PUtility.INTERESTED)
						|| peermsg.peerMsgType.equals(P2PUtility.NOTINTERESTED)) {
					peerintearctionfinalmsg.setmessagesToPeer(peermsg);
					peerintearctionfinalmsg.finalPID = this.peeridofRemote;
					PeerProcess.addToMessageQueue(peerintearctionfinalmsg);
				} else {
					int bytesAlreadyRead = P2PUtility.ZERO;
					int bytesRead;
					byte[] dataBuffPayload = new byte[peermsg.contentlen - P2PUtility.ONE];
					while (bytesAlreadyRead < peermsg.contentlen - P2PUtility.ONE) {
						bytesRead = iostrm.read(dataBuffPayload, bytesAlreadyRead,
								peermsg.contentlen - P2PUtility.ONE - bytesAlreadyRead);
						if (bytesRead == P2PUtility.MINUS_ONE)
							return;
						bytesAlreadyRead += bytesRead;
					}

					byte[] dataBuffWithPayload = new byte[peermsg.contentlen + P2PUtility.FOUR];
					System.arraycopy(contentNoPayload, P2PUtility.ZERO, dataBuffWithPayload, P2PUtility.ZERO,
							P2PUtility.FOUR + P2PUtility.ONE);
					System.arraycopy(dataBuffPayload, P2PUtility.ZERO, dataBuffWithPayload,
							P2PUtility.FOUR + P2PUtility.ONE,
							dataBuffPayload.length);

					PeerMessage dataMsgWithPayload = PeerMessage.populateMessage(dataBuffWithPayload);
					peerintearctionfinalmsg.setmessagesToPeer(dataMsgWithPayload);
					peerintearctionfinalmsg.finalPID = peeridofRemote;
					PeerProcess.addToMessageQueue(peerintearctionfinalmsg);
					dataBuffPayload = null;
					dataBuffWithPayload = null;
					bytesAlreadyRead = P2PUtility.ZERO;
					bytesRead = P2PUtility.ZERO;
				}
			}
		} catch (IOException e) {
			// PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.HANDSHAKE_MSG10 + e);
		}

	}

	public RemotePeerManager(String addme, int myport, int contype, String mypid) {
		try {
			this.myconTypedetails = contype;
			this.mypeeridlocal = mypid;
			this.peersockinfo = new Socket(addme, myport);
		} catch (UnknownHostException e) {
			// PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.HANDSHAKE_MSG10 + e);
		} catch (IOException e) {
			// PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.HANDSHAKE_MSG10 + e);
		}
		this.myconTypedetails = contype;

		try {
			iostrm = peersockinfo.getInputStream();
			ostream = peersockinfo.getOutputStream();
		} catch (Exception ex) {
			// PeerProcess.writeToLogFile(mypeeridlocal + P2PUtility.HANDSHAKE_MSG10 + e);
		}
	}

}