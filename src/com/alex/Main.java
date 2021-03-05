package com.alex;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import org.json.*;

public class Main {

    public static void main(String[] args) throws IOException {
        String tags=getTags();
        getInfoFromJson(tags);

        //System.out.println("\n***DONE!");

    }

    private static String getTags() throws IOException {
        System.out.println("\n***please list the tags you are interested in(divided by the comma without spaces):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = reader.readLine();
        return str;
    }

    private static void getInfoFromJson(String tags) {
        String uri = "https://open-api.myhelsinki.fi/v1/events/";//music";
        if(!tags.isEmpty())
        {
            uri+="?tags_filter="+tags;
        }

        GetDataAPI api = new GetDataAPI();
        String jsonString = api.getHTML(uri);
        JSONObject obj = new JSONObject(jsonString);
        JSONArray data = obj.getJSONArray("data");
        int size = data.length();
        int page = 1;
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < size; i++) {

            //for names
            JSONObject jnames = data.getJSONObject(i).getJSONObject("name");
            Iterator<String> keys = jnames.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                text.append(key);
                text.append(';');
                if (!jnames.isNull(key)) {
                    text.append(jnames.getString(key));
                    text.append(';');
                } else {
                    text.append("null;");
                }
            }

            //for address
            JSONObject jaddres = data.getJSONObject(i).getJSONObject("location").getJSONObject("address");
            Iterator<String> addrKeys = jaddres.keys();
            while (addrKeys.hasNext()) {
                String key = addrKeys.next();
                if (jaddres.get(key).toString() != "null") {
                    text.append(jaddres.getString(key));
                    text.append(',');
                } else {
                    text.append("-,");
                }
            }
            text.append('\n');
            if (i>=1000*page)
            {
                saveToCSV(text, page);
                text.setLength(0);
                page++;
            }
        }

        saveToCSV(text, page);
        System.out.print("Number of events = ");
        System.out.println(size);
    }

    private static void saveToCSV(StringBuilder  text, int page)
    {

        PrintWriter pw = null;
        String fileName = "events_list_"+page+".csv";
        System.out.println(fileName);
        try {
            pw = new PrintWriter(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pw.write(text.toString());
        pw.close();
    }
}

class GetDataAPI {

    public String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection connection;
        BufferedReader reader;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
