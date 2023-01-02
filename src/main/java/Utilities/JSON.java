package Utilities;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Getter @Setter
public class JSON {

    private Utilities utilities = new Utilities();

    private boolean keepNull = false;

    public static void main(String[] arr)throws Exception{
        JSON json = new JSON();
        //String payload = "{\"country\":\"ca\",\"ip\":\"67.69.76.135\",\"mmp_fields\":{\"mmp_user_id\":\"a7e80493b18a67bb1d63f2674391fe4e\",\"advertiser_iap_event_name\":\"undefined\",\"app_version\":\"2.3.2\",\"tz\":\"UTC-0400\",\"package_id\":\"com.adjust.insights\",\"imp_d\":\"c6085bb6-c49a-11eb-abfe-05c533e05567:1\",\"mcc\":\"302\",\"event_timestamp\":\"1622745475\",\"is_viewthrough\":\"0\",\"app_store\":\"google\",\"osv\":\"11\",\"event_type\":\"install\",\"mnc\":\"610\",\"click_timestamp\":\"1622745461\",\"sess_cnt\":\"1\",\"network_name\":\"samsung dsp test 2021 may\",\"is_attributed\":\"1\",\"trk\":\"1\",\"device_os\":\"android\",\"device_make\":\"Samsung\",\"is_organic\":\"0\",\"install_timestamp\":\"1622745475\",\"cr_group\":\"a170060\",\"matched\":\"device_tag\",\"advertiser_app_open_event_name\":\"undefined\",\"device\":\"GalaxyA70\"},\"datacenter\":\"ams\",\"optout_reason\":\"gdpr\",\"creative_id\":170060,\"flight_id\":224532,\"mmp_name\":\"adjust\",\"optout\":true,\"protocol\":\"https\",\"hostname\":\"ams-delivery-1\",\"google_ifa\":\"735c4dfe-e226-4a91-90ff-66486f6f812b\",\"event\":\"mmp\",\"campaign_id\":72848,\"timestamp\":1.622745506235313E9}";
        //System.out.println(json.getJSONValue(payload, "mmp_fields.tz"));

        Utilities utilities = new Utilities();
        String data = "{" + utilities.readFileFromLocal("./test.data/Prod.Data. 272073.Process.txt") + "}";
        ArrayList<String> keys = json.getJSONValue(data,"advertiser_spend_pacing.mode");
        //System.out.println(data);
        keys.forEach(key ->{
            System.out.println(key);
        });
    }

    /**
     *
     * @param JSONDoc
     * @param term
     * @return
     * @throws Exception
     */
    public ArrayList<String> getJSONValue(final String JSONDoc, final String term)throws Exception{
        ArrayList<String> results = new ArrayList<String>();

        try {
            if(this.iSJSON(JSONDoc)) {
                final String[] terms = term.split("\\.");
                final String[] arr_result_top = this.funcGetValue_JSON_S(JSONDoc, terms).split(",");
                for(String result:arr_result_top) {
                    results.add(trimHeadingAndTailingQuotes(result.replace("#_#", ":").replace(";", ",").replace("##",";")));
                }
            }else {
                results.add("null");
            }

        }catch(Exception e) {
            results.add("null");
            System.out.println("Something went wrong. Please check");
            e.printStackTrace();
        }

        if(results.size() == 1 && results.get(0).equalsIgnoreCase("null") && !keepNull) {
            results.clear();
        }
        return results;
    }


    public ArrayList<String> getKeysFromJSONObject(final String JSONDoc)throws Exception {

        ArrayList<String> Keys = new ArrayList<String>();

        try {
            if(this.iSJSON(JSONDoc)) {
                JSONObject objJSON = new JSONObject(JSONDoc);
                for(String s_Key: objJSON.keySet()) {
                    Keys.add(s_Key);
                }
            }else {
                //Do nothing
            }

        }catch(Exception e) {
            System.out.println("Something went wrong when it tried to fetch JSON keys");
            e.printStackTrace();
        }
        return Keys;
    }

