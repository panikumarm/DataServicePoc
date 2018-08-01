package com.data.service.file;

public interface IFileHandler<T> {
    void writeToFile(T t);
}
