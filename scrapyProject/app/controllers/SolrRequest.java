package controllers;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import models.Query;


public class  SolrRequest extends DefaultHandler {
    private Query query;
    private String temp;
    private ArrayList<Query> queryList = new ArrayList<>();
    private ArrayList<String> moreInfoList = new ArrayList<>();
    private String[] tempList;
    private int counter = 1;

    public void characters(char[] buffer, int start, int length) {
        temp = new String(buffer, start, length);
    }

    /*
        When the parser encounters a start element it comes here.
     */
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {
        temp = "";

        if (qName.equalsIgnoreCase("doc")) {
            query = new Query();
            query.number = counter;
            counter += 1;
        }

    }

    /*
        When the parser encounters an end element it comes here.
        Then it checks what kind of element it was to set the correct
        field.(Manufacturer, Description, Price, Link, Category, MoreInfo)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        try{
            if (qName.equalsIgnoreCase("doc")) {
                queryList.add(query);
            } else if (qName.equalsIgnoreCase("Manufacturer")) {
                query.setManufacturer(temp);
            } else if (qName.equalsIgnoreCase("Description")) {
                query.setDescription(temp);
            } else if (qName.equalsIgnoreCase("Price")) {
                query.setPrice(temp);
            }else if (qName.equalsIgnoreCase("Link")) {
                query.setLink(temp);
            }else if (qName.equalsIgnoreCase("Category")) {
                query.setCategory(temp);
            }else if (qName.equalsIgnoreCase("MoreInfo")) {
                tempList = temp.split("\\+");
                query.setMoreInfoList(tempList);
            }
        }catch(NullPointerException e){
        }


    }

    /*
        Returns the list of queries parsed from the SOLR requests.
     */
    public ArrayList returnList() {
        return queryList;
    }

}