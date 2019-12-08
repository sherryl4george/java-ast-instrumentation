package astparser;

import org.json.JSONObject;

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
        this.value = value == null ? "null" : value.toString();
    }
    public AP(String type, String name, String value){
        this.type = type;
        this.name = name;
        this.value = value == null ? "null" : value.toString();
    }

    @Override
    public String toString() {
        return "type=" + type +
                ", name=" + name +
                ", value=" + value ;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("name", name);
        jsonObject.put("value", value);
        return jsonObject;
    }
}
