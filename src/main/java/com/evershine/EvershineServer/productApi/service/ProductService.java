package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.ProductNotFoundException;
import com.evershine.EvershineServer.productApi.dto.*;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FilterService filterService;

    @Transactional
    public ProductLightResponseDto addProduct(ProductRequestDto productRequestDto){
        if(productRepository.existsBySlug(productRequestDto.getSlug()))
            throw new DuplicateFilterException("Slug already exist with name: "+productRequestDto.getSlug());

        Product product = new Product();
        product = addFilters(product, productRequestDto);
        product = productRepository.save(product);

        return toProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductLightResponseDto> getAllProducts(){
        return productRepository.findAllFull()
                .stream()
                .map(this::toProductResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductLightResponseDto getProductById(UUID id){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product does not exist with id: "+id));
        return toProductResponseDto(product);
    }

    // while update don`t send category, type or variant id
    // since all of them gone ignore, because no one can update their parents
    @Transactional
    public ProductLightResponseDto updateProductById(UUID id, ProductRequestDto dto){

        Product oldProduct = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product does not exists with id: "+id));

        if(
                oldProduct!=null
                && dto.getSlug()!=null
                && !oldProduct.getSlug().equals(dto.getSlug())
                && productRepository.existsBySlug(dto.getSlug())
        ){
            throw new DuplicateFilterException("Product slug already exist with name: "+dto.getSlug());
        }

        oldProduct = addProductInfo(oldProduct,dto);

        if(dto.getBrandId()!=null)
            oldProduct.setBrand(filterService.getBrand(dto.getBrandId()));

        if(dto.getSubBrandId()!= null)
            oldProduct.setSubBrand(filterService.getSubBrand(dto.getSubBrandId()));

        if(dto.getGradeId()!= null)
            oldProduct.setGrade(filterService.getGrade(dto.getGradeId()));

        if(dto.getSubGradeId()!=null)
            oldProduct.setSubGrade(filterService.getSubGrade(dto.getSubGradeId()));

        oldProduct = productRepository.save(oldProduct);
        return toProductResponseDto(oldProduct);
    }

    @Transactional(readOnly = true)
    public ProductLightResponseDto getProductBySlug(String slug){
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(()->new ProductNotFoundException("Product does not exist with slug: "+slug));
        return toProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductMetaDataDto> getAllMetaProducts() {

        return productRepository.findAllFull()
                .stream()
                .map(this::toProductMetaDtaDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductMetaDataDto> getAllActiveProducts(){
        List<Product> productList = productRepository.findByProductStatus(ProductStatus.ACTIVE);
        return productList.stream()
                .map(this::toProductMetaDtaDto)
                .toList();
    }

    @Transactional
    public ProductLightResponseDto setProductStatus(UUID id,String status){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product does not exists with id: "+id));

        if(status != null && status.equalsIgnoreCase("ACTIVE"))
            product.setProductStatus(ProductStatus.ACTIVE);
        else
            product.setProductStatus(ProductStatus.UNAVAILABLE);

        return toProductResponseDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProductById(UUID id){
        if(productRepository.existsById(id))
            productRepository.deleteById(id);
        else
            throw new ProductNotFoundException("Product does not exists with id: "+id);
    }

    private ProductMetaDataDto toProductMetaDtaDto(Product p) {

        ProductMetaDataDto dto = new ProductMetaDataDto();

        dto.setId(p.getId());
        dto.setImageUrl(p.getImageUrl());
        dto.setTitle(p.getTitle());
        dto.setSlug(p.getSlug());

        // JSON attribute → DTO
        if(p.getAttributes()!= null)
            dto.setAttributes(
                    p.getAttributes().stream()
                            .map(a -> new AttributeDto(
                                    a.getLabel(),
                                    a.getUnit(),
                                    a.getMin() !=null?a.getMin():null,
                                    a.getMax() !=null? a.getMax():null
                            ))
                            .toList()
            );
        if(p.getCategory()!= null)
            dto.setCategoryId(p.getCategory().getId());
        if((p.getCategoryType()!= null))
            dto.setCategoryTypeId(p.getCategoryType().getId());
        if(p.getCategoryVariant()!= null)
            dto.setCategoryVariantId(p.getCategoryVariant().getId());

        if(p.getBrand()!= null)
            dto.setBrandId(p.getBrand().getId());
        if(p.getSubBrand()!= null)
            dto.setSubBrandId(p.getSubBrand().getId());
        if(p.getGrade()!= null)
            dto.setGradeId(p.getGrade().getId());
        if(p.getSubGrade()!= null)
            dto.setSubGradeId(p.getSubGrade().getId());

        return dto;
    }

    private ProductLightResponseDto toProductResponseDto(Product product) {
        ProductLightResponseDto dto = new ProductLightResponseDto();

        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setSlug(product.getSlug());
        dto.setImageUrl(product.getImageUrl());
        dto.setProductStatus(product.getProductStatus());

        if(product.getAttributes()!=null)
            dto.setFullAttributeResponseDtos(getAttributeDtoList(product));

        dto.setOverviewHtml(product.getOverviewHtml());

        if(product.getPropertyTables()!= null)
            dto.setPropertyRequestDtoList(getPropertyRequestDto(product));

        // Safely extract IDs using null checks
        if (product.getCategory() != null) dto.setCategoryId(product.getCategory().getId());
        if (product.getCategoryType() != null) dto.setCategoryTypeId(product.getCategoryType().getId());
        if (product.getCategoryVariant() != null) dto.setCategoryVariantId(product.getCategoryVariant().getId());

        if (product.getBrand() != null) dto.setBrandId(product.getBrand().getId());
        if (product.getSubBrand() != null) dto.setSubBrandId(product.getSubBrand().getId());

        if (product.getGrade() != null) dto.setGradeId(product.getGrade().getId());
        if (product.getSubGrade() != null) dto.setSubGradeId(product.getSubGrade().getId());

        return dto;
    }

    private List<PropertyRequestDto> getPropertyRequestDto(Product product) {
        List<PropertyRequestDto> propertyRequestDtoList = new ArrayList<>();
        for(PropertyTable table: product.getPropertyTables()){
            PropertyRequestDto dto = new PropertyRequestDto();
            dto.setHeading(table.getHeading());
            dto.setTableOfContent(table.getTableOfContent());
            propertyRequestDtoList.add(dto);
        }
        return propertyRequestDtoList;
    }

    private List<FullAttributeResponseDto> getAttributeDtoList(Product product) {
        List<FullAttributeResponseDto> attributeDtoList = new ArrayList<>();

        for(Attribute attribute : product.getAttributes()){
            FullAttributeResponseDto dto = new FullAttributeResponseDto();
            dto.setLabel(attribute.getLabel());
            dto.setUnit(attribute.getUnit());
            if(attribute.getMax() != null && attribute.getMin()!=null){
                dto.setMin(attribute.getMin());
                dto.setMax(attribute.getMax());
            }
            dto.setValues(attribute.getValues());
            attributeDtoList.add(dto);
        }
        return attributeDtoList;
    }

    private Product addFilters(Product product, ProductRequestDto productRequestDto) {

        if(productRequestDto.getCategoryId()!=null)
            product.setCategory(filterService.getCategory(productRequestDto.getCategoryId()));
        if(productRequestDto.getCategoryTypeId()!=null)
            product.setCategoryType(filterService.getCategoryType(productRequestDto.getCategoryTypeId()));
        if (productRequestDto.getCategoryVariantId()!=null)
            product.setCategoryVariant(filterService.getCategoryVariant(productRequestDto.getCategoryVariantId()));

        if(productRequestDto.getBrandId()!=null)
            product.setBrand(filterService.getBrand(productRequestDto.getBrandId()));
        if(productRequestDto.getSubBrandId()!= null)
            product.setSubBrand(filterService.getSubBrand(productRequestDto.getSubBrandId()));
        if(productRequestDto.getGradeId()!= null)
            product.setGrade(filterService.getGrade(productRequestDto.getGradeId()));
        if(productRequestDto.getSubGradeId()!=null)
            product.setSubGrade(filterService.getSubGrade(productRequestDto.getSubGradeId()));

        product = addProductInfo(product, productRequestDto);
        return product;
    }

    private Product addProductInfo(Product product,ProductRequestDto productRequestDto){
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setOverviewHtml(productRequestDto.getOverviewHtml());
        product.setPropertyTables(productRequestDto.getPropertyTables());
        product.setSlug(productRequestDto.getSlug());
        product.setTitle(productRequestDto.getTitle());

        return addAttributes(product, productRequestDto);

    }

    private Product addAttributes(Product product, ProductRequestDto productRequestDto) {
        List<Attribute> attributeList = new ArrayList<>();
        for(AttributeRequestDto dto: productRequestDto.getAttributeRequestDtos()){
            Attribute attribute = new Attribute();
            attribute.setMax(dto.getMax());
            attribute.setMin(dto.getMin());
            attribute.setUnit(dto.getUnit());
            attribute.setValues(dto.getValues());
            attribute.setCustomInput(dto.getCustomInput());
            attribute.setLabel(dto.getLabel());
            attributeList.add(attribute);
        }
        product.setAttributes(attributeList);
        return product;
    }
}
