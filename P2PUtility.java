import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class P2PUtility {

	static OutputStreamWriter streamWriteSupporter;
	static FileOutputStream fileToWrite;

	public static final boolean TRUE = true;
	public static final boolean FALSE = false;
	public static String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	public static final String INTERESTED = "2";
	public static final String HAVE = "4";
	public static final int FIVEHUNDRD = 500;
	public static final int BYTEVALUE = 4;
	public static final int MINUS_ONE = -1;
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int NINE = 9;
	public static final int TEN = 10;
	public static final int ELEVEN = 11;
	public static final int THIRTEEN = 13;
	public static final int FOURTEEN = 14;
	public static final int EIGHTEEN = 18;
	public static final int TWENTY_EIGHT = 28;
	public static final int THIRTY_TWO = 32;
	public static final int SEN = 100;
	public static final int ONETHOUSAND = 1000;
	public static final int TWOTHOUSAND = 2000;
	public static final int FIVETHOUSAND = 5000;
	public static final String NOTINTERESTED = "3";
	public static final String MSG_MNGR_MSG1 = " received the 'have' message from ";
	public static final String MSG_MNGR_MSG2 = " for the piece ";
	public static final String MSG_MNGR_MSG3 = " received the 'bitfield' message from ";
	public static final String MSG_MNGR_MSG4 = " received the 'not interested' message from ";
	public static final String MSG_MNGR_MSG5 = " received the 'interested' message from  ";
	public static final String MSG_MNGR_MSG6 = " is choked by ";
	public static final String MSG_MNGR_MSG7 = " is unchoked by ";
	public static final String MSG_MNGR_MSG8 = " 'requests' for piece ";
	public static final String MSG_MNGR_MSG9 = " from ";
	public static final String MSG_MNGR_MSG10 = " is sending the 'piece' message for piece ";
	public static final String MSG_MNGR_MSG11 = " to ";
	public static final String MSG_MNGR_MSG12 = " ERROR in reading the file : ";
	public static final String MSG_MNGR_MSG13 = " ERROR :  Zero bytes read from the file !";
	public static final String MSG_MNGR_MSG14 = " ERROR : File could not be read properly.";
	public static final String MSG_MNGR_MSG15 = " is sending the 'interested' message to ";
	public static final String MSG_MNGR_MSG16 = " is sending the 'not interested' message to  ";
	public static final String MSG_MNGR_MSG17 = " is sending the 'unchoke' message to ";
	public static final String MSG_MNGR_MSG18 = " is sending the 'choke' message to ";
	public static final String MSG_MNGR_MSG19 = " is sending the 'have' message to ";
	public static final String MSG_MNGR_MSG20 = " is sending the 'bitfield' message to ";
	public static final String PIECE = "7";
	public static final String UTIL_CONFIG_NAME = "Common.cfg";
	public static final String UTIL_CONFIG_PRFRD_NGBRS = "NumberOfPreferredNeighbors";
	public static final String UTIL_CONFIG_UNCHOKE_INTRVL = "UnchokingInterval";
	public static final String UTIL_CONFIG_OPTMISTIC_INTRVL = "OptimisticUnchokingInterval";
	public static final String UTIL_CONFIG_FILENAME = "FileName";
	public static final String UTIL_CONFIG_FILESIZE = "FileSize";
	public static final String UTIL_CONFIG_PIECESIZE = "PieceSize";
	public static final String REQUEST = "6";
	public static final String TRIGGER_EXCPT = " Exception in connection: ";

	public static final String HANDSHAKE_MSG1 = "Header exceeds limit length.";
	public static final String HANDSHAKE_MSG2 = "Peer ID exceeds limit length.";
	public static final String HANDSHAKE_MSG3 = "Error in Handshake";
	public static final String HANDSHAKE_MSG4 = "Byte array length not matching.";
	public static final String HANDSHAKE_MSG5 = "Handshake : Peer Id = ";
	public static final String HANDSHAKE_MSG6 = ", Header  =";
	public static final String HANDSHAKE_MSG7 = "Wrong Header.";
	public static final String HANDSHAKE_MSG8 = "Wrong zero bits field.";
	public static final String HANDSHAKE_MSG9 = "Wrong peer id.";
	public static final String HANDSHAKE_MSG10 = " run exception in remote peer handler ";

	public static final String PEERMSG_MSG1 = "Wrong selection";
	public static final String PEERMSG_MSG2 = "Msg length exceeds limit length.";
	public static final String PEERMSG_MSG3 = "Payload should not be null";
	public static final String PEERMSG_MSG4 = "MessageType length exceeds limit length.";
	public static final String PEERMSG_MSG5 = "[Message] : Message Length - ";
	public static final String PEERMSG_MSG6 = ", Message msgType - ";
	public static final String PEERMSG_MSG7 = ", Data - ";
	public static final String PEERMSG_MSG8 = "Wrong message length.";
	public static final String PEERMSG_MSG9 = "Wrong message type.";
	public static final String PEERMSG_MSG10 = "Wrong data.";
	public static final String PEERMSG_MSG11 = "Byte array length is too small...";

	public static final String PEERPROCESS_MSG1 = "peerinfo";
	public static final String PEERPROCESS_MSG2 = " has the preferred neighbors ";
	public static final String PEERPROCESS_MSG3 = " is sending the 'unchoke' message to ";
	public static final String PEERPROCESS_MSG4 = " is sending the 'have' message to ";
	public static final String PEERPROCESS_MSG5 = " has the optimistically unchoked neighbor ";
	public static final String PEERPROCESS_MSG6 = ": Peer ";
	public static final String PEERPROCESS_NO_OF_PEERS = "NumberOfPreferredNeighbors";
	public static final String PEERPROCESS_UNCHOKINGINTERVAL = "UnchokingInterval";
	public static final String PEERPROCESS_OPTIMSTIC = "OptimisticUnchokingInterval";
	public static final String PEERPROCESS_FILENAME = "FileName";
	public static final String PEERPROCESS_FILESIZE = "FileSize";
	public static final String PEERPROCESS_PIECESIZE = "PieceSize";

	public static final String PEERPROCESS_MSG7 = "common config";
	public static final String PEERPROCESS_MSG8 = "deprecation";
	public static final String PEERPROCESS_MSG9 = "log_peer_";
	public static final String PEERPROCESS_MSG10 = ".log";
	public static final String PEERPROCESS_MSG11 = " is started";
	public static final String PEERPROCESS_MSG12 = "peer info";
	public static final String PEERPROCESS_MSG13 = " gets time out expetion: ";
	public static final String PEERPROCESS_MSG14 = " gets exception in Starting Listening thread: ";
	public static final String PEERPROCESS_MSG15 = " ERROR in creating the file : ";
	public static final String PEERPROCESS_MSG16 = " gets time out exception in Starting the listening thread: ";
	public static final String PEERPROCESS_MSG17 = " gets exception in Starting the listening thread: ";
	public static final String PEERPROCESS_MSG18 = "All peers have completed downloading the file.";
	public static final String PEERPROCESS_MSG19 = " Exception in ending : ";
	public static final String PEERPROCESS_MSG20 = " process has been terminated";

	public static final String REMOTE_MNGR_MSG1 = " 'handshake' has been sent";
	public static final String REMOTE_MNGR_MSG2 = " 'handshake' sending failed.";
	public static final String REMOTE_MNGR_MSG3 = "P2PFILESHARINGPROJ";
	public static final String REMOTE_MNGR_MSG4 = " makes a connection to ";
	public static final String REMOTE_MNGR_MSG5 = " received the 'handshake' message from ";
	public static final String REMOTE_MNGR_MSG6 = " is connected from ";
	public static final String REMOTE_MNGR_MSG7 = " 'handshake' message has been sent successfully.";
	public static final String REMOTE_MNGR_MSG8 = " 'handshake' message sending failed.";
	public static final String REMOTE_MNGR_MSG9 = " Error ";
	public static final String REMOTE_MNGR_MSG10 =" sendingHandshake  ";
	public static final String REMOTE_MNGR_MSG11 =" receivePiece : ";

	public static final String DATEFORMAT = "MM-dd-yyyy HH:mm:ss";
	public static final String PERIOD = ".";
	public static final String BRACE = "{";
	public static final String BRACE_CLOSE = "}";
	public static final String BRACE2 = "[ ";
	public static final String BRACE2_CLOSE = "]";
	public static final String COMMA = ", ";
	public static final String REPLACE_REG = ", $";
	public static final String CON_EXISTS = " contentExists='";
	public static final String APOSTP = "'";
	public static final String SPACE = " ";
	public static final String NOTHING = "";
	public static final String NEWLINE = "\n";
	public static final String FINAL_REM_MSG = ", finalRemotePID='";
	public static final String CON_INX = ", contentIndex='";
	public static final String CON_BYT = ", contentBytes='";

	public static final String MSG_CONT_MSG1 = " has the piece already.";
	public static final String MSG_CONT_MSG2 = " has downloaded the piece ";
	public static final String MSG_CONT_MSG3 = " from ";
	public static final String MSG_CONT_MSG4 = ". Now the number of pieces it has is ";
	public static final String MSG_CONT_MSG5 = " has downloaded the complete file.";
	public static final String MSG_CONT_MSG6 = " ERROR in updating bitfield ";
	public static final String MSG_CONT_MSG7 = " Error in updating the PeerInfo.cfg ";

	public static final String READ_WRITE = "rw";
	public static final String READONLY = "r";
	public static final String CONF_FILE1 = "PeerInfo.cfg";

	public static final String STR_FORMATTER = "\\s+";
	public static final String STR_FORMATTER2 = "0x%02X ";

	public static final String UNCHOKE = "1";

	public static final String BITFIELD = "5";

	public static final String CHOKE = "0";
	public static final String ENCODING = "UTF-8";
	public static final String ENCODING2 = "UTF8";
	public static final String LEADING_ZEROES = "0000000000";

	public static void start(String fileName) throws IOException {
		fileToWrite = new FileOutputStream(fileName);
		streamWriteSupporter = new OutputStreamWriter(fileToWrite, ENCODING);
	}

	public static void writerClose() {
		try {
			streamWriteSupporter.flush();
			fileToWrite.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int byteArrayToInt(byte[] mybytes) {

		int converted = ZERO;
		for (int counter = ZERO; counter < FOUR; counter++) {
			int shift = (THREE - counter) * EIGHT;
			converted += (mybytes[counter + ZERO] & 0x000000FF) << shift;
		}
		return converted;
	}

	public static void writeToLogFile(String content) {
		try {
			streamWriteSupporter.write(content + NEWLINE);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static byte[] convertToBytes(int value) {
		byte[] converted = new byte[FOUR];
		int tracker = ZERO;
		while (tracker < FOUR) {
			int offset = (converted.length - ONE - tracker) * EIGHT;
			converted[tracker] = (byte) ((value >>> offset) & 0xFF);
			tracker++;
		}
		return converted;
	}

	static String byteArrayToHexString(byte tobeconverted[]) {

		int counter = ZERO;
		byte converted = 0x00;

		if (tobeconverted == null || tobeconverted.length <= ZERO)
			return null;

		StringBuffer out = new StringBuffer(tobeconverted.length * TWO);

		while (counter < tobeconverted.length) {
			converted = (byte) (tobeconverted[counter] & 0xF0);
			converted = (byte) (converted >>> FOUR);

			converted = (byte) (converted & 0x0F);

			out.append(pseudo[(int) converted]);

			converted = (byte) (tobeconverted[counter] & 0x0F);

			out.append(pseudo[(int) converted]);
			counter++;

		}
		return new String(out);
	}

}
