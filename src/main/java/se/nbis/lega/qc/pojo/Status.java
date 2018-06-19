package se.nbis.lega.qc.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Data
public class Status {

    @SerializedName("state")
    private final String state;

    @SerializedName("details")
    private final String details;

}
