package Apache.http;

import Apache.objects.Transferable;

import java.util.ArrayList;
import java.util.List;

public class Transfer {

    private final List<Object> contents;

    public Transfer(Object content){
        contents = new ArrayList<>();
        contents.add(content);
    }

    public Transfer(List<Object> contents) {
        this.contents = contents;
    }

    public List<Object> getContents() {
        return contents;
    }

    public Object getFirstContent() {
        return contents.size() > 0 ? contents.get(0) : null;
    }

}
