import java.util.Date;

public class RemotePeerInfo implements Comparable<RemotePeerInfo> {

	public String getRemotePeerID() {
		return remotePeerID;
	}

	public void setRemotePeerID(String remotePeerID) {
		this.remotePeerID = remotePeerID;
	}

	public String getRemotePeerAddrs() {
		return remotePeerAddrs;
	}

	public void setRemotePeerAddrs(String remotePeerAddrs) {
		this.remotePeerAddrs = remotePeerAddrs;
	}

	public String remotePeerID;
	public String remotePeerAddrs;
	public String remotePort;
	public int firstPeerCheck;

	public String getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

	public int getFirstPeerCheck() {
		return firstPeerCheck;
	}

	public void setFirstPeerCheck(int firstPeerCheck) {
		this.firstPeerCheck = firstPeerCheck;
	}

	public double checkrate = P2PUtility.ZERO;
	public int interestedToShare = 1;
	public int checkprefNgbr = P2PUtility.ZERO;
	public int optmisticceck = P2PUtility.ZERO;
	public int chokeCheck = P2PUtility.ONE;

	public double getCheckrate() {
		return checkrate;
	}

	public void setCheckrate(double checkrate) {
		this.checkrate = checkrate;
	}

	public int getInterestedToShare() {
		return interestedToShare;
	}

	public MessageContent bitField;
	public int peerstatus = P2PUtility.MINUS_ONE;
	public int remotePeerLoc;
	public int moduleDone = P2PUtility.ZERO;
	public int handshakecheck = P2PUtility.ZERO;
	public Date begindatetime;
	public Date endatetime;

	public void setInterestedToShare(int interestedToShare) {
		this.interestedToShare = interestedToShare;
	}

	public int getCheckprefNgbr() {
		return checkprefNgbr;
	}

	public void setCheckprefNgbr(int checkprefNgbr) {
		this.checkprefNgbr = checkprefNgbr;
	}

	public int getOptmisticceck() {
		return optmisticceck;
	}

	public void setOptmisticceck(int optmisticceck) {
		this.optmisticceck = optmisticceck;
	}

	public int getChokeCheck() {
		return chokeCheck;
	}

	public void setChokeCheck(int chokeCheck) {
		this.chokeCheck = chokeCheck;
	}

	public MessageContent getBitField() {
		return bitField;
	}

	public void setBitField(MessageContent bitField) {
		this.bitField = bitField;
	}

	public int getPeerstatus() {
		return peerstatus;
	}

	public void setPeerstatus(int peerstatus) {
		this.peerstatus = peerstatus;
	}

	public int getRemotePeerLoc() {
		return remotePeerLoc;
	}

	public void setRemotePeerLoc(int remotePeerLoc) {
		this.remotePeerLoc = remotePeerLoc;
	}

	public int getModuleDone() {
		return moduleDone;
	}

	public void setModuleDone(int moduleDone) {
		this.moduleDone = moduleDone;
	}

	public int getHandshakecheck() {
		return handshakecheck;
	}

	public void setHandshakecheck(int handshakecheck) {
		this.handshakecheck = handshakecheck;
	}

	public Date getBegindatetime() {
		return begindatetime;
	}

	public void setBegindatetime(Date begindatetime) {
		this.begindatetime = begindatetime;
	}

	public Date getEndatetime() {
		return endatetime;
	}

	public void setEndatetime(Date endatetime) {
		this.endatetime = endatetime;
	}

	public RemotePeerInfo(String pId, String pAddress, String pPort, int pIndex) {
		setRemotePeerID(pId);
		setRemotePeerAddrs(pAddress);
		setRemotePort(pPort);
		setBitField(new MessageContent());
		setRemotePeerLoc(pIndex);
	}

	public int compareTo(RemotePeerInfo o1) {

		if (this.checkrate > o1.checkrate)
			return P2PUtility.ZERO;
		else if (this.checkrate == o1.checkrate)
			return P2PUtility.ZERO;
		else
			return P2PUtility.MINUS_ONE;
	}

	public RemotePeerInfo(String pId, String pAddress, String pPort, int pIsFirstPeer, int pIndex) {
		remotePeerID = pId;
		remotePeerAddrs = pAddress;
		remotePort = pPort;
		firstPeerCheck = pIsFirstPeer;
		bitField = new MessageContent();
		remotePeerLoc = pIndex;
	}

}
