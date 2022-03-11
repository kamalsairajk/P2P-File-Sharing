import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.RandomAccessFile;

import java.io.FileWriter;
import java.io.FileReader;

public class MessageContent {
	private int partSize;
	private Content[] contentPart;

	public void setcontentPart(Content[] contentPart) {
		this.contentPart = contentPart;
	}

	public void setpartSize(int partSize) {
		this.partSize = partSize;
	}

	public MessageContent() {
		setpartSize((int) Math
				.ceil(((double) P2PConfgUtility.fileSize / (double) P2PConfgUtility.pieceSize)));

		setcontentPart(new Content[partSize]);

		int sizeCtr = P2PUtility.ZERO;
		while (sizeCtr < getpartSize()) {
			this.contentPart[sizeCtr] = new Content();
			sizeCtr++;
		}

	}

	public byte[] convertMeToBytes() {
		return this.getBytes();
	}

	public static MessageContent openUpMessage(byte[] unpackme) {
		MessageContent returnMessagePayload = new MessageContent();
		for (int iterator = P2PUtility.ZERO; iterator < unpackme.length; iterator++) {
			int count = P2PUtility.SEVEN;
			while (P2PUtility.ZERO <= count) {
				if (iterator * P2PUtility.EIGHT
						+ (P2PUtility.EIGHT - count - P2PUtility.ONE) < returnMessagePayload.partSize) {
					if ((unpackme[iterator] & ((P2PUtility.ONE << count))) != P2PUtility.ZERO)
						returnMessagePayload.contentPart[iterator * P2PUtility.EIGHT
								+ (P2PUtility.EIGHT - count - P2PUtility.ONE)].setcontentExists(P2PUtility.ONE);
					else
						returnMessagePayload.contentPart[iterator * P2PUtility.EIGHT
								+ (P2PUtility.EIGHT - count - P2PUtility.ONE)].setcontentExists(P2PUtility.ZERO);
				}
				count--;
			}
		}

		return returnMessagePayload;
	}

	public int getpartSize() {
		return partSize;
	}

	public synchronized boolean compare(MessageContent currentMsgContent) {

		for (int iterator = P2PUtility.ZERO; iterator < currentMsgContent.getpartSize(); iterator++) {
			if (currentMsgContent.getcontentPart()[iterator].getcontentExists() == P2PUtility.ONE
					&& this.getcontentPart()[iterator].getcontentExists() == P2PUtility.ZERO) {
				return P2PUtility.TRUE;
			} else
				continue;
		}

		return P2PUtility.FALSE;
	}

	public synchronized int getdifference(MessageContent currentMsgContent) {
		if (getpartSize() >= currentMsgContent.getpartSize()) {
			for (int iterator = P2PUtility.ZERO; iterator < getpartSize(); iterator++) {
				if (currentMsgContent.getcontentPart()[iterator].getcontentExists() == P2PUtility.ONE
						&& this.getcontentPart()[iterator].getcontentExists() == P2PUtility.ZERO) {
					return iterator;
				}
			}
		} else {
			for (int iterator = P2PUtility.ZERO; iterator < getpartSize(); iterator++) {
				if (currentMsgContent.getcontentPart()[iterator].getcontentExists() == P2PUtility.ONE
						&& this.getcontentPart()[iterator].getcontentExists() == P2PUtility.ZERO) {
					return iterator;
				}
			}
		}

		return P2PUtility.MINUS_ONE;
	}

	public Content[] getcontentPart() {
		return contentPart;
	}

	public byte[] getBytes() {
		int tempSize = getpartSize() / P2PUtility.EIGHT;
		if (partSize % P2PUtility.EIGHT != P2PUtility.ZERO)
			tempSize = tempSize + P2PUtility.ONE;
		byte[] tempBytes = new byte[tempSize];
		int tempInt = P2PUtility.ZERO;
		int counter = P2PUtility.ZERO;
		int counter2;
		for (counter2 = P2PUtility.ONE; counter2 <= this.partSize; counter2++) {

			tempInt = tempInt << P2PUtility.ONE;
			if (this.contentPart[counter2 - P2PUtility.ONE].getcontentExists() == P2PUtility.ONE) {
				tempInt = tempInt + P2PUtility.ONE;
			} else
				tempInt = tempInt + P2PUtility.ZERO;
			if (counter2 % P2PUtility.EIGHT == P2PUtility.ZERO && counter2 != P2PUtility.ZERO) {
				tempBytes[counter] = (byte) tempInt;
				counter = counter + P2PUtility.ONE;
				tempInt = P2PUtility.ZERO;
			}

		}
		if ((counter2 - P2PUtility.ONE) % P2PUtility.EIGHT != P2PUtility.ZERO) {
			tempInt = tempInt << (P2PUtility.EIGHT - ((partSize) - (partSize / P2PUtility.EIGHT) * P2PUtility.EIGHT));
			tempBytes[counter] = (byte) tempInt;
		}
		return tempBytes;
	}

