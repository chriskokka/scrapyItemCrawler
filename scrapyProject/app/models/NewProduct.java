package models;


public class NewProduct {
    public String newItem;


    public NewProduct(){
    }

    public NewProduct(String newItem){
        this.newItem = newItem;
    }

    public String getnewItem() {
        return newItem;
    }

    public void setnewItem(String newItem) {
        this.newItem = newItem;
    }
}
