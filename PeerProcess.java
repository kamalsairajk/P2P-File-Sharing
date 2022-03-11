import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import java.net.*;

public class PeerProcess {


	public static MessageContent getLocalPayload() {
		return localPayload;
	}

	public static void setLocalPayload(MessageContent localPayload) {
		PeerProcess.localPayload = localPayload;
	}

	public static synchronized PeerInteraction removeFromMessageQueue() {
		PeerInteraction message = null;
		if (!queueOfFinalInteractions.isEmpty()) {
			message = queueOfFinalInteractions.remove();
		}
		return message;
	}

	public static void editPeers() {
		try {
			Scanner scanner = new Scanner(new File(P2PUtility.CONF_FILE1));

			while (scanner.hasNextLine()) {
				String[] params = scanner.nextLine().trim().split(P2PUtility.STR_FORMATTER);
				String peerID = params[P2PUtility.ZERO];
				int isCompleted = Integer.parseInt(params[P2PUtility.THREE]);
				if (isCompleted == P2PUtility.ONE) {
					tableOfPeerDetails.get(peerID).moduleDone = P2PUtility.ONE;
					tableOfPeerDetails.get(peerID).interestedToShare = P2PUtility.ZERO;
					tableOfPeerDetails.get(peerID).chokeCheck = P2PUtility.ZERO;
				}
			}
			scanner.close();
		} catch (Exception excptnn) {
			writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG1 + excptnn.toString());
		}
	}

	private ServerSocket serverListenerSocket = null;
	private int serverPortNo;
	public static String peerID;
	private int ongoingIndexOfPeer;
	private Thread listenToRemote; 
	private static boolean isDownloadDone = P2PUtility.FALSE;
	public static MessageContent localPayload = null;
	

	public static class PickNeighbors extends TimerTask {
		public void run() {

			editPeers();
			Enumeration<String> enumData = tableOfPeerDetails.keys();
			int countInterested = P2PUtility.ZERO;
			String perfeerred = P2PUtility.NOTHING;
			while (enumData.hasMoreElements()) {
				String singleData = (String) enumData.nextElement();
				RemotePeerInfo pref = tableOfPeerDetails.get(singleData);
				if (singleData.equals(peerID))
					continue;
				if (pref.moduleDone == P2PUtility.ZERO && pref.handshakecheck == P2PUtility.ONE) {
					countInterested++;
				} else if (pref.moduleDone == P2PUtility.ONE) {
					try {
						tableOfPrefNgbrs.remove(singleData);
					} catch (Exception exe) {
					}
				}
			}
			if (countInterested > P2PConfgUtility.preferredNeighborCount) {
				if (!tableOfPrefNgbrs.isEmpty())
					tableOfPrefNgbrs.clear();
				List<RemotePeerInfo> remotePeerList = new ArrayList<RemotePeerInfo>(tableOfPeerDetails.values());
				Collections.sort(remotePeerList, new Comparator<RemotePeerInfo>() {
					public int compare(RemotePeerInfo peer1, RemotePeerInfo peer2) {
						if (peer1 == null && peer2 == null)
							return P2PUtility.ZERO;

						if (peer1 == null)
							return P2PUtility.ONE;

						if (peer2 == null)
							return P2PUtility.MINUS_ONE;

						if (peer1 instanceof Comparable) {
							return peer2.compareTo(peer1);
						} else {
							return peer2.toString().compareTo(peer1.toString());

						}
					}
				});
				int p2pCounter = P2PUtility.ZERO;
				int iterator = P2PUtility.ZERO;
				while (iterator < remotePeerList.size()) {
					if (p2pCounter > P2PConfgUtility.preferredNeighborCount - P2PUtility.ONE)
						break;
					if (remotePeerList.get(iterator).handshakecheck == P2PUtility.ONE
							&& !remotePeerList.get(iterator).remotePeerID.equals(peerID)
							&& tableOfPeerDetails
									.get(remotePeerList.get(iterator).remotePeerID).moduleDone == P2PUtility.ZERO) {
						tableOfPeerDetails
								.get(remotePeerList.get(iterator).remotePeerID).checkprefNgbr = P2PUtility.ONE;
						tableOfPrefNgbrs.put(remotePeerList.get(iterator).remotePeerID,
								tableOfPeerDetails.get(remotePeerList.get(iterator).remotePeerID));

						p2pCounter++;

						perfeerred = perfeerred + remotePeerList.get(iterator).remotePeerID + P2PUtility.COMMA;

						if (tableOfPeerDetails.get(remotePeerList.get(iterator).remotePeerID).chokeCheck == P2PUtility.ONE) {

							writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG3 + remotePeerList.get(iterator).remotePeerID);
							try {
								PeerProcess.socketConnectionTables.get(remotePeerList.get(iterator).remotePeerID)
										.getOutputStream()
										.write(PeerMessage.convertMsgToByteArray(new PeerMessage("1")));
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
							PeerProcess.tableOfPeerDetails
									.get(remotePeerList.get(iterator).remotePeerID).chokeCheck = P2PUtility.ZERO;
							writeHaveMsg(PeerProcess.socketConnectionTables.get(remotePeerList.get(iterator).remotePeerID),
									remotePeerList.get(iterator).remotePeerID);
							PeerProcess.tableOfPeerDetails
									.get(remotePeerList.get(iterator).remotePeerID).peerstatus = P2PUtility.THREE;
						}

					}

					iterator++;
				}
			} else

			{
				enumData = tableOfPeerDetails.keys();
				while (enumData.hasMoreElements()) {
					String singleData = (String) enumData.nextElement();
					RemotePeerInfo remoteInfoPref = tableOfPeerDetails.get(singleData);
					if (singleData.equals(peerID))
						continue;

					if (remoteInfoPref.moduleDone == P2PUtility.ZERO
							&& remoteInfoPref.handshakecheck == P2PUtility.ONE) {
						if (!tableOfPrefNgbrs.containsKey(singleData)) {
							perfeerred = perfeerred + singleData + P2PUtility.COMMA;
							tableOfPrefNgbrs.put(singleData, tableOfPeerDetails.get(singleData));
							tableOfPeerDetails.get(singleData).checkprefNgbr = P2PUtility.ONE;
						}
						if (remoteInfoPref.chokeCheck == P2PUtility.ONE) {
							writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG3 + singleData);

							try {
								PeerProcess.socketConnectionTables.get(singleData).getOutputStream()
										.write(PeerMessage.convertMsgToByteArray(new PeerMessage("1")));
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
							PeerProcess.tableOfPeerDetails.get(singleData).chokeCheck = P2PUtility.ZERO;
							writeHaveMsg(PeerProcess.socketConnectionTables.get(singleData), singleData);
							PeerProcess.tableOfPeerDetails.get(singleData).peerstatus = P2PUtility.THREE;
						}

					}

				}
			}
			// Result Log 3: Preferred Neighbors
			if (perfeerred != P2PUtility.NOTHING) {
				perfeerred = perfeerred.replaceAll(P2PUtility.REPLACE_REG, P2PUtility.NOTHING);
				PeerProcess.writeToLogFile(
						PeerProcess.peerID + P2PUtility.PEERPROCESS_MSG2 + perfeerred + P2PUtility.PERIOD);
			}

		}

	}

	private static void writeHaveMsg(Socket mysoc, String myrempid) {
		byte[] mycontenntEncoded = PeerProcess.localPayload.convertMeToBytes();

		writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG4 + myrempid);

		try {
			mysoc.getOutputStream()
					.write(PeerMessage.convertMsgToByteArray(new PeerMessage(P2PUtility.HAVE, mycontenntEncoded)));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		mycontenntEncoded = null;
	}
	
	public static volatile Timer mytimer;
	public static volatile Hashtable<String, RemotePeerInfo> tableOfPeerDetails = new Hashtable<String, RemotePeerInfo>();
	public static volatile Hashtable<String, RemotePeerInfo> tableOfPrefNgbrs = new Hashtable<String, RemotePeerInfo>();
	public static volatile Hashtable<String, RemotePeerInfo> tableOfUnchokNgbrs = new Hashtable<String, RemotePeerInfo>();
	


	public static class OptimisticallyUnchokingNeighbors extends TimerTask {

		public void run() {

			editPeers();
			if (!tableOfUnchokNgbrs.isEmpty())
				tableOfUnchokNgbrs.clear();
			Enumeration<String> myenum = tableOfPeerDetails.keys();
			Vector<RemotePeerInfo> mypeers = new Vector<RemotePeerInfo>();
			while (myenum.hasMoreElements()) {
				String enumdata = (String) myenum.nextElement();
				RemotePeerInfo remotepeerinfolocal = tableOfPeerDetails.get(enumdata);
				if (remotepeerinfolocal.chokeCheck == P2PUtility.ONE && !enumdata.equals(peerID)
						&& remotepeerinfolocal.moduleDone == P2PUtility.ZERO
						&& remotepeerinfolocal.handshakecheck == P2PUtility.ONE)
					mypeers.add(remotepeerinfolocal);
			}

			// Random neighbours logic
			if (mypeers.size() > P2PUtility.ZERO) {
				Collections.shuffle(mypeers);
				RemotePeerInfo firstpeer = mypeers.firstElement();

				tableOfPeerDetails.get(firstpeer.remotePeerID).optmisticceck = P2PUtility.ONE;
				tableOfUnchokNgbrs.put(firstpeer.remotePeerID, tableOfPeerDetails.get(firstpeer.remotePeerID));
				// Result Log 4:
				PeerProcess.writeToLogFile(
						PeerProcess.peerID + P2PUtility.PEERPROCESS_MSG5 + firstpeer.remotePeerID + P2PUtility.PERIOD);

				if (tableOfPeerDetails.get(firstpeer.remotePeerID).chokeCheck == P2PUtility.ONE) {
					PeerProcess.tableOfPeerDetails.get(firstpeer.remotePeerID).chokeCheck = P2PUtility.ZERO;
					writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG3 + firstpeer.remotePeerID);

					try {
						PeerProcess.socketConnectionTables.get(firstpeer.remotePeerID).getOutputStream()
								.write(PeerMessage.convertMsgToByteArray(new PeerMessage("1")));
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					byte[] mycontenntEncoded = PeerProcess.localPayload.convertMeToBytes();
					writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG4 + firstpeer.remotePeerID);

					try {
						PeerProcess.socketConnectionTables.get(firstpeer.remotePeerID).getOutputStream().write(
								PeerMessage.convertMsgToByteArray(new PeerMessage(P2PUtility.HAVE, mycontenntEncoded)));
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}

					mycontenntEncoded = null;
					PeerProcess.tableOfPeerDetails.get(firstpeer.remotePeerID).peerstatus = P2PUtility.THREE;
				}
			}

		}

	}

	private static volatile Queue<PeerInteraction> queueOfFinalInteractions = new LinkedList<PeerInteraction>();
	public static Hashtable<String, Socket> socketConnectionTables = new Hashtable<String, Socket>();
	private static Vector<Thread> listOfRemotePeers = new Vector<Thread>();
	public static Vector<Thread> processVectorForSending = new Vector<Thread>();
	private static Thread peerProcessMsg;

	public static void writeToLogFile(String logme) {

		P2PUtility.writeToLogFile(initializeTime() + P2PUtility.PEERPROCESS_MSG6 + logme);
		System.out.println(initializeTime() + P2PUtility.PEERPROCESS_MSG6 + logme);
	}

	public static String initializeTime() {
		SimpleDateFormat date = new SimpleDateFormat(P2PUtility.DATEFORMAT);
		Calendar calendar = Calendar.getInstance();
		return date.format(calendar.getTime());

	}

	public static void configureParameters() {

		try {
			Scanner configFile = new Scanner(new File(P2PUtility.UTIL_CONFIG_NAME));
			while (configFile.hasNextLine()) {
				String property = configFile.nextLine();
				String[] kvPairs = property.split(P2PUtility.STR_FORMATTER);
				if (kvPairs[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.PEERPROCESS_NO_OF_PEERS)) {
					P2PConfgUtility.preferredNeighborCount = Integer.parseInt(kvPairs[P2PUtility.ONE]);
				} else if (kvPairs[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.PEERPROCESS_UNCHOKINGINTERVAL)) {
					P2PConfgUtility.unchokingInterval = Integer.parseInt(kvPairs[P2PUtility.ONE]);
				} else if (kvPairs[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.PEERPROCESS_OPTIMSTIC)) {
					P2PConfgUtility.optimisticUnchokingInterval = Integer.parseInt(kvPairs[P2PUtility.ONE]);
				} else if (kvPairs[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.PEERPROCESS_FILENAME)) {
					P2PConfgUtility.fileName = kvPairs[P2PUtility.ONE];
				} else if (kvPairs[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.PEERPROCESS_FILESIZE)) {
					P2PConfgUtility.fileSize = Integer.parseInt(kvPairs[P2PUtility.ONE]);
				} else if (kvPairs[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.PEERPROCESS_PIECESIZE)) {
					P2PConfgUtility.pieceSize = Integer.parseInt(kvPairs[P2PUtility.ONE]);
				}
			}
			configFile.close();
		} catch (Exception e) {
			writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG7 + e.toString());
		}
	}
	public static synchronized void addToMessageQueue(PeerInteraction message) {
		queueOfFinalInteractions.add(message);
	}
	@SuppressWarnings(P2PUtility.PEERPROCESS_MSG8)
	public static void main(String[] args) {
		PeerProcess mypeerprocess = new PeerProcess();
		peerID = args[P2PUtility.ZERO];
		try {
			P2PUtility.start(P2PUtility.PEERPROCESS_MSG9 + peerID + P2PUtility.PEERPROCESS_MSG10);
			writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG11);
			configureParameters();
			try {
				Scanner read = new Scanner(new File(P2PUtility.CONF_FILE1));
				int lineCounter = P2PUtility.ZERO;
				while (read.hasNextLine()) {
					String peerinfoData = read.nextLine().trim();
					String[] peerInfoMap = peerinfoData.split(P2PUtility.SPACE);
					tableOfPeerDetails.put(peerInfoMap[P2PUtility.ZERO],
							new RemotePeerInfo(peerInfoMap[P2PUtility.ZERO], peerInfoMap[P2PUtility.ONE],
									peerInfoMap[P2PUtility.TWO],
									Integer.parseInt(peerInfoMap[3]),
									lineCounter));
					lineCounter++;
				}
				read.close();
			} catch (Exception ex) {
				writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG12 + ex.toString());
			}
			boolean initialPeerCheck = P2PUtility.FALSE;
			Enumeration<String> myenum = tableOfPeerDetails.keys();
			while (myenum.hasMoreElements()) {
				String data = (String) myenum.nextElement();
				if (!data.equals(peerID)) {
					tableOfPrefNgbrs.put(data, tableOfPeerDetails.get(data));
				}
			}

			Enumeration<String> myenum2 = tableOfPeerDetails.keys();

			while (myenum2.hasMoreElements()) {
				RemotePeerInfo peerInfo = tableOfPeerDetails.get(myenum2.nextElement());
				if (peerInfo.remotePeerID.equals(peerID)) {
					mypeerprocess.serverPortNo = Integer.parseInt(peerInfo.remotePort);
					mypeerprocess.ongoingIndexOfPeer = peerInfo.remotePeerLoc;
					if (peerInfo.firstPeerCheck == P2PUtility.ONE) {
						initialPeerCheck = P2PUtility.TRUE;
						break;
					}
				}
			}

			localPayload = new MessageContent();
			localPayload.initializeBits(initialPeerCheck ? P2PUtility.ONE : P2PUtility.ZERO, peerID);

			peerProcessMsg = new Thread(new MessageManager(peerID));
			peerProcessMsg.start();

			if (initialPeerCheck) {
				try {
					mypeerprocess.serverListenerSocket = new ServerSocket(mypeerprocess.serverPortNo);

					mypeerprocess.listenToRemote = new Thread(
							new TriggerInteraction(mypeerprocess.serverListenerSocket, peerID));
					mypeerprocess.listenToRemote.start();
				} catch (SocketTimeoutException soctimeout) {
					writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG13 + soctimeout.toString());
					P2PUtility.writerClose();
					System.exit(P2PUtility.ZERO);
				} catch (IOException ex) {
					writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG14
							+ mypeerprocess.serverPortNo + ex.toString());
					P2PUtility.writerClose();
					System.exit(P2PUtility.ZERO);
				}
			}
			
			else {
				try {
					new File(peerID).mkdir();

					OutputStream outputStreamtemp = new FileOutputStream(new File(peerID, P2PConfgUtility.fileName),
							true);
					byte toWrite = P2PUtility.ZERO;

					for (int counter = P2PUtility.ZERO; counter < P2PConfgUtility.fileSize; counter++)
						outputStreamtemp.write(toWrite);
					outputStreamtemp.close();
				} catch (Exception e1) {
					writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG15 + e1.getMessage());
				}

				myenum2 = tableOfPeerDetails.keys();
				while (myenum2.hasMoreElements()) {
					RemotePeerInfo peerInfo = tableOfPeerDetails.get(myenum2.nextElement());
					if (mypeerprocess.ongoingIndexOfPeer > peerInfo.remotePeerLoc) {
						Thread tempThread = new Thread(new RemotePeerManager(peerInfo.remotePeerAddrs,
								Integer.parseInt(peerInfo.remotePort), 1, peerID));
						listOfRemotePeers.add(tempThread);
						tempThread.start();
					}
				}

				try {
					mypeerprocess.serverListenerSocket = new ServerSocket(mypeerprocess.serverPortNo);
					mypeerprocess.listenToRemote = new Thread(
							new TriggerInteraction(mypeerprocess.serverListenerSocket, peerID));
					mypeerprocess.listenToRemote.start();
				} catch (SocketTimeoutException xsoctimeout) {
					writeToLogFile(
							peerID + P2PUtility.PEERPROCESS_MSG16 + xsoctimeout.toString());
					P2PUtility.writerClose();
					System.exit(P2PUtility.ZERO);
				} catch (IOException ex) {
					writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG17
							+ mypeerprocess.serverPortNo + P2PUtility.SPACE + ex.toString());
					P2PUtility.writerClose();
					System.exit(P2PUtility.ZERO);
				}
			}

			mytimer = new Timer();
			mytimer.schedule(new PickNeighbors(), P2PUtility.ZERO,
					P2PConfgUtility.unchokingInterval * P2PUtility.ONETHOUSAND);

			mytimer = new Timer();
			mytimer.schedule(new OptimisticallyUnchokingNeighbors(), P2PUtility.ZERO,
					P2PConfgUtility.optimisticUnchokingInterval * P2PUtility.ONETHOUSAND);

			while (P2PUtility.TRUE) {

				String line;
				int hasFileCount = P2PUtility.ONE;

				try {
					BufferedReader bufferedREader = new BufferedReader(new FileReader(P2PUtility.CONF_FILE1));

					while ((line = bufferedREader.readLine()) != null) {
						hasFileCount = hasFileCount
								* Integer.parseInt(line.trim().split(P2PUtility.STR_FORMATTER)[P2PUtility.THREE]);
					}
					if (hasFileCount == P2PUtility.ZERO) {
						bufferedREader.close();
						isDownloadDone = P2PUtility.FALSE;
					} else {
						bufferedREader.close();
						isDownloadDone = P2PUtility.TRUE;
					}

				} catch (Exception e) {
					writeToLogFile(e.toString());
					isDownloadDone = P2PUtility.FALSE;
				}
				if (isDownloadDone) {
					writeToLogFile(P2PUtility.PEERPROCESS_MSG18);

					mytimer.cancel();
					mytimer.cancel();

					try {
						Thread.currentThread();
						Thread.sleep(P2PUtility.TWOTHOUSAND);
					} catch (InterruptedException intruptexe) {
					}

					if (mypeerprocess.listenToRemote.isAlive())
						mypeerprocess.listenToRemote.stop();

					if (peerProcessMsg.isAlive())
						peerProcessMsg.stop();

					for (int counter = P2PUtility.ZERO; counter < listOfRemotePeers.size(); counter++)
						if (listOfRemotePeers.get(counter).isAlive())
							listOfRemotePeers.get(counter).stop();

					for (int counter = P2PUtility.ZERO; counter < processVectorForSending.size(); counter++)
						if (processVectorForSending.get(counter).isAlive())
							processVectorForSending.get(counter).stop();

					break;
				} else {
					try {
						Thread.currentThread();
						Thread.sleep(P2PUtility.FIVETHOUSAND);
					} catch (InterruptedException ex) {
					}
				}
			}
		} catch (Exception ex) {
			writeToLogFile(peerID + P2PUtility.PEERPROCESS_MSG19 + ex.getMessage());
		} finally {
			writeToLogFile(peerID +P2PUtility.PEERPROCESS_MSG20);
			P2PUtility.writerClose();
			System.exit(P2PUtility.ZERO);
		}
	}



}
