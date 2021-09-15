package Apache.workstation.pos;

import Apache.objects.Selectable;

public class SpecialFunction implements Selectable {

    String name;

    SpecialFunction(String name){
        this.name = name;
    }

    @Override
    public String getSelectableName() {
        return name;
    }
}
