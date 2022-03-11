import java.util.Date;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Enumeration;

import java.net.Socket;
import java.io.File;


public class MessageManager implements Runnable {
	private RandomAccessFile myFile;

	public RandomAccessFile getMyFile() {
		return myFile;
	}

	public void setMyFile(RandomAccessFile myFile) {
		this.myFile = myFile;
	}

	private void log(String message) {
		PeerProcess.writeToLogFile(message);
	}

	public void run() {
		PeerMessage remotPeerMessage;
		PeerInteraction peerInteractionDetails;

		while (P2PUtility.TRUE) {
			peerInteractionDetails = PeerProcess.removeFromMessageQueue();
			while (peerInteractionDetails == null) {
				Thread.currentThread();
				try {
					Thread.sleep(P2PUtility.FIVEHUNDRD);
				} catch (InterruptedException intrpt) {
					intrpt.printStackTrace();

				}
				peerInteractionDetails = PeerProcess.removeFromMessageQueue();
			}

			remotPeerMessage = peerInteractionDetails.getmessagesToPeer();

			int currentStatus = PeerProcess.tableOfPeerDetails.get(peerInteractionDetails.finalPID).peerstatus;

			if (remotPeerMessage.peerMsgType.equals(P2PUtility.HAVE)
					&& currentStatus != P2PUtility.FOURTEEN) {

				int checkLoc = PeerProcess.getLocalPayload()
						.getdifference(PeerProcess.tableOfPeerDetails.get(peerInteractionDetails.finalPID).bitField);
				if (checkLoc > P2PUtility.ZERO)
					log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG1
							+ peerInteractionDetails.finalPID + P2PUtility.MSG_MNGR_MSG2 + checkLoc
							+ P2PUtility.PERIOD);
				if (isInterested(remotPeerMessage, peerInteractionDetails.finalPID)) {

					sendInterested(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
							peerInteractionDetails.finalPID);
					PeerProcess.tableOfPeerDetails.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.NINE;
				} else {

					sendNotInterested(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
							peerInteractionDetails.finalPID);
					PeerProcess.tableOfPeerDetails.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.THIRTEEN;
				}
			} else {
				switch (currentStatus) {

					case P2PUtility.TWO:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.BITFIELD)) {
							log(
									PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG3
											+ peerInteractionDetails.finalPID);
							sendMessagePayload(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
									peerInteractionDetails.finalPID);
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.THREE;
						}
						break;

					case P2PUtility.THREE:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.NOTINTERESTED)) {
							// Result Log 9:
							PeerProcess
									.writeToLogFile(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG4
											+ peerInteractionDetails.finalPID + P2PUtility.PERIOD);
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).interestedToShare = P2PUtility.ZERO;
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.FIVE;
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).handshakecheck = P2PUtility.ONE;
						} else if (remotPeerMessage.peerMsgType.equals(P2PUtility.INTERESTED)) {
							// Result Log 8:
							log(
									PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG5
											+ peerInteractionDetails.finalPID
											+ P2PUtility.PERIOD);
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).interestedToShare = P2PUtility.ONE;
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).handshakecheck = P2PUtility.ONE;

							if (!PeerProcess.tableOfPrefNgbrs.containsKey(peerInteractionDetails.finalPID)
									&& !PeerProcess.tableOfUnchokNgbrs
											.containsKey(peerInteractionDetails.finalPID)) {
								chokingPeers(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).chokeCheck = P2PUtility.ONE;
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.SIX;
							} else {
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).chokeCheck = P2PUtility.ZERO;
								unchokingPeers(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.FOUR;
							}
						}
						break;

					case P2PUtility.FOUR:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.REQUEST)) {
							// sending piece here
							byte[] byteChunkLoc = remotPeerMessage.content;
							int chunkloc = P2PUtility.byteArrayToInt(byteChunkLoc);

							log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG10 + chunkloc
									+ P2PUtility.MSG_MNGR_MSG11 + peerInteractionDetails.finalPID);

							byte[] alreadyRead = new byte[P2PConfgUtility.pieceSize];
							int bytecountRead = P2PUtility.ZERO;

							try {
								myFile = new RandomAccessFile(new File(PeerProcess.peerID, P2PConfgUtility.fileName),
										P2PUtility.READONLY);
								myFile.seek(chunkloc * P2PConfgUtility.pieceSize);
								bytecountRead = myFile.read(alreadyRead, P2PUtility.ZERO, P2PConfgUtility.pieceSize);
							} catch (IOException e) {
								log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG12 + e.toString());
							}
							if (bytecountRead == P2PUtility.ZERO) {
								log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG13);
							} else if (bytecountRead < P2PUtility.ZERO) {
								log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG14);
							}

							byte[] buffer = new byte[bytecountRead + P2PUtility.FOUR];
							System.arraycopy(byteChunkLoc, P2PUtility.ZERO, buffer, P2PUtility.ZERO, P2PUtility.FOUR);
							System.arraycopy(alreadyRead, P2PUtility.ZERO, buffer, P2PUtility.FOUR, bytecountRead);

							PeerMessage sendMessage = new PeerMessage(P2PUtility.PIECE, buffer);
							byte[] b = PeerMessage.convertMsgToByteArray(sendMessage);

							// write to stream
							try {
								PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID).getOutputStream()
										.write(b);
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}

							buffer = null;
							alreadyRead = null;
							b = null;
							byteChunkLoc = null;
							sendMessage = null;

							try {
								myFile.close();
							} catch (Exception e) {
							}
							// done sending piece

							if (!PeerProcess.tableOfPrefNgbrs.containsKey(peerInteractionDetails.finalPID)
									&& !PeerProcess.tableOfUnchokNgbrs
											.containsKey(peerInteractionDetails.finalPID)) {
								chokingPeers(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).chokeCheck = P2PUtility.ONE;
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.SIX;
							}
						}
						break;

					case P2PUtility.EIGHT:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.BITFIELD)) {
							if (isInterested(remotPeerMessage, peerInteractionDetails.finalPID)) {

								sendInterested(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.NINE;
							} else {

								sendNotInterested(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.THIRTEEN;
							}
						}
						break;

					case P2PUtility.NINE:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.CHOKE)) {
							// Result Log 6:
							log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG6
									+ peerInteractionDetails.finalPID + P2PUtility.PERIOD);
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.FOURTEEN;
						} else if (remotPeerMessage.peerMsgType.equals(P2PUtility.UNCHOKE)) {
							// Result Log 5:
							log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG7
									+ peerInteractionDetails.finalPID + P2PUtility.PERIOD);
							int changePresent = PeerProcess.getLocalPayload()
									.getdifference(PeerProcess.tableOfPeerDetails
											.get(peerInteractionDetails.finalPID).bitField);
							if (changePresent == P2PUtility.MINUS_ONE) {
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.THIRTEEN;

							} else {
								
								byte[] pieceByte = new byte[P2PUtility.FOUR];
								for (int counter = P2PUtility.ZERO; counter < P2PUtility.FOUR; counter++) {
									pieceByte[counter] = P2PUtility.ZERO;
								}

								byte[] pieceIndexByte = P2PUtility.convertToBytes(changePresent);
								System.arraycopy(pieceIndexByte, P2PUtility.ZERO, pieceByte, P2PUtility.ZERO,
										pieceIndexByte.length);
								PeerMessage message = new PeerMessage(P2PUtility.REQUEST, pieceByte);
								byte[] b = PeerMessage.convertMsgToByteArray(message);

								// write to stream
								try {
									PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID)
											.getOutputStream().write(b);
								} catch (IOException ioe) {
									ioe.printStackTrace();
								}

								pieceByte = null;
								pieceIndexByte = null;
								b = null;
								message = null;

							}
							log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG8 + changePresent
									+ P2PUtility.MSG_MNGR_MSG9 + peerInteractionDetails.finalPID);

							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.ELEVEN;
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).begindatetime = new Date();
						}
						break;

					case P2PUtility.ELEVEN:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.PIECE)) {
							byte[] buffer = remotPeerMessage.content;
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).endatetime = new Date();
							long timeLapse = PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).endatetime.getTime()
									- PeerProcess.tableOfPeerDetails.get(peerInteractionDetails.finalPID).begindatetime
											.getTime();

											
							PeerProcess.tableOfPeerDetails.get(
									peerInteractionDetails
											.finalPID).checkrate = ((double) (buffer.length + P2PUtility.FOUR
													+ P2PUtility.ONE)
													/ (double) timeLapse)
													* P2PUtility.SEN;

							PeerProcess.getLocalPayload().contentUpdate(Content.decodePiece(buffer),
									peerInteractionDetails.finalPID);

							int neededChunkLoc = PeerProcess.getLocalPayload()
									.getdifference(PeerProcess.tableOfPeerDetails
											.get(peerInteractionDetails.finalPID).bitField);
							if (neededChunkLoc != P2PUtility.MINUS_ONE) {

								byte[] pieceByte = new byte[P2PUtility.FOUR];
								for (int counter = P2PUtility.ZERO; counter < P2PUtility.FOUR; counter++) {
									pieceByte[counter] = P2PUtility.ZERO;
								}

								byte[] pieceIndexByte = P2PUtility.convertToBytes(neededChunkLoc);
								System.arraycopy(pieceIndexByte, P2PUtility.ZERO, pieceByte, P2PUtility.ZERO,
										pieceIndexByte.length);
								PeerMessage message = new PeerMessage(P2PUtility.REQUEST, pieceByte);
								byte[] b = PeerMessage.convertMsgToByteArray(message);

								// write to stream
								try {
									PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID)
											.getOutputStream().write(b);
								} catch (IOException ioe) {
									ioe.printStackTrace();
								}

								pieceByte = null;
								pieceIndexByte = null;
								b = null;
								message = null;

								PeerProcess
										.writeToLogFile(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG8 + neededChunkLoc
												+ P2PUtility.MSG_MNGR_MSG9 + peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.ELEVEN;
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).begindatetime = new Date();
							} else
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.THIRTEEN;

							PeerProcess.editPeers();


							Enumeration<String> pipdata = PeerProcess.tableOfPeerDetails.keys();
							while (pipdata.hasMoreElements()) {
								String finalpip = (String) pipdata.nextElement();
								RemotePeerInfo pref = PeerProcess.tableOfPeerDetails.get(finalpip);

								if (finalpip.equals(PeerProcess.peerID))
									continue;

								if (pref.moduleDone == P2PUtility.ZERO && pref.chokeCheck == P2PUtility.ZERO
										&& pref.handshakecheck == P2PUtility.ONE) {

									sendHave(PeerProcess.socketConnectionTables.get(finalpip), finalpip);
									PeerProcess.tableOfPeerDetails.get(finalpip).peerstatus = P2PUtility.THREE;

								}

							}

							buffer = null;
							remotPeerMessage = null;

						} else if (remotPeerMessage.peerMsgType.equals(P2PUtility.CHOKE)) {
							// Result Log 6:
							log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG6
									+ peerInteractionDetails.finalPID + P2PUtility.PERIOD);
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.FOURTEEN;
						}
						break;

					case P2PUtility.FOURTEEN:
						if (remotPeerMessage.peerMsgType.equals(P2PUtility.HAVE)) {
							if (isInterested(remotPeerMessage, peerInteractionDetails.finalPID)) {

								sendInterested(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.NINE;
							} else {

								sendNotInterested(PeerProcess.socketConnectionTables.get(peerInteractionDetails.finalPID),
										peerInteractionDetails.finalPID);
								PeerProcess.tableOfPeerDetails
										.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.THIRTEEN;
							}
						} else if (remotPeerMessage.peerMsgType.equals(P2PUtility.UNCHOKE)) {
							// Result Log 5:
							log(
									PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG7
											+ peerInteractionDetails.finalPID);
							PeerProcess.tableOfPeerDetails
									.get(peerInteractionDetails.finalPID).peerstatus = P2PUtility.FOURTEEN;
						}
						break;

				}
			}

		}
	}

	private void sendInterested(Socket socket, String presentPeerId) {
		log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG15 + presentPeerId);
		PeerMessage message = new PeerMessage(P2PUtility.INTERESTED);
		// write to stream
		try {
			socket.getOutputStream().write(PeerMessage.convertMsgToByteArray(message));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void sendNotInterested(Socket socket, String presentPeerId) {
		PeerProcess
				.writeToLogFile(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG16 + presentPeerId);
		PeerMessage message = new PeerMessage(P2PUtility.NOTINTERESTED);
		// write to stream
		try {
			socket.getOutputStream().write(PeerMessage.convertMsgToByteArray(message));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void unchokingPeers(Socket socket, String presentPeerId) {

		log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG17 + presentPeerId);
		PeerMessage message = new PeerMessage(P2PUtility.UNCHOKE);
		// write to stream
		try {
			socket.getOutputStream().write(PeerMessage.convertMsgToByteArray(message));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private boolean isInterested(PeerMessage message, String remotePid) {

		MessageContent messageContent = MessageContent.openUpMessage(message.content);
		PeerProcess.tableOfPeerDetails.get(remotePid).bitField = messageContent;

		if (PeerProcess.getLocalPayload().compare(messageContent))
			return P2PUtility.TRUE;
		return P2PUtility.FALSE;
	}

	private void chokingPeers(Socket socket, String presentPeerId) {
		log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG18 + presentPeerId);
		// write to stream
		try {
			socket.getOutputStream().write(PeerMessage.convertMsgToByteArray(new PeerMessage(P2PUtility.CHOKE)));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void sendHave(Socket socket, String presentPeerId) {

		log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG19 + presentPeerId);
		byte[] encodedMessagePayload = PeerProcess.getLocalPayload().convertMeToBytes();
		PeerMessage message = new PeerMessage(P2PUtility.HAVE, encodedMessagePayload);
		// write to stream
		try {
			socket.getOutputStream().write(PeerMessage.convertMsgToByteArray(message));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		encodedMessagePayload = null;
	}

	private static String ongoingRemotePID = null;

	public MessageManager() {
		ongoingRemotePID = null;
	}

	public static String getOngoingRemotePID() {
		return ongoingRemotePID;
	}

	public MessageManager(String currentPeerId2) {
		ongoingRemotePID = currentPeerId2;
	}

	public static void setOngoingRemotePID(String ongoingRemotePID) {
		MessageManager.ongoingRemotePID = ongoingRemotePID;
	}

	public static String printmymessages(byte[] bytes) {
		StringBuilder strbldr = new StringBuilder();
		strbldr.append(P2PUtility.BRACE2);
		for (byte mybyte : bytes) {
			strbldr.append(String.format(P2PUtility.STR_FORMATTER2, mybyte));
		}
		return (strbldr.append(P2PUtility.BRACE2_CLOSE)).toString();
	}

	
	private void sendMessagePayload(Socket socket, String presentPeerId) {

		log(PeerProcess.peerID + P2PUtility.MSG_MNGR_MSG20 + presentPeerId);
		byte[] mytransformedmsg = PeerProcess.getLocalPayload().convertMeToBytes();

		// write to stream
		try {
			socket.getOutputStream().write(PeerMessage.convertMsgToByteArray(new PeerMessage(P2PUtility.BITFIELD, mytransformedmsg)));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		mytransformedmsg = null;
	}

}