    /**
     * Export a pretty JSON String by GSON
     * @param input
     * @return
     */
    public String exportPrettyJSONByGSON(final String input){
        JsonObject objJSON = new Gson().fromJson(input, JsonObject.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String prettyJson = gson.toJson(objJSON);
        return prettyJson;
    }

    /**
     *
     * @param JSONDoc
     * @param terms
     * @return
     * @throws Exception
     */
    private String funcGetValue_JSON_S(final String JSONDoc, final String[] terms)throws Exception {
        StringBuffer result = new StringBuffer("");
        try {
            if(terms.length > 0) {
                String topTerm = terms[0];
                if(terms.length == 1) {
                    /*
                     * last layer, get value directly
                     */
                    result.append(this.getValueJSONNODEOneLayer(JSONDoc, topTerm) + ",");
                }else {
                    ArrayList<Object> objects = this.getObjects(JSONDoc, topTerm);
                    String[] subTerms = Arrays.copyOfRange(terms, 1, terms.length);
                    if(objects.size() > 0) {
                        for(int i=0; i<objects.size(); i++) {
                            Object objJSON = objects.get(i);
                            result.append(this.funcGetValue_JSON_S(objJSON.toString(), subTerms)+ ",");
                        }
                    }else {
                        /*
                         * When there isn't any object returned,
                         * it means the keyword doesn't exist in the JSON doc
                         * or has empty data
                         */
                    }

                }

            }else {
                /*
                 * If the term array is empty, do nothing.
                 */
            }

        }catch(Exception e) {
            e.printStackTrace();
            result = new StringBuffer("null");
        }

        String finalResult = utilities.trimLastComma(result.toString(),",").trim();
        return (finalResult.length() >0)?finalResult:"null";
    }

    /*
     * Get node value from JSON: one layer
     */

    private String getValueJSONNODEOneLayer(final String JSONDoc, final String termName) {
        StringBuffer result = new StringBuffer("");
        //JSONObject objJSON = new JSONObject(JSONDoc);
        if(JSONDoc != null && !JSONDoc.trim().equalsIgnoreCase("null")){
            JsonObject objJSON = new Gson().fromJson(JSONDoc, JsonObject.class);
            try{
                if(this.iSNodeExist(objJSON, termName)){
                    Object tempObject = objJSON.get(termName);
                    /*
                     * If it is a JSON Array, parse it as an array
                     * If not, parse it as a straight object value
                     */
                    if(tempObject instanceof JsonArray) {
                        JsonArray JSONArrayData = (JsonArray)tempObject;
                        if(JSONArrayData.size() > 0) {
                            for(int i=0; i<JSONArrayData.size(); i++) {
                                Object tempObj = JSONArrayData.get(i);
                                //JsonObject objData = new Gson().fromJson(tempObj.toString(), JsonObject.class);
                                result.append(tempObj.toString().
                                        replace(";","##").replace(",", ";").replace(":", "#_#").trim() + ",");
                            }
                        }else{

                        }
                    }else{
                        /*
                         * If it is not an array, since it is only one layer, just fetch value.
                         */
                        result.append(this.getValueFromJSONNODE(objJSON, termName));
                    }

                }else {

                }
            }catch(Exception e) {
                e.printStackTrace();

            }
        }

        String finalResult = utilities.trimLastComma(result.toString(),",").trim();
        return (finalResult.length() >0)?finalResult:"null";
    }



    /*
     * Get node value from JSON
     */
    private String getValueFromJSONNODE(final JsonObject objInput, final String termName){
        String result = "";

        try{
            if(this.iSNodeExist(objInput, termName)){
                if(objInput.get(termName).isJsonObject()){
                    result = objInput.get(termName).getAsJsonObject().toString();
                }else{
                    result = objInput.get(termName).isJsonNull()?"null":objInput.get(termName).getAsString();
                }

                /*
                int type = getJSONKeyValueType(objInput,termName);
                switch(type){
                    case(1):{
                        result = objInput.get(termName).getAsString();
                        break;
                    }
                    case(2):{
                        result = String.valueOf(objInput.get(termName).getAsInt());
                        break;
                    }
                    case(3):{
                        result = String.valueOf(objInput.get(termName).getAsDouble());
                        break;
                    }
                    case(4):{
                        result = String.valueOf(objInput.get(termName).getAsBoolean());
                        break;
                    }
                    case(5):{
                        result = String.valueOf(objInput.get(termName).getAsLong());
                        break;
                    }
                    case(6):{
                        result = objInput.get(termName).toString();
                        break;
                    }
                    default:{
                        result = objInput.get(termName).getAsString();
                        break;
                    }
                }
                 */
            }else{
                result = "null";
            }

        }catch(JSONException je){
            System.out.println(je.toString());
        }
        result = result.replace(";","##").replace(",", ";").replace(":", "#_#").trim();

        if(result.length() <= 0){
            result = "null";
        }
        return result.trim();
    }


    private ArrayList<Object> getObjects(String JSONDoc, String term){
        ArrayList<Object> objResults = new ArrayList<Object>();
        //JSONObject objJSON = new JSONObject(JSONDoc);
        JsonObject objJSON = new Gson().fromJson(JSONDoc, JsonObject.class);
        try {
            if(this.iSNodeExist(objJSON, term)){
                Object objTemp = objJSON.get(term);
                /*
                 * If the object is a JSONArray
                 */
                if(objTemp instanceof JsonArray) {
                    JsonArray JSONObj = (JsonArray)objTemp;
                    if(JSONObj.size() > 0){
                        for(int i=0; i<JSONObj.size();i++) {
                            Object objSecondLayer = JSONObj.get(i);
                            objResults.add(objSecondLayer);
                        }
                    }
                }
                /*
                 * If the object is not a JSONArray
                 */
                else{
                    objResults.add(objTemp);
                }

            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return objResults;
    }

    private int getJSONKeyValueType(JsonObject objInput, String keyName){
        int result = 0;
        Object oKeyNode = objInput.get(keyName);
        if(oKeyNode instanceof String){
            result = 1;
        }else if(oKeyNode instanceof Integer){
            result = 2;
        }else if(oKeyNode instanceof Double){
            result = 3;
        }else if(oKeyNode instanceof Boolean){
            result = 4;
        }else if(oKeyNode instanceof Long){
            result = 5;
        }else if(oKeyNode instanceof JsonObject){
            result = 6;
        }
        return result;
    }

    private boolean iSNodeExist(JsonObject objInput, String termName){
        boolean result = true;
        if(!objInput.has(termName)){
            result = false;
        }
        return result;
    }

    private boolean iSJSON(String JSONDoc) {
        boolean iSJSON = false;
        try {
            JSONObject objJSON = new JSONObject(JSONDoc);
            if(objJSON instanceof JSONObject) {
                iSJSON = true;
            }

        }catch(Exception e) {
            System.out.println("The input is not a JSON file");
            System.out.println("Input: " + JSONDoc);
        }
        return iSJSON;
    }

    private String trimHeadingAndTailingQuotes(final String input){
        String result = input;
        if(result.indexOf("\"") == 0){
            result = result.substring(1,result.length()-1);
        }
        if(result.indexOf("\"") == result.length() - 1){
            result.substring(0,result.length() - 2);
        }
        return result;
    }
}
