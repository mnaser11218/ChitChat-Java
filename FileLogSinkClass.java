import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

    public class FileLogSinkClass{
        private String filePath;

        public FileLogSinkClass(String filePath) {
            this.filePath = filePath;
        }
        public void log(String message) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void emptyFileWriter() throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

        }

    }

