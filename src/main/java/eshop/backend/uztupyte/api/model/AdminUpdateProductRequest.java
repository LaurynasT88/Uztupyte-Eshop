package eshop.backend.uztupyte.api.model;

public class AdminUpdateProductRequest {

    private String name;
    private String shortDescription;
    private String longDescription;
    private Double price;
    private Integer quantity;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getShortDescription() {

        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {

        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {

        return longDescription;
    }

    public void setLongDescription(String longDescription) {

        this.longDescription = longDescription;
    }

    public Double getPrice() {

        return price;
    }

    public void setPrice(Double price) {

        this.price = price;
    }

    public Integer getQuantity() {

        return quantity;
    }

    public void setQuantity(Integer quantity) {

        this.quantity = quantity;
    }
}
