package se.nbis.lega.qc.pojo;

import java.util.Arrays;

public enum StorageDriver {

    FILESYSTEM("FileStorage"),
    S3("S3Storage");

    private final String driver;

    StorageDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }

    public static StorageDriver getValue(String driver) {
        return Arrays.stream(StorageDriver.values()).filter(a -> a.driver.equals(driver)).findAny().orElseThrow(() -> new IllegalArgumentException(String.format("StorageDriver with name %s is not supported.", driver)));
    }

}
