package Apache.workstation.pos;

import Apache.config.Config;
import Apache.http.Transfer;
import Apache.objects.CounterPerson;
import Apache.objects.Customer;
import Apache.database.CounterPersonBase;
import Apache.database.CustomerBase;
import Apache.workstation.SceneController;
import Apache.objects.Transferable;
import Apache.util.InputVerifier;
import Apache.util.TextFieldModifier;
import Apache.http.Gateway;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Header {

    private static TextField CUSTOMER_NUMBER_FIELD;
    private static TextField COUNTER_PERSON_NUMBER_FIELD;
    private static TextField PO_FIELD;
    private static TextField TRANS_CODE_FIELD;
    private static Label CUSTOMER_NAME_LABEL;
    private static Label CUSTOMER_ADDRESS_LABEL;
    private static Label CUSTOMER_ADDRESS_LABEL2;
    private static Label CUSTOMER_PHONE_LABEL;
    private static Label COUNTER_PERSON_NAME_LABEL;
    private static Label VEHICLE_DESCRIPTION_LABEL;
    private static Label SHIP_TO_LABEL;

    private static int focusIndex = 0;

    public static boolean customerLocked;

    public static void initiate(
            TextField customerNumberField,
            TextField counterPersonField,
            TextField poField,
            TextField transCodeField,
            Label customerNameLabel,
            Label customerAddressLabel,
            Label customerAddressLabel2,
            Label customerPhoneLabel,
            Label counterPersonNameLabel,
            Label vehicleDescriptionLabel,
            Label shipToLabel
    ) {
        CUSTOMER_NUMBER_FIELD = customerNumberField;
        COUNTER_PERSON_NUMBER_FIELD = counterPersonField;
        PO_FIELD = poField;
        TRANS_CODE_FIELD = transCodeField;
        CUSTOMER_NAME_LABEL = customerNameLabel;
        CUSTOMER_ADDRESS_LABEL = customerAddressLabel;
        CUSTOMER_ADDRESS_LABEL2 = customerAddressLabel2;
        CUSTOMER_PHONE_LABEL = customerPhoneLabel;
        COUNTER_PERSON_NAME_LABEL = counterPersonNameLabel;
        VEHICLE_DESCRIPTION_LABEL = vehicleDescriptionLabel;
        SHIP_TO_LABEL = shipToLabel;
        TextFieldModifier.requireCaps(CUSTOMER_NUMBER_FIELD);
        TextFieldModifier.requireCaps(COUNTER_PERSON_NUMBER_FIELD);
        TextFieldModifier.requireCaps(PO_FIELD);
        TextFieldModifier.requireCaps(TRANS_CODE_FIELD);
    }

    public static void refocus() {
        switch (focusIndex) {
            case 0 -> CUSTOMER_NUMBER_FIELD.requestFocus();
            case 1 -> COUNTER_PERSON_NUMBER_FIELD.requestFocus();
            case 2 -> PO_FIELD.requestFocus();
            case 3 -> TRANS_CODE_FIELD.requestFocus();
        }
    }

    static void enterFromLineBody() {
        focusPoField();
    }

    static void focusPoField() {
        focusIndex = 2;
        PO_FIELD.requestFocus();
    }

    static void focusCounterPersonField() {
        focusIndex = 1;
        COUNTER_PERSON_NUMBER_FIELD.requestFocus();
    }

    static void focusCustomerNumberField() {
        focusIndex = 0;
        CUSTOMER_NUMBER_FIELD.requestFocus();
    }


    /**
     * Move Request Methods
     * <p>
     * These methods are used to handle requests
     * to navigate around the header
     */

    public static void applyShipTo(String shipTo) {
        if (InputVerifier.verifyShipTo(shipTo)) {
            Error.clear();
            ActiveSequence.SHIP_TO = shipTo;
            Header.SHIP_TO_LABEL.setText(shipTo);
            return;
        }
        Error.send("Invalid ShipTo description");
    }

    public static void applyVehicleDescription(String description) {
        VEHICLE_DESCRIPTION_LABEL.setTextFill(Color.BLACK);
        ActiveSequence.VEHICLE_DESCRIPTION = description;
        Header.VEHICLE_DESCRIPTION_LABEL.setText(description);
        if (LineBody.isFocusedHere())
            LineBody.reFocus();
        else
            Header.refocus();
    }

    static void requestMoveCustomerNumberField(KeyCode code) {

        if (code == KeyCode.DOWN || code == KeyCode.ENTER) {
            String customerRequest = CUSTOMER_NUMBER_FIELD.getText();
            // Handle requests from customer field
            if (customerRequest.equalsIgnoreCase("END")) {
                ActiveSequence.startNew();
                return;
            }

            if (customerRequest.equalsIgnoreCase("1.3")) {
                CUSTOMER_NUMBER_FIELD.clear();
                InputMenu.performRequest(InputMenuType.INVOICE_VIEW);
                return;
            }

            if (customerRequest.equalsIgnoreCase("5.6")) {
                CUSTOMER_NUMBER_FIELD.clear();
                SceneController.setToPaymentManager();
                ActiveSequence.startNew();
                return;
            }

            if (!InputVerifier.verifyCustomerRequest(customerRequest)) {
                Error.send("Invalid customer entry");
                CUSTOMER_NUMBER_FIELD.clear();
                CUSTOMER_NUMBER_FIELD.requestFocus();
                return;
            }

            if (Config.STAND_ALONE) {
                Customer customer = CustomerBase.getCustomerByNumber(customerRequest);
                if (customer == null) {
                    List<Transferable> customerList = CustomerBase.getCustomersByName(customerRequest);
                    if (customerList.size() == 0) {
                        Error.send("No customers found");
                        Header.resetCustomerInfo();
                        CUSTOMER_NUMBER_FIELD.clear();
                        focusCustomerNumberField();
                        return;
                    }
                    if (customerList.size() == 1) {
                        Error.clear();
                        ActiveSequence.CUSTOMER = (Customer) customerList.get(0);
                        loadCustomer();
                        focusCounterPersonField();
                        return;
                    }
                    SelectionMenu.performRequest(SelectionMenuType.CUSTOMER, customerList);
                } else {
                    Error.clear();
                    ActiveSequence.CUSTOMER = customer;
                    loadCustomer();
                    focusCounterPersonField();
                }
            } else {
                if (customerRequest.equals(""))
                    customerRequest = "*";
                List<Customer> customers = Gateway.queryCustomers(customerRequest);
                if (customers == null) {
                    Error.send("Sorry, a server error occurred");
                    CUSTOMER_NUMBER_FIELD.clear();
                    CUSTOMER_NUMBER_FIELD.requestFocus();
                    return;
                }

                switch (customers.size()) {
                    case 0 -> {
                        Error.send("Customer not found");
                        Header.resetCustomerInfo();
                        CUSTOMER_NUMBER_FIELD.clear();
                        focusCustomerNumberField();
                    }
                    case 1 -> {
                        Error.clear();
                        ActiveSequence.CUSTOMER = customers.get(0);
                        loadCustomer();
                        focusCounterPersonField();
                    }
                    default -> {
                        SelectionMenu.performRequest(SelectionMenuType.CUSTOMER, new ArrayList<>(customers));
                    }
                }
            }
        }
    }

    static void requestMoveCounterPersonField(KeyCode code) {
        if (code == KeyCode.UP) {
            if (customerLocked) {
                ActiveSequence.startNew();
                return;
            }
            Error.clear();
            focusCustomerNumberField();
        }

        if (code == KeyCode.DOWN || code == KeyCode.ENTER) {
            CounterPerson counterPerson;
            String query = COUNTER_PERSON_NUMBER_FIELD.getText();
            if(!InputVerifier.verifyCounterPersonNumber(query)){
                Error.send("Invalid counter person number");
                COUNTER_PERSON_NUMBER_FIELD.clear();
                focusCounterPersonField();
                return;
            }

            if (Config.STAND_ALONE) {
                counterPerson = CounterPersonBase.getCounterPersonByNumber(query);
                if (counterPerson == null) {
                    Error.send("Unknown counter person");
                    COUNTER_PERSON_NUMBER_FIELD.clear();
                    focusCounterPersonField();
                    return;
                }
            } else {
                List<Object> counterPeople = Gateway.queryCounterPeople(query);
                if(counterPeople == null){
                    Error.send("A server error occurred");
                    COUNTER_PERSON_NUMBER_FIELD.clear();
                    focusCounterPersonField();
                    return;
                }else if(counterPeople.size() < 1){
                    Error.send("Unknown counter person");
                    COUNTER_PERSON_NUMBER_FIELD.clear();
                    focusCounterPersonField();
                    return;
                }else{
                    counterPerson = (CounterPerson) counterPeople.get(0);
                }
            }

            ActiveSequence.COUNTER_PERSON = counterPerson;
            loadCounterPerson();
            Error.clear();
            if (code == KeyCode.DOWN)
                focusPoField();
            else {
                ActiveSequence.TRANS_CODE = "SAL";
                loadTransCode();
                LineBody.requestDown();
            }
        }
    }

    static void requestMovePoField(KeyCode code) {
        if (code == KeyCode.DOWN || code == KeyCode.ENTER || code == KeyCode.UP) {
            String po = PO_FIELD.getText();
            if (InputVerifier.verifyPO(po)) {
                ActiveSequence.PURCHASE_ORDER = po;
                switch (code) {
                    case DOWN, ENTER -> LineBody.requestDown();
                    case UP -> focusCounterPersonField();
                }
                return;
            }
            Error.send("Invalid po");
            focusPoField();
        }
    }


    /**
     * Utility Methods
     * <p>
     * General utility methods for sequence handling
     */

    static void reset() {
        focusIndex = 0;
        customerLocked = false;
        resetCustomerInfo();
        CUSTOMER_NUMBER_FIELD.clear();
        COUNTER_PERSON_NUMBER_FIELD.clear();
        PO_FIELD.clear();
        TRANS_CODE_FIELD.clear();
        COUNTER_PERSON_NAME_LABEL.setText("");
        VEHICLE_DESCRIPTION_LABEL.setTextFill(Color.RED);
        VEHICLE_DESCRIPTION_LABEL.setText("NO VEHICLE SPECIFIED");
        SHIP_TO_LABEL.setText("");
    }

    static void resetCustomerInfo() {
        CUSTOMER_NAME_LABEL.setText("WELCOME TO APACHE AUTO PARTS");
        CUSTOMER_ADDRESS_LABEL.setText("");
        CUSTOMER_ADDRESS_LABEL2.setText("THANK YOU FOR");
        CUSTOMER_PHONE_LABEL.setText("YOUR BUSINESS");
    }

    static void loadCustomer() {
        CUSTOMER_NUMBER_FIELD.setText(ActiveSequence.CUSTOMER.getNumber());
        CUSTOMER_NAME_LABEL.setText(ActiveSequence.CUSTOMER.getName());
        CUSTOMER_ADDRESS_LABEL.setText(ActiveSequence.CUSTOMER.getAddress());
        CUSTOMER_ADDRESS_LABEL2.setText(ActiveSequence.CUSTOMER.getCityStateZip());
        CUSTOMER_PHONE_LABEL.setText(ActiveSequence.CUSTOMER.getPhone());
    }

    static void loadCounterPerson() {
        COUNTER_PERSON_NUMBER_FIELD.setText(Integer.toString(ActiveSequence.COUNTER_PERSON.getNumber()));
        COUNTER_PERSON_NAME_LABEL.setText(ActiveSequence.COUNTER_PERSON.getName());
    }

    static void loadTransCode() {
        TRANS_CODE_FIELD.setText(ActiveSequence.TRANS_CODE);
    }

    static void loadAll() {
        loadCustomer();
        loadCounterPerson();
        loadTransCode();
        PO_FIELD.setText(ActiveSequence.PURCHASE_ORDER);
        VEHICLE_DESCRIPTION_LABEL.setText(ActiveSequence.VEHICLE_DESCRIPTION);
        if (ActiveSequence.VEHICLE_DESCRIPTION.equals("")) {
            VEHICLE_DESCRIPTION_LABEL.setTextFill(Color.RED);
            VEHICLE_DESCRIPTION_LABEL.setText("NO VEHICLE SPECIFIED");
        } else {
            VEHICLE_DESCRIPTION_LABEL.setTextFill(Color.BLACK);
        }

        SHIP_TO_LABEL.setText(ActiveSequence.SHIP_TO);
    }


}
