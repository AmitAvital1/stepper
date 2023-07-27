package project.java.stepper.dd.impl.json;

import com.google.gson.*;

public class JsonData {
    private JsonElement jsonObject;
    private Gson gson = new Gson();

    public JsonData(String json) throws JsonSyntaxException {
        Object o = gson.fromJson(json, Object.class);
        jsonObject = JsonParser.parseString(json);
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
