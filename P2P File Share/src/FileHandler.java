import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FileHandler {
    volatile static ConcurrentHashMap<Integer, byte[]> pieceMap = new ConcurrentHashMap<>();
    public static final String FILE_PATH = "./FileToShare/";
    public static final String FILE_NAME = ConfigReader.getFileName();

    public static byte[] getFilePiece(int pieceIndex) {
        return pieceMap.get(pieceIndex);
    }

    public static synchronized void initializePieceMapFromCompleteFile() {
        File file = new File(FILE_PATH+FILE_NAME);
        if (!file.exists()) {
            System.out.println(Logger.getTimestamp() + ": FATAL: System cannot find file that the client should start with");
            return;
        }
        
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            System.out.println(Logger.getTimestamp() + ": Given file is " + fileBytes.length + " bytes long");
            int pieceSize = ConfigReader.getPieceSize();

            for (int i = 0; i < fileBytes.length; i+= pieceSize) {
                int pieceIndex = i/pieceSize;

                int pieceBytesEndIndex = i + pieceSize;
                if (pieceBytesEndIndex > fileBytes.length)
                    pieceBytesEndIndex = fileBytes.length;

                pieceMap.put(pieceIndex, Arrays.copyOfRange(fileBytes, i, pieceBytesEndIndex));
            }

            
        }
        catch (IOException ioException) {
            throw new RuntimeException("Something wrong with file path probably in FileHandler\n" + ioException.getMessage());
        }
    }

    public static synchronized boolean combinePiecesIntoCompleteFile() {
        System.out.println(Logger.getTimestamp() + ": ALL PIECES RECEIVED! Attempting to combine them into file...");

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(new File(FILE_PATH + "NEW_" + FILE_NAME));

            int maxIndex = Bitfield.calculatePieceAmt() - 1;
            
            for (int i = 0; i < maxIndex; i++) {
                if (!pieceMap.containsKey(i)) {
                    System.out.println(Logger.getTimestamp() + ": Tried to combine all piece into complete file, but there was a missing piece at index: " + i);
                    return false;
                }
                fileOutputStream.write(pieceMap.get(i));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Something wrong outputting the file\n" + e.getMessage());
        }
        finally {
            try {
                fileOutputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Problem closing file output stream\n" + e.getMessage());
            }
        }

        Logger.logFullDownloadComplete();
        System.out.println(Logger.getTimestamp() + ": FILE RECOMBINATION COMPLETE!");
        return true;
    }

    public static synchronized void addPiece(int pieceIndex, byte[] piece) {
        pieceMap.put(pieceIndex, piece);
    }
}
