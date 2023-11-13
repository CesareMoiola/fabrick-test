package com.moiola.fabricktest.bank_account;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.io.Serializable;
import java.util.List;

@Data
public class FabrickResponse implements Serializable {

    private String status;
    private List<String> error;
    private JsonNode payload;

    public FabrickResponse(){}

    public FabrickResponse(String status, List<String> error, JsonNode payload) {
        this.status = status;
        this.error = error;
        this.payload = payload;
    }


    public boolean isThereAnError(){
        return status.equals(HttpStatus.OK.toString());
    }

    @Override
    public String toString() {
        return "FabrickResponse{" +
                "status='" + status +
                ", error=" + error +
                ", payload='" + payload +
                '}';
    }
}
