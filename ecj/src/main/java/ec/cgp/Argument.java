package ec.cgp;

public class Argument {
    public Object input = null;
    public Boolean isLeaf = false;

    Argument(Object input, Boolean isLeaf){
        this.input=input;
        this.isLeaf=isLeaf;
    }
}
