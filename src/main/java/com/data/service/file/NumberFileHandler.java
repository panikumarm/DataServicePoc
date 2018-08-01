package com.data.service.file;

import com.data.service.constants.DataServiceConstants;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NumberFileHandler implements IFileHandler<Integer> {

    private Path path;
    private FileChannel fileChannel;
    private final Charset utf8 = StandardCharsets.UTF_8;
    private CharBuffer buffer = CharBuffer.allocate(System.lineSeparator().length() + DataServiceConstants.NUMBER_BYTE_SIZE);

    public NumberFileHandler() {
        this.init();
    }

    private void init() {
        try {
            createFile();
            fileChannel = FileChannel.open(path, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            System.out.println("Unable to create the file channel: " + exception.getMessage());
        }
    }

    private void createFile() {
        path = Paths.get(getPath());
        if (Files.exists(path)) removeFile();
        try {
            Files.createFile(path);
        } catch (IOException e) {
            System.out.println("Unable to create the file: " + e.getMessage());
        }
    }

    private String getPath() {
        return SystemUtils.USER_HOME.toString() + DataServiceConstants.FILE_PATH_SEPARATOR + DataServiceConstants.LOG_FILE_NAME;
    }

    private void removeFile() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("Unable to delete the log file ");
        }
    }

    @Override
    public void writeToFile(Integer number) {
        buffer.put(prependZeroes(String.valueOf(number)) + System.lineSeparator());
        buffer.flip();
        ByteBuffer byteBuffer = utf8.encode(buffer);
        buffer.clear();
        try {
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            System.out.println("Unable to write the number to the file " + number + " exception: " + e.getMessage());
        }
    }

    private String prependZeroes(String number) {
        if (number.length() < DataServiceConstants.NUMBER_BYTE_SIZE)
            return ("000000000" + number).substring(number.length());
        return number;
    }
}
