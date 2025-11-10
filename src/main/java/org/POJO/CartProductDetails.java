package org.POJO;

public class CartProductDetails {
    private String productId;
    private String cartId;
    private int quantity;

    public CartProductDetails(String productId, String cartId, int quantity) {
        this.productId = productId;
        this.cartId = cartId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getCartId() {
        return cartId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductDetails{" +
                "productId='" + productId + '\'' +
                ", cartId='" + cartId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
