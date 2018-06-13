package se.nbis.lega.qc.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FileDescriptor {

    @SerializedName("id")
    private final String id;

    @SerializedName("stable_id")
    private final String stableId;

    @SerializedName("key_id")
    private final String keyId;

    @SerializedName("header")
    private final String header;

}
