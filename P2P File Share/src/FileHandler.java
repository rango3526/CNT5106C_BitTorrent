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
    static final String FILE_PATH = "../FileToShare/" + ConfigReader.getFileName();

    public static byte[] GetFilePiece(int pieceIndex) {
        return pieceMap.get(pieceIndex);
    }

    public static synchronized void initializePieceMapFromCompleteFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("This system is not starting with a file.");
            return;
        }
        
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            int pieceSize = ConfigReader.getPieceSize();

            for (int i = 0; i < fileBytes.length + pieceSize; i+= pieceSize) {
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
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(new File(FILE_PATH));

            int maxIndex = ConfigReader.getFileSize() / ConfigReader.getPieceSize();
            
            for (int i = 0; i < maxIndex; i++) {
                if (!pieceMap.containsKey(i)) {
                    System.out.println("Tried to combine all piece into complete file, but there was a missing piece at index: " + i);
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
        return true;
    }

    public static synchronized void addPiece(int pieceIndex, byte[] piece) {
        pieceMap.put(pieceIndex, piece);
    }
}
