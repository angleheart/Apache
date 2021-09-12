package Apache.http;

import Apache.objects.Part;

import java.util.ArrayList;
import java.util.List;

public class PartResponse {

    private final List<Part> parts;

    public PartResponse(List<Part> parts){
        this.parts = parts;
    }

    public List<Part> getParts(){
        return parts;
    }

}
