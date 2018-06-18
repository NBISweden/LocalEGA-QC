package se.nbis.lega.qc.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class FileDescriptor {

    @SerializedName("user")
    private final String userName;

    @SerializedName("user_id")
    private final String userId;

    @SerializedName("filepath")
    private final String filePath;

    @SerializedName("file_id")
    private final String id;

    @SerializedName("stable_id")
    private final String stableId;

    @SerializedName("key_id")
    private final String keyId;

    @SerializedName("header")
    private final String header;

}
