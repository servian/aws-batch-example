package model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CustomerResult {


    String reviewId;
    String sentimentResult;

    @Override
    public String toString(){

        return reviewId+","+sentimentResult;
    }


}
