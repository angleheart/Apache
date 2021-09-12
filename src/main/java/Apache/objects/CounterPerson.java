package Apache.objects;

public class CounterPerson implements Selectable {

    private final int number;
    private final String name;

    public CounterPerson(
            int number,
            String name
    ){
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return number + "\n" + name;
    }

    @Override
    public String getSelectableName() {
        return number + " - " + name;
    }
}
