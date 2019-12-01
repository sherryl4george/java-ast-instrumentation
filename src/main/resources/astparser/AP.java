package astparser;

public class AP {
    String type;
    String name;
    String value;
    public AP(String type, String name, int value){
        this.type = type;
        this.name = name;
        this.value = Integer.toString(value);
    }
    public AP(String type, String name, char value){
        this.type = type;
        this.name = name;
        this.value = Character.toString(value);
    }
    public AP(String type, String name, boolean value){
        this.type = type;
        this.name = name;
        this.value = Boolean.toString(value);
    }
    public AP(String type, String name, double value){
        this.type = type;
        this.name = name;
        this.value = Double.toString(value);
    }
    public AP(String type, String name, Object value){
        this.type = type;
        this.name = name;
        this.value = value.toString();
    }
    public AP(String type, String name, String value){
        this.type = type;
        this.name = name;
        this.value = value.toString();
    }

    @Override
    public String toString() {
        return "type=" + type +
                ", name=" + name +
                ", value=" + value ;
    }
}
