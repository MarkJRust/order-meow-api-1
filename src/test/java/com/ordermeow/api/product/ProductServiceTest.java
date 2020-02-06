package com.ordermeow.api.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String PRODUCT_NAME = "GARBAGE";
    private static final long PRODUCT_ID = 1;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void createAlbum_happyPath() {
        ProductEntity expected = new ProductEntity();
        expected.setProductName(PRODUCT_NAME);

        when(productRepository.save(expected)).thenReturn(expected);

        ProductEntity actual = productService.createProduct(expected);

        Assertions.assertEquals(expected.getProductName(), actual.getProductName());
        verify(productRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void createAlbum_nullNameThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(null);

        Assertions.assertThrows(ProductExceptions.BadProductName.class, () -> productService.createProduct(badProduct));
    }

    @Test
    void createAlbum_emptyStringNameThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName("");

        Assertions.assertThrows(ProductExceptions.BadProductName.class, () -> productService.createProduct(badProduct));
    }

    @Test
    void getAlbum_happyPath() {
        ProductEntity expected = new ProductEntity();
        expected.setProductId(PRODUCT_ID);
        expected.setProductName(PRODUCT_NAME);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(expected));
        ProductEntity actual = productService.getProduct(PRODUCT_ID);

        Assertions.assertEquals(expected.getProductName(), actual.getProductName());
    }

    @Test
    void getAlbum_notFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(ProductExceptions.ProductNotFound.class, () -> productService.getProduct(PRODUCT_ID));
    }

    @Test
    void deleteProduct_happyPath() {
        ProductEntity expected = new ProductEntity();
        expected.setProductId(PRODUCT_ID);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(expected));
        doNothing().when(productRepository).deleteById(PRODUCT_ID);

        productService.deleteProductById(PRODUCT_ID);
        verify(productRepository, times(1)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).deleteById(PRODUCT_ID);
    }

    @Test
    void deleteProduct_productIdNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(ProductExceptions.ProductNotFound.class, () -> productService.deleteProductById(PRODUCT_ID));

        verify(productRepository, times(0)).deleteById(PRODUCT_ID);
    }
}
