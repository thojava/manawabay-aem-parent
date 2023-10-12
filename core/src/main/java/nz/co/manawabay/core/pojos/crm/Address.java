package nz.co.manawabay.core.pojos.crm;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class Address {
    private String country;
    private String city;
    private String postcode;
}
