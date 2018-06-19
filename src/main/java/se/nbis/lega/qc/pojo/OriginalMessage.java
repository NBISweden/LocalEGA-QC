package se.nbis.lega.qc.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OriginalMessage {

    @SerializedName("user")
    private final String userName;

    @SerializedName("filepath")
    private final String filePath;

    @SerializedName("stable_id")
    private final String stableId;

    @SerializedName("encrypted_integrity")
    private final EncryptedIntegrity encryptedIntegrity;

}
