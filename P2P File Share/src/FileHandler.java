import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileHandler {
    static HashMap<Integer, byte[]> pieceMap = new HashMap<Integer, byte[]>();
    static String filePath = "../FileToShare/" + ConfigReader.getFileName();

    public static byte[] GetFilePiece(int pieceIndex) {
        return pieceMap.get(pieceIndex);
    }

    public static void InitializePieceMapFromCompleteFile() {
        File file = new File(filePath);
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

    public static boolean CombinePiecesIntoCompleteFile() {
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(new File(filePath));

            int maxIndex = ConfigReader.getFileSize() / ConfigReader.getPieceSize();
            
            for (int i = 0; i < maxIndex; i++) {
                if (!pieceMap.containsKey(i))
                    return false;
                fileOutputStream.write(pieceMap.get(i));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Something wrong outputting the file\n" + e.getMessage());
        }

        return true;
    }

    public static void addPiece(int pieceIndex, byte[] piece) {
        pieceMap.put(pieceIndex, piece);
    }
}
