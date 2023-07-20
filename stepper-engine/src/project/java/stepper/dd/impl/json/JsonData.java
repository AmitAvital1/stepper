package project.java.stepper.dd.impl.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonData {
    private JsonObject jsonObject;

    public JsonData(String json) throws JsonSyntaxException {
        jsonObject = JsonParser.parseString(json).getAsJsonObject();
    }
    @Override
    public String toString(){
        if(jsonObject != null){
            return jsonObject.toString();
        }
        else
            return "Invalid Json";
    }
}
