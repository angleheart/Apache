package Apache.workstation.pos;

import Apache.objects.Transferable;

public class SpecialFunction implements Transferable {

    String name;

    SpecialFunction(String name){
        this.name = name;
    }

    @Override
    public String getSelectableName() {
        return name;
    }
}
