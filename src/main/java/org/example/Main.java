package org.example;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException {

        //Создание файла 1
        String[] columnMapping = {"id", "firstName",
                "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json,"data1.json");

        //Создание файла 2
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2,"data2.json");

        //Создание файла 3
        String json3 = readString("new_data.json");
        List<Employee> list3 = jsonToList(json3);
        System.out.println(list3.toString());
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list,listType);
        return json;
    }

    public static void writeString(String json, String nameOfFile){
        try (FileWriter fileWriter = new FileWriter(nameOfFile)) {
              fileWriter.write(json);
              fileWriter.flush();
        } catch (IOException e) {
        }
    }

    public static List<Employee> parseXML(String filename) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filename));

        List<Employee> returnList = new LinkedList<>();

        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);

            if (Node.ELEMENT_NODE == node_.getNodeType()) {

                NodeList nodeListIn = node_.getChildNodes();
                List<String> list = new LinkedList<>();

                for (int j = 0; j < nodeListIn.getLength(); j++) {
                    Node node2_ = nodeListIn.item(j);
                    if (Node.ELEMENT_NODE == node2_.getNodeType()) {
                        Element element = (Element) node2_;
                        list.add(element.getTextContent());
                    }
                }

                Employee employee = new Employee(
                    Long.parseLong(list.get(0)),
                    list.get(1),
                    list.get(2),
                    list.get(3),
                    Integer.parseInt(list.get(4))
                    );

                returnList.add(employee);
            }
        }
        return returnList;
    }

    public static String readString(String nameOfFile) {
        String line = "Ошибка";
        try (BufferedReader reader = new BufferedReader(new FileReader(nameOfFile))) {
             line = reader.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static List<Employee> jsonToList(String json) throws ParseException {

        Gson gson = new Gson();
        JSONParser parser = new JSONParser();

        Object object = parser.parse(json);
        JSONArray jsonArray = (JSONArray) object;

        List<Employee> listEmployee = new LinkedList<>();
        for(int i=0; i < jsonArray.size();i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

            // FFF... Зачем тут "String.valueOf" я 2 дня убил на поиск ошибки ( ????
            Employee employee = gson.fromJson(String.valueOf(jsonObject), Employee.class);
            listEmployee.add(employee);
        }
        return listEmployee;
    }
}
