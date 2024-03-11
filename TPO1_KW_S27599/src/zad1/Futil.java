package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class Futil {
    public static void processDir(String dirName, String resultFileName) {
        Path startdir = Paths.get(dirName);
        Path resultFilePath = Paths.get(resultFileName);

        try (
                FileChannel outputChannel = FileChannel.open(resultFilePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        ) {
            Files.walkFileTree(startdir, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;

                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try (
                            FileChannel inputChannel = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.CREATE);
                    ) {
                        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                        inputChannel.read(buffer);
                        buffer.flip();
                        CharBuffer decoded = Charset.forName("cp1250").decode(buffer);
                        Charset UTF8 = StandardCharsets.UTF_8;
                        ByteBuffer encoded = UTF8.encode(decoded);
                        outputChannel.write(encoded);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
