
public class PeerInteraction {
	private PeerMessage messagesToPeer;

	public void setmessagesToPeer(PeerMessage dataMessage) {
		this.messagesToPeer = dataMessage;
	}

	public PeerMessage getmessagesToPeer() {
		return messagesToPeer;
	}

	public PeerInteraction() {
		this.finalPID = null;

		messagesToPeer = new PeerMessage();

	}

	public String finalPID; // the final target peer id

	

}
