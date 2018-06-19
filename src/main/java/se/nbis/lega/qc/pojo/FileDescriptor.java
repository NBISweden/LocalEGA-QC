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
    private final int id;

    @SerializedName("stable_id")
    private final String stableId;

    @SerializedName("encrypted_integrity")
    private final EncryptedIntegrity encryptedIntegrity;

    @SerializedName("org_msg")
    private final OriginalMessage originalMessage;

}
