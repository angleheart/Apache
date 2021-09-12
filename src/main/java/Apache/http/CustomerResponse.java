package Apache.http;

import Apache.objects.Customer;

import java.util.List;

public class CustomerResponse {

    private final List<Customer> customers;

    public CustomerResponse(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

}
