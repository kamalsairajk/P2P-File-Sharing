import java.util.Objects;

public class Content {
	public byte[] contentBytes;

	private String finalRemotePID;

	private int contentExists;

	public int contentIndex;

	public Content() {
		setfinalRemotePID(null);
		contentIndex = P2PUtility.MINUS_ONE;
		contentBytes = new byte[P2PConfgUtility.pieceSize];
		setcontentExists(P2PUtility.ZERO);

	}

	public Content(int contentExists, String finalRemotePID, int contentIndex, byte[] contentBytes) {

		setcontentExists(contentExists);

		this.contentBytes = contentBytes;
		setfinalRemotePID(finalRemotePID);

		this.contentIndex = contentIndex;
	}

	@Override
	public boolean equals(Object objectToCompare) {
		if (objectToCompare == this)
			return P2PUtility.TRUE;
		if (!(objectToCompare instanceof Content)) {
			return P2PUtility.FALSE;
		}
		Content piece = (Content) objectToCompare;
		return contentExists == piece.contentExists && Objects.equals(finalRemotePID, piece.finalRemotePID)
				&& contentIndex == piece.contentIndex && Objects.equals(contentBytes, piece.contentBytes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contentExists, finalRemotePID, contentIndex, contentBytes);
	}

	@Override
	public String toString() {
		return P2PUtility.BRACE + P2PUtility.CON_EXISTS + this.contentExists + P2PUtility.APOSTP
				+ P2PUtility.FINAL_REM_MSG + this.finalRemotePID + P2PUtility.APOSTP
				+ P2PUtility.CON_INX + this.contentIndex + P2PUtility.APOSTP + P2PUtility.CON_BYT + this.contentBytes
				+ P2PUtility.APOSTP + P2PUtility.BRACE_CLOSE;
	}

	public static Content decodePiece(byte[] messageContent) {
		byte[] tempbytIn = new byte[P2PUtility.BYTEVALUE];
		Content piece = new Content();
		System.arraycopy(messageContent, P2PUtility.ZERO, tempbytIn, P2PUtility.ZERO, P2PUtility.BYTEVALUE);
		piece.contentIndex = P2PUtility.byteArrayToInt(tempbytIn);
		piece.contentBytes = new byte[messageContent.length - P2PUtility.BYTEVALUE];
		System.arraycopy(messageContent, P2PUtility.BYTEVALUE, piece.contentBytes, P2PUtility.ZERO,
		messageContent.length - P2PUtility.BYTEVALUE);
		return piece;
	}

	public void setcontentExists(int contentExists) {
		this.contentExists = contentExists;
	}

	public void setfinalRemotePID(String finalRemotePID) {
		this.finalRemotePID = finalRemotePID;
	}

	public int getcontentExists() {
		return this.contentExists;
	}
}
