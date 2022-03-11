import java.util.Scanner;

import java.io.File;

public class P2PConfgUtility {
	

	public static int unchokingInterval; // the interval for unchoke

	public static void configureParameters() {

		try {
			Scanner configfile = new Scanner(new File(P2PUtility.UTIL_CONFIG_NAME));
			while (configfile.hasNextLine()) {
				String properties = configfile.nextLine();
				String[] property = properties.split(P2PUtility.SPACE);
				if (property[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.UTIL_CONFIG_PRFRD_NGBRS)) {
					P2PConfgUtility.preferredNeighborCount = Integer.parseInt(property[P2PUtility.ONE]);
				} else if (property[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.UTIL_CONFIG_UNCHOKE_INTRVL)) {
					P2PConfgUtility.unchokingInterval = Integer.parseInt(property[P2PUtility.ONE]);
				} else if (property[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.UTIL_CONFIG_OPTMISTIC_INTRVL)) {
					P2PConfgUtility.optimisticUnchokingInterval = Integer.parseInt(property[P2PUtility.ONE]);
				} else if (property[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.UTIL_CONFIG_FILENAME)) {
					P2PConfgUtility.fileName = property[P2PUtility.ONE];
				} else if (property[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.UTIL_CONFIG_FILESIZE)) {
					P2PConfgUtility.fileSize = Integer.parseInt(property[P2PUtility.ONE]);
				} else if (property[P2PUtility.ZERO].equalsIgnoreCase(P2PUtility.UTIL_CONFIG_PIECESIZE)) {
					P2PConfgUtility.pieceSize = Integer.parseInt(property[P2PUtility.ONE]);
				}
			}

			configfile.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	public static int fileSize; // size of the file to transfer

	public static int optimisticUnchokingInterval; // optimistically select neighbors and unchoke

	public static int preferredNeighborCount; // count of preferred neighbours from the property file

	public static int pieceSize; // the size of piece that must be sent in one connection

	public static int numberOfPieces = (int) Math.ceil((double) fileSize / (double) pieceSize);
	public static String fileName; // name of the file
}
