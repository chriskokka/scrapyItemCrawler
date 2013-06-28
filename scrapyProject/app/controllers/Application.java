package controllers;

import models.NewProduct;
import play.*;
import play.mvc.*;
import play.data.Form;
import models.Query;

import views.html.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Application extends Controller {


	/*
		queryForm - empty form of a Query class.
		productForm - empty form of a MoreInfo class.
		currentCategory, currentManufacturer - used for query verification on templates
		to handle solr request better. (This can be enhanced by sending solr more precise
		queries)
	*/
    public static Form<Query> queryForm = Form.form(Query.class);
    public static Form<NewProduct> productForm = Form.form(NewProduct.class);
    public static String currentCategory = "";
    public static String currentManufacturer = "";

    public static Result index() {
        return ok(index.render());
    }

    public static Result products(){
        return ok(products.render(queryForm));
    }

    public static Result foundProducts(){
        ArrayList<Query> emptyList = new ArrayList<>();
        return ok(foundproducts.render(emptyList, currentCategory, currentManufacturer));
    }

    public static Result addProducts(){
        return ok(addproduct.render(productForm));
    }

	/*
		This is a result handler, that will handle the adding of a new item link.
		It will get the value of the newItem text field from the Form, make a file
		and save it in proper format for Solr and then update solr via: updateSolr().
	*/
    public static Result addProductsPOST(){
        Form<NewProduct> filledProduct = productForm.bindFromRequest();
        String newItem = filledProduct.get().newItem;

        try{
            File file = new File("PATH_TO_SOLR_FILE_DIR");	// The path has to be to the dir where you are going to upload data to solr from.
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }else{
                file.createNewFile();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("PATH_TO_SOLR_FILE_DIR")));
            out.println("<add>");
            out.println("<doc>");
            out.println("<field name=\"Link\">" + newItem + "</field>");
            out.println("</doc>");
            out.println("</add>");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

         updateSolr();

        return redirect(routes.Application.index());
    }

	/*
		This is the result handler, that will direct the user to the
		found products page. It will bind data from the request and
		query Solr accordingly. It uses getDataFromSolr() function
		to accomplish that.
	*/
    public static Result querySolr(){
        Form<Query> filledForm = queryForm.bindFromRequest();
        String lower;
        String upper;
        currentCategory = filledForm.get().category;
        currentManufacturer = filledForm.get().manufacturer;


        String[] tempList = filledForm.get().price.split("-");
        lower = tempList[0];
        upper = tempList[1];

        try{
            File file = new File("scrapyProject\\assets\\solrreq.xml");
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }else{
                file.createNewFile();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("scrapyProject\\assets\\solrreq.xml", true)));
            out.println("<root>");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		//This is the actual query call.
        getDataFromSolr(filledForm.get().manufacturer,lower,upper,filledForm.get().category);

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("scrapyProject\\assets\\solrreq.xml", true)));
            out.println("</root>");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            SolrRequest handler   = new SolrRequest();
            saxParser.parse("scrapyProject\\assets\\solrreq.xml", handler);
            ArrayList<Query> queryList = handler.returnList();

            return ok(foundproducts.render(queryList,currentCategory,currentManufacturer));

        } catch (Throwable err) {
            err.printStackTrace ();
        }
        return redirect(routes.Application.foundProducts());
    }


	/*
		Function sends a query to solr and will save the result after 
		modifying it to /assets/solrreq.xml
	*/
    public static void getDataFromSolr(String manufacturer,String lower,String upper, String category){

        if (category.contains(" ")){
            category = category.replaceAll(" ","%20");
        }
        if(lower.contains(" ")){
            lower = lower.replaceAll(" ","%20");
        }
        if(upper.contains(" ")){
            upper = upper.replaceAll(" ","%20");
        }
        if(manufacturer.contains(" ")){
            manufacturer = manufacturer.replaceAll(" ","%20");
        }

        try {
            URL my_url = new URL("http://localhost:8983/solr/select?q=Category:"+category+"*+Manufacturer:"+manufacturer+"*+Price:["+lower+"TO"+upper+"]*&rows=10&wt=xml&indent=true");
            BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
            String strTemp = "";

            System.out.println(my_url.toString());


            while(null != (strTemp = br.readLine())){

                if(strTemp.equals("<response>")){
                    strTemp = "";
                }else if(strTemp.equals("</response>")){
                    strTemp = "";
                } else if(strTemp.equals("</result>")){
                    strTemp = "";
                }else if(strTemp.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")){
                    strTemp = "";
                }else if(strTemp.contains("<lst name=\"responseHeader\">")){
                    strTemp = "";
                }else if(strTemp.contains("<int name=\"status\">")){
                    strTemp = "";
                }else if(strTemp.contains("<int name=\"QTime\">")){
                    strTemp = "";
                }else if(strTemp.contains("<lst name=\"params\">")){
                    strTemp = "";
                }else if(strTemp.contains("<str name=\"indent\">")){
                    strTemp = "";
                }else if(strTemp.contains("<str name=\"q\">")){
                    strTemp = "";
                }else if(strTemp.contains("<str name=\"wt\">")){
                    strTemp = "";
                }else if(strTemp.contains("<str name=\"rows\">")){
                    strTemp = "";
                }else if(strTemp.contains("</lst>")){
                    strTemp = "";
                }else if(strTemp.contains("<result name=")){
                    strTemp = "";
                }else if(strTemp.contains("<str name=\"start\"")){
                    strTemp = "";
                }

                if(strTemp.contains("name=\"Link\"")){
                    strTemp = strTemp.replaceFirst("<str name=\"Link\">","<Link>");
                    strTemp = strTemp.replaceAll("/str","/Link");
                }
                if(strTemp.contains("name=\"Category\"")){
                    strTemp = strTemp.replaceFirst("<str name=\"Category\">","<Category>");
                    strTemp = strTemp.replaceAll("/str","/Category");
                }
                if(strTemp.contains("name=\"Manufacturer\"")){
                    strTemp = strTemp.replaceFirst("<str name=\"Manufacturer\">","<Manufacturer>");
                    strTemp = strTemp.replaceAll("/str","/Manufacturer");
                }
                if(strTemp.contains("name=\"Price\"")){
                    strTemp = strTemp.replaceFirst("<str name=\"Price\">","<Price>");
                    strTemp = strTemp.replaceAll("/str","/Price");
                }
                if(strTemp.contains("name=\"Description\"")){
                    strTemp = strTemp.replaceFirst("<str name=\"Description\">","<Description>");
                    strTemp = strTemp.replaceAll("/str","/Description");
                }
                if(strTemp.contains("name=\"MoreInfo\"")){
                    strTemp = strTemp.replaceFirst("<str name=\"MoreInfo\">","<MoreInfo>");
                    strTemp = strTemp.replaceAll("/str","/MoreInfo");
                }



                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("scrapyProject\\assets\\solrreq.xml", true)));
                    out.println(strTemp);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

	/*
		This function will execute the command to upload data to Solr.
	*/
    public static void updateSolr(){
        try{
            String[] command = new String[]{"java","-Durl=http://localhost:8983/solr/collection2/update","-jar", "post.jar","solrDataURL.xml"};
            Runtime.getRuntime().exec(command,null, new File("PATH_TO_SOLR_FILE_DIR"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
  
}
