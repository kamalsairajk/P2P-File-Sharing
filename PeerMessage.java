
import java.io.UnsupportedEncodingException;

public class PeerMessage {
	

	public PeerMessage(String msgType) {

		try {

			if (msgType == "0" || msgType == "1"|| msgType == "2"
					|| msgType == "3") {
				this.contentlen = P2PUtility.ONE;
				this.messageLength = ((Integer) P2PUtility.ONE).toString();
				this.computedLen = P2PUtility.convertToBytes(P2PUtility.ONE);
				try {
					this.peerMsgType = msgType.trim();
					this.msgTypeinBytes = this.peerMsgType.getBytes(P2PUtility.ENCODING2);
				} catch (UnsupportedEncodingException uee) {
					PeerProcess.writeToLogFile(uee.toString());
				}
				this.content = null;
			} else
				throw new Exception(P2PUtility.PEERMSG_MSG1);

		} catch (Exception customex) {
			PeerProcess.writeToLogFile(customex.toString());
		}

	}

	public String peerMsgType;
	private byte[] computedLen = null;
	private String messageLength;

	public byte[] content = null;
	public int contentlen = P2PUtility.ONE;
	private byte[] msgTypeinBytes = null;
	



	public static PeerMessage populateMessage(byte[] populateThis) {

		byte[] msgLength = new byte[P2PUtility.FOUR];

		PeerMessage peerMsg = new PeerMessage();

		byte[] content = null;

		byte[] msgType = new byte[P2PUtility.ONE];
		int computedLenLocal;

		try {

			if (populateThis == null)
				throw new Exception(P2PUtility.PEERMSG_MSG10);
			else if (populateThis.length < P2PUtility.FOUR + P2PUtility.ONE)
				throw new Exception(P2PUtility.PEERMSG_MSG11);

			System.arraycopy(populateThis, P2PUtility.ZERO, msgLength, P2PUtility.ZERO, P2PUtility.FOUR);
			System.arraycopy(populateThis, P2PUtility.FOUR, msgType, P2PUtility.ZERO, P2PUtility.ONE);

			peerMsg.setMessageLength(msgLength);
			peerMsg.setpeerMsgType(msgType);

			computedLenLocal = P2PUtility.byteArrayToInt(msgLength);

			if (computedLenLocal > P2PUtility.ONE) {
				content = new byte[computedLenLocal - P2PUtility.ONE];
				System.arraycopy(populateThis, P2PUtility.FOUR + P2PUtility.ONE, content, P2PUtility.ZERO,
						populateThis.length - P2PUtility.FOUR - P2PUtility.ONE);
				peerMsg.setcontent(content);
			}

			content = null;
		} catch (Exception ee) {
			PeerProcess.writeToLogFile(ee.toString());
			peerMsg = null;
		}
		return peerMsg;
	}

	public PeerMessage() {

	}

	public PeerMessage(String msgType, byte[] content) {

		try {
			if (content != null) {

				this.contentlen = content.length + P2PUtility.ONE;
				this.messageLength = ((Integer) (content.length + P2PUtility.ONE)).toString();
				this.computedLen = P2PUtility.convertToBytes(content.length + P2PUtility.ONE);

				if (this.computedLen.length > P2PUtility.FOUR)
					throw new Exception(P2PUtility.PEERMSG_MSG2);

				this.content = content;

			} else {
				if (msgType == P2PUtility.CHOKE || msgType == P2PUtility.UNCHOKE || msgType == P2PUtility.INTERESTED
						|| msgType == P2PUtility.NOTINTERESTED) {
					this.contentlen = P2PUtility.ONE;
					this.messageLength = ((Integer) P2PUtility.ONE).toString();
					this.computedLen = P2PUtility.convertToBytes(P2PUtility.ONE);

					this.content = null;
				} else
					throw new Exception(P2PUtility.PEERMSG_MSG3);

			}

			try {
				this.peerMsgType = msgType.trim();
				this.msgTypeinBytes = this.peerMsgType.getBytes(P2PUtility.ENCODING2);
			} catch (UnsupportedEncodingException uee) {
				PeerProcess.writeToLogFile(uee.toString());
			}

			if (this.msgTypeinBytes.length > P2PUtility.ONE)
				throw new Exception(P2PUtility.PEERMSG_MSG4);

		} catch (Exception customexe) {
			PeerProcess.writeToLogFile(customexe.toString());
		}

	}

	public void setMessageLength(byte[] len) {

		Integer tempLen = P2PUtility.byteArrayToInt(len);

		this.contentlen = tempLen;
		this.computedLen = len;
		this.messageLength = tempLen.toString();

	}

	public String toString() {
		String tempString = null;
		try {
			tempString = P2PUtility.PEERMSG_MSG5 + this.messageLength + P2PUtility.PEERMSG_MSG6 + this.peerMsgType
					+ P2PUtility.PEERMSG_MSG7 + (new String(this.content, P2PUtility.ENCODING2)).toString().trim();
		} catch (UnsupportedEncodingException uee) {
			PeerProcess.writeToLogFile(uee.toString());
		}
		return tempString;
	}

	public static byte[] convertMsgToByteArray(PeerMessage packMeUp) {
		byte[] stremOfMMsg = null;
		int typeOfMsg;

		try {

			typeOfMsg = Integer.parseInt(packMeUp.peerMsgType);
			if (packMeUp.computedLen.length > P2PUtility.FOUR)
				throw new Exception(P2PUtility.PEERMSG_MSG8);
			else if (typeOfMsg < P2PUtility.ZERO || typeOfMsg > P2PUtility.SEVEN)
				throw new Exception(P2PUtility.PEERMSG_MSG9);
			else if (packMeUp.msgTypeinBytes == null)
				throw new Exception(P2PUtility.PEERMSG_MSG9);
			else if (packMeUp.computedLen == null)
				throw new Exception(P2PUtility.PEERMSG_MSG8);

			if (packMeUp.content != null) {
				stremOfMMsg = new byte[P2PUtility.FOUR + P2PUtility.ONE + packMeUp.content.length];

				System.arraycopy(packMeUp.computedLen, P2PUtility.ZERO, stremOfMMsg, P2PUtility.ZERO,
						packMeUp.computedLen.length);
				System.arraycopy(packMeUp.msgTypeinBytes, P2PUtility.ZERO, stremOfMMsg, P2PUtility.FOUR,
						P2PUtility.ONE);
				System.arraycopy(packMeUp.content, P2PUtility.ZERO, stremOfMMsg, P2PUtility.FOUR + P2PUtility.ONE,
						packMeUp.content.length);

			} else {
				stremOfMMsg = new byte[P2PUtility.FIVE];

				System.arraycopy(packMeUp.computedLen, P2PUtility.ZERO, stremOfMMsg, P2PUtility.ZERO,
						packMeUp.computedLen.length);
				System.arraycopy(packMeUp.msgTypeinBytes, P2PUtility.ZERO, stremOfMMsg, P2PUtility.FOUR,
						P2PUtility.ONE);

			}

		} catch (Exception uee) {
			PeerProcess.writeToLogFile(uee.toString());
			stremOfMMsg = null;
		}

		return stremOfMMsg;
	}

	public void setpeerMsgType(byte[] type) {
		try {

			this.msgTypeinBytes = type;
			this.peerMsgType = new String(type, P2PUtility.ENCODING2);

		} catch (UnsupportedEncodingException uee) {
			PeerProcess.writeToLogFile(uee.toString());
		}
	}

	public void setcontent(byte[] content) {
		this.content = content;
	}

}
