package Utilities;

import javax.net.ssl.*;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Utilities {

    public String trimLastComma(String input, final String character){
        if(input.length() > 0){
            if(input.substring(input.length()-1,input.length()).equalsIgnoreCase(character)){
                input = input.substring(0,input.length()-1);
            }
        }
        return input;
    }

    public String readFileFromLocal(final String filePath){
        StringBuffer dataFromFile = new StringBuffer("");
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null){
                dataFromFile.append(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return dataFromFile.toString();
    }

    public String returnDateFromUnixTimeStamp(final String timeStamp){
        java.util.Date time = new java.util.Date(Long.parseLong(timeStamp)*1000);
        return String.valueOf(time);
    }

    public boolean isNumeric(final String input) {
        return input.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public void saveDataToFile(final String data, final String fileName)throws IOException{
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(data);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getTimeStampString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime = new Date();
        return dateFormat.format(dateTime).toString();
    }

    public String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        return dateFormat.format(date).toString();
    }

    public String getCurrentHour(){
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Date date= new Date();
        return dateFormat.format(date).toString();
    }

    public String getPastXDate(final int days){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        Date date = cal.getTime();
        return dateFormat.format(date).toString();
    }

    public String getFutureXDate(final int days){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +days);
        Date date = cal.getTime();
        return dateFormat.format(date).toString();
    }

    public ArrayList<String> removeDuplicatesFromList(final ArrayList<String> dataList){
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();
        int count = 0;
        if(dataList.size() >0){
            results.add(dataList.get(0));
            for(String data: dataList.subList(1, dataList.size())){
                boolean isDuplicated = false;
                for(String existData: results){
                    if(data.equalsIgnoreCase(existData)){
                        isDuplicated = true;
                        break;
                    }
                }
                if(!isDuplicated){
                    results.add(data);
                }else{
                    count +=1;
                    duplicates.add(data);
                }
            }
        }
        System.out.println(duplicates);

        return results;
    }

    public boolean FIX_HTTPS_SSL_CERTIFICATES_VALIDATION_FAILURE() throws Exception{
        /*
         *  fix for
         *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
         *       sun.security.validator.ValidatorException:
         *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
         *               unable to find valid certification path to requested target
         */
        boolean b_result = false;
        try{
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        //public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            /*
             * end of the fix
             */
            b_result = true;
        }catch(Exception e){
            e.printStackTrace();
            b_result = false;
        }
        return b_result;
    }


    public String getGQLPayloadFromFile(final String filePath)throws Exception{
        return this.readFileFromLocal(filePath).replace("\"", "\\\"");
    }

    public String capitalizeFirstLetter(final String input){
        return input.substring(0,1).toUpperCase() + input.substring(1);
    }

    public ArrayList<String> filterDuplicates(final ArrayList<String> inputs, final boolean iSCaseSensitive){
        Map<String, String> tempData = new TreeMap<String, String>();
        int count = 0;
        inputs.forEach((term)->{
            final String key = (iSCaseSensitive ? term : term.toLowerCase());
            if(!tempData.containsKey(key)){
                tempData.put(key, term);
            }
        });
        return new ArrayList<>(tempData.values());
    }

    public String removeLastCharacter(final String input){
        return input.substring(0, input.length()-1);
    }

}
