package Apache.http;

import Apache.objects.CounterPerson;

import java.util.List;

public class CounterPersonResponse {

    private final List<CounterPerson> counterPeople;

    public CounterPersonResponse(List<CounterPerson> counterPeople) {
        this.counterPeople = counterPeople;
    }

    public List<CounterPerson> getCounterPeople() {
        return counterPeople;
    }
}
