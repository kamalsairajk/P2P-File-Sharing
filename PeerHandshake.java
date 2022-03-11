
import java.io.*;

public class PeerHandshake {

	private String pidofMsg;

	private String headerOfMsg;

	public PeerHandshake() {

	}

	private byte[] peerID = new byte[P2PUtility.FOUR];
	private byte[] zerosinline = new byte[P2PUtility.TEN];

	private byte[] header = new byte[P2PUtility.EIGHTEEN];

	public PeerHandshake(String localheaderOfMsg, String localpeerID) {

		try {
			this.headerOfMsg = localheaderOfMsg;
			this.header = localheaderOfMsg.getBytes(P2PUtility.ENCODING2);
			if (this.header.length > P2PUtility.EIGHTEEN)
				throw new Exception(P2PUtility.HANDSHAKE_MSG1);

			this.pidofMsg = localpeerID;
			this.peerID = localpeerID.getBytes(P2PUtility.ENCODING2);
			if (this.peerID.length > P2PUtility.EIGHTEEN)
				throw new Exception(P2PUtility.HANDSHAKE_MSG2);

			this.zerosinline = P2PUtility.LEADING_ZEROES.getBytes(P2PUtility.ENCODING2);
		} catch (Exception e) {

			PeerProcess.writeToLogFile(P2PUtility.HANDSHAKE_MSG3 + e.toString());
		}

	}

	public String getStringPeerId() {
		return pidofMsg;
	}

	public String getHeaderString() {
		return headerOfMsg;
	}

	public static PeerHandshake fillTheMessage(byte[] msgGot) {

		byte[] pidofMsg = null;
		PeerHandshake peerHandshakeMsg = null;

		byte[] headerOfMsg = null;

		try {
			if (msgGot.length != P2PUtility.THIRTY_TWO)
				throw new Exception(P2PUtility.HANDSHAKE_MSG4);

			peerHandshakeMsg = new PeerHandshake();
			pidofMsg = new byte[P2PUtility.FOUR];

			headerOfMsg = new byte[P2PUtility.EIGHTEEN];

			System.arraycopy(msgGot, P2PUtility.ZERO, headerOfMsg, P2PUtility.ZERO, P2PUtility.EIGHTEEN);
			System.arraycopy(msgGot, P2PUtility.TWENTY_EIGHT, pidofMsg, P2PUtility.ZERO, P2PUtility.FOUR);
			try {
				peerHandshakeMsg.headerOfMsg = (new String(headerOfMsg, P2PUtility.ENCODING2)).toString().trim();
				peerHandshakeMsg.header = peerHandshakeMsg.headerOfMsg.getBytes();
			} catch (UnsupportedEncodingException uee) {
				PeerProcess.writeToLogFile(uee.toString());
			}

			try {
				peerHandshakeMsg.pidofMsg = (new String(pidofMsg, P2PUtility.ENCODING2)).toString().trim();
				peerHandshakeMsg.peerID = peerHandshakeMsg.pidofMsg.getBytes();

			} catch (UnsupportedEncodingException uee) {
				PeerProcess.writeToLogFile(uee.toString());
			}

		} catch (Exception uee) {
			PeerProcess.writeToLogFile(uee.toString());
			peerHandshakeMsg = null;
		}
		return peerHandshakeMsg;
	}

	public String toString() {
		return (P2PUtility.HANDSHAKE_MSG5 + this.pidofMsg + P2PUtility.HANDSHAKE_MSG6 + this.headerOfMsg);
	}

	public static byte[] packageTheMessage(PeerHandshake peerHandshakeMsg) {

		byte[] sendMessage = new byte[P2PUtility.THIRTY_TWO];

		try {
			if (peerHandshakeMsg.header == null) {
				throw new Exception(P2PUtility.HANDSHAKE_MSG7);
			}
			if (peerHandshakeMsg.header.length > P2PUtility.EIGHTEEN
					|| peerHandshakeMsg.header.length == P2PUtility.ZERO) {
				throw new Exception(P2PUtility.HANDSHAKE_MSG7);
			} else {
				System.arraycopy(peerHandshakeMsg.header, P2PUtility.ZERO, sendMessage, P2PUtility.ZERO,
						peerHandshakeMsg.header.length);
			}

			if (peerHandshakeMsg.zerosinline == null) {
				throw new Exception(P2PUtility.HANDSHAKE_MSG8);
			}
			if (peerHandshakeMsg.zerosinline.length > P2PUtility.TEN
					|| peerHandshakeMsg.zerosinline.length == P2PUtility.ZERO) {
				throw new Exception(P2PUtility.HANDSHAKE_MSG8);
			} else {
				System.arraycopy(peerHandshakeMsg.zerosinline, P2PUtility.ZERO, sendMessage, P2PUtility.EIGHTEEN, P2PUtility.NINE);
			}
			if (peerHandshakeMsg.peerID == null) {
				throw new Exception(P2PUtility.HANDSHAKE_MSG9);
			} else if (peerHandshakeMsg.peerID.length > P2PUtility.FOUR
					|| peerHandshakeMsg.peerID.length == P2PUtility.ZERO) {
				throw new Exception(P2PUtility.HANDSHAKE_MSG9);
			} else {
				System.arraycopy(peerHandshakeMsg.peerID, P2PUtility.ZERO, sendMessage, P2PUtility.TWENTY_EIGHT,
						peerHandshakeMsg.peerID.length);
				
			}

		} catch (Exception e) {
			PeerProcess.writeToLogFile(e.toString());
			sendMessage = null;
		}

		return sendMessage;
	}
}
