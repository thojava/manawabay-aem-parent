package nz.co.manawabay.core.pojos.crm;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class CustomerInterest {
    private String interestId;
    private String name;
    private String status;
}