	static String byteArrayToHexString(byte convertme[]) {
		byte mybyte = 0x00;
		int iterator = P2PUtility.ZERO;
		if (convertme == null || convertme.length <= P2PUtility.ZERO)
			return null;

		StringBuffer strbuf = new StringBuffer(convertme.length * 2);

		for (; iterator < convertme.length; iterator++) {
			mybyte = (byte) (convertme[iterator] & 0xF0);
			mybyte = (byte) (mybyte >>> P2PUtility.FOUR);
			mybyte = (byte) (mybyte & 0x0F);
			strbuf.append(P2PUtility.pseudo[(int) mybyte]);
			mybyte = (byte) (convertme[iterator] & 0x0F);
			strbuf.append(P2PUtility.pseudo[(int) mybyte]);
		}
		return new String(strbuf);
	}

	public void initializeBits(int fileExists, String givenPID) {

		if (fileExists == P2PUtility.ONE) {
			int iterator = P2PUtility.ZERO;
			while (iterator < getpartSize()) {
				this.contentPart[iterator].setcontentExists(P2PUtility.ONE);
				this.contentPart[iterator].setfinalRemotePID(givenPID);
				iterator++;
			}
		} else {
			int iterator = P2PUtility.ZERO;
			while (iterator < getpartSize()) {
				this.contentPart[iterator].setcontentExists(P2PUtility.ZERO);
				this.contentPart[iterator].setfinalRemotePID(givenPID);
				iterator++;
			}

		}

	}

	public synchronized void contentUpdate(Content givenContentPart, String remotepid) {
		try {
			if (PeerProcess.getLocalPayload().contentPart[givenContentPart.contentIndex]
					.getcontentExists() == P2PUtility.ONE) {
				logMessage(remotepid + P2PUtility.MSG_CONT_MSG1);
			} else {
				RandomAccessFile randomAccessFile = new RandomAccessFile(
						new File(PeerProcess.peerID, P2PConfgUtility.fileName), P2PUtility.READ_WRITE);
				byte[] byteWrite = givenContentPart.contentBytes;

				randomAccessFile.seek(givenContentPart.contentIndex * P2PConfgUtility.pieceSize);
				randomAccessFile.write(byteWrite);

				this.contentPart[givenContentPart.contentIndex].setcontentExists(P2PUtility.ONE);
				this.contentPart[givenContentPart.contentIndex].setfinalRemotePID(remotepid);
				randomAccessFile.close();

				logMessage(
						PeerProcess.peerID + P2PUtility.MSG_CONT_MSG2 + givenContentPart.contentIndex
								+ P2PUtility.MSG_CONT_MSG3 + remotepid + P2PUtility.MSG_CONT_MSG4
								+ PeerProcess.getLocalPayload().ownPieces());

				if (PeerProcess.getLocalPayload().isCompleted()) {
					PeerProcess.tableOfPeerDetails.get(PeerProcess.peerID).interestedToShare = P2PUtility.ZERO;
					PeerProcess.tableOfPeerDetails.get(PeerProcess.peerID).moduleDone = P2PUtility.ONE;
					PeerProcess.tableOfPeerDetails.get(PeerProcess.peerID).chokeCheck = P2PUtility.ZERO;
					updatePeerInfo(PeerProcess.peerID, P2PUtility.ONE);

					logMessage(PeerProcess.peerID + P2PUtility.MSG_CONT_MSG5);

				}
			}

		} catch (IOException ioe) {
			logMessage(PeerProcess.peerID + P2PUtility.MSG_CONT_MSG6 + ioe.getMessage());
		}

	}

	private void logMessage(String message) {
		PeerProcess.writeToLogFile(message);
	}

	public int ownPieces() {
		int partCount = P2PUtility.ZERO;
		int iterator = P2PUtility.ZERO;
		while (iterator < getpartSize()) {
			if (this.contentPart[iterator].getcontentExists() == P2PUtility.ONE)
				partCount++;
			iterator++;
		}
		return partCount;
	}

	public boolean isCompleted() {
		int iterator = P2PUtility.ZERO;
		while (iterator < getpartSize()) {
			if (this.contentPart[iterator].getcontentExists() == P2PUtility.ZERO) {
				return P2PUtility.FALSE;
			}
			iterator++;
		}

		return P2PUtility.TRUE;
	}

	public void updatePeerInfo(String clientID, int hasFile) {
		BufferedWriter bffwriter = null;
		BufferedReader buffReader = null;

		try {
			buffReader = new BufferedReader(new FileReader(P2PUtility.CONF_FILE1));

			String readdString;
			StringBuffer buffer = new StringBuffer();

			while ((readdString = buffReader.readLine()) != null) {
				if (readdString.trim().split(P2PUtility.STR_FORMATTER)[P2PUtility.ZERO].equals(clientID)) {
					buffer.append(readdString.trim().split(P2PUtility.STR_FORMATTER)[P2PUtility.ZERO] + P2PUtility.SPACE
							+ readdString.trim().split(P2PUtility.STR_FORMATTER)[P2PUtility.ONE] + P2PUtility.SPACE
							+ readdString.trim().split(P2PUtility.STR_FORMATTER)[P2PUtility.TWO] + P2PUtility.SPACE
							+ hasFile);
				} else {
					buffer.append(readdString);

				}
				buffer.append(P2PUtility.NEWLINE);
			}

			buffReader.close();

			bffwriter = new BufferedWriter(new FileWriter(P2PUtility.CONF_FILE1));
			bffwriter.write(buffer.toString());

			bffwriter.close();
		} catch (IOException ioe) {
			logMessage(clientID + P2PUtility.MSG_CONT_MSG7 + ioe.getMessage());
		}
	}

}
