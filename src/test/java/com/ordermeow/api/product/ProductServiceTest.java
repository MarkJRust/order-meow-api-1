package com.ordermeow.api.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String PRODUCT_NAME = "GARBAGE";
    private static final String PRODUCT_DESCRIPTION = "DESCRIPTION";
    private static final long PRODUCT_ID = 1;
    private static final String FILE_NAME = "File Name.png";
    private static final String IMAGE_TYPE = MediaType.IMAGE_JPEG_VALUE;
    private static final byte[] IMAGE_BYTES = new byte[]{0x01, 0x02, 0x03};

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void createProduct_happyPath() throws IOException {
        ProductEntity expected = new ProductEntity();
        MultipartFile file = mock(MultipartFile.class);

        expected.setProductName(PRODUCT_NAME);
        expected.setProductDescription(PRODUCT_DESCRIPTION);
        expected.setProductPrice(BigDecimal.valueOf(1.00));
        expected.setFileName(FILE_NAME);
        expected.setFileType(IMAGE_TYPE);
        expected.setProductImage(IMAGE_BYTES);

        when(file.getOriginalFilename()).thenReturn(FILE_NAME);
        when(file.getContentType()).thenReturn(IMAGE_TYPE);
        when(file.getBytes()).thenReturn(IMAGE_BYTES);
        when(productRepository.save(expected)).thenReturn(expected);

        ProductEntity actual = productService.createProduct(expected, file);

        Assertions.assertEquals(expected.getProductName(), actual.getProductName());
        Assertions.assertEquals(expected.getFileName(), actual.getFileName());
        Assertions.assertEquals(expected.getFileType(), actual.getFileType());
        Assertions.assertEquals(expected.getProductDescription(), actual.getProductDescription());
        Assertions.assertEquals(expected.getProductImage(), actual.getProductImage());
        Assertions.assertEquals(expected.getProductPrice(), actual.getProductPrice());
        verify(productRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void createProduct_nullNameThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(null);

        Assertions.assertThrows(ProductExceptions.BadProductName.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_emptyStringNameThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName("");

        Assertions.assertThrows(ProductExceptions.BadProductName.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_nullDescriptionThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription(null);
        Assertions.assertThrows(ProductExceptions.BadProductDescription.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_emptyDescriptionThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription("");

        Assertions.assertThrows(ProductExceptions.BadProductDescription.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_nullPriceThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription(PRODUCT_DESCRIPTION);
        badProduct.setProductPrice(null);
        Assertions.assertThrows(ProductExceptions.BadProductPrice.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_priceEqualsZeroThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription(PRODUCT_DESCRIPTION);
        badProduct.setProductPrice(BigDecimal.ZERO);
        Assertions.assertThrows(ProductExceptions.BadProductPrice.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_priceLessThanZeroThrowsException() {
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription(PRODUCT_DESCRIPTION);
        badProduct.setProductPrice(BigDecimal.valueOf(-1));
        Assertions.assertThrows(ProductExceptions.BadProductPrice.class, () -> productService.createProduct(badProduct, null));
    }

    @Test
    void createProduct_badFileNameThrowsException() {
        MultipartFile file = mock(MultipartFile.class);
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription(PRODUCT_DESCRIPTION);
        badProduct.setProductPrice(BigDecimal.valueOf(1.00));
        when(file.getOriginalFilename()).thenReturn("..");
        Assertions.assertThrows(ProductExceptions.FileNameInvalid.class, () -> productService.createProduct(badProduct, file));
    }

    @Test
    void createProduct_ioExceptionReadingFileBytes() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        ProductEntity badProduct = new ProductEntity();
        badProduct.setProductName(PRODUCT_NAME);
        badProduct.setProductDescription(PRODUCT_DESCRIPTION);
        badProduct.setProductPrice(BigDecimal.valueOf(1.00));
        when(file.getOriginalFilename()).thenReturn(FILE_NAME);
        when(file.getContentType()).thenReturn(IMAGE_TYPE);
        when(file.getBytes()).thenThrow(new IOException());

        Assertions.assertThrows(ProductExceptions.InvalidFileException.class, () -> productService.createProduct(badProduct, file));
    }

    @Test
    void getProduct_happyPath() {
        ProductEntity expected = new ProductEntity();
        expected.setProductId(PRODUCT_ID);
        expected.setProductName(PRODUCT_NAME);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(expected));
        ProductEntity actual = productService.getProduct(PRODUCT_ID);

        Assertions.assertEquals(expected.getProductName(), actual.getProductName());
    }

    @Test
    void getProduct_notFound() {
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
