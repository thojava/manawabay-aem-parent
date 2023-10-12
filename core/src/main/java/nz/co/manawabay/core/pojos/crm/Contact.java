package nz.co.manawabay.core.pojos.crm;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;
import java.util.List;


@Builder
@Getter
public class Contact {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String dateOfBirth;
    private String gender;
    private String initialSignUpSource;
    private Address address;
    private CustomerSubscriptions customerSubscriptions;
    private List<CustomerInterest> customerInterests;
}
