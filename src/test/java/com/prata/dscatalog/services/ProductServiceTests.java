package com.prata.dscatalog.services;

import com.prata.dscatalog.dto.ProductDTO;
import com.prata.dscatalog.entities.Category;
import com.prata.dscatalog.entities.Product;
import com.prata.dscatalog.repositories.CategoryRepository;
import com.prata.dscatalog.repositories.ProductRepository;
import com.prata.dscatalog.services.exceptions.DatabaseException;
import com.prata.dscatalog.services.exceptions.ResourceNotFoundException;
import com.prata.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependenId;
    private Product product;
    private Category category;
    ProductDTO productDTO;
    private PageImpl<Product> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependenId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProdutDTO();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependenId)).thenReturn(true);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependenId);
    }

    @Test
    public void updateShoulThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, productDTO);

        });

    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShoulThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);

        });

    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findAllPagedShouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 12);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);

        Mockito.verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependenId);
        });

        Mockito.verify(repository, times(1)).deleteById(dependenId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, times(1)).deleteById(existingId);
    }
}
