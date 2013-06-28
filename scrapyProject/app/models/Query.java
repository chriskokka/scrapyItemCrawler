package models;


public class Query {
    public String manufacturer;
    public String price;
    public String category;
    public String description;
    public String link;
    public String[] moreInfoList;
    public int number;

    public Query(){
    }

    public Query(String manufacturer, String price,String category,String description,String link,String[] moreInfoList,int number){
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
        this.description = description;
        this.link = link;
        this.moreInfoList = moreInfoList;
        this.number = number;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String[] getMoreInfoList() {
        return moreInfoList;
    }

    public void setMoreInfoList(String[] moreInfoList) {
        this.moreInfoList = moreInfoList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
