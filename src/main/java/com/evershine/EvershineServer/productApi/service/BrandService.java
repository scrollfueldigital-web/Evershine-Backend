package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.FilterOptionNotFoundException;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Brand;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.BrandRepository;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final AdminService adminService;

    @Transactional
    public FilterResponseDto addBrand(String brandName){
        if(brandRepository.existsByName(brandName)){
            throw new DuplicateFilterException("Brand name already exist with name: "+brandName);
        }

        Brand brand = new Brand();
        brand.setActive(true);
        brand.setName(brandName);
        brandRepository.save(brand);
        return toFilterResponseDto(brand);
    }

    @Transactional(readOnly = true)
    public FilterResponseDto getBrand(UUID id){
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Brand does not exists with id: "+id)
                );
        // for admin can see deactivated as well as
        return toFilterResponseDto(brand);
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllBrands(){

        return brandRepository.findAll()
                .stream()
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllActiveBrands(){
        return brandRepository.findAll()
                .stream()
                .filter(Brand::getActive)
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional
    public String deleteBrand(UUID id){
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Brand does not exists with id: "+id)
                );
        brand.setActive(false);
        List<Product> products = productRepository
                .findByBrandId(id)
                .stream()
                .peek(product -> product.setProductStatus(ProductStatus.UNAVAILABLE))
                .toList();
        productRepository.saveAll(products);
        brandRepository.save(brand);

        return "Brand "+brand.getName()+" deactivated successfully and affected "+products.size()+" products";
    }

    @Transactional
    public FilterResponseDto updateBrand(String name, UUID id){
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Brand does not exists with id: "+id)
                );

        // if xx already in db, and new update with same yy to xx so check new name as well as
        // that new name does not exist

        if(!brand.getName().equals(name) && brandRepository.existsByName(name)) {
            throw new DuplicateFilterException("Brand name already exist with name: "+name);
        }

        brand.setName(name);
        brand = brandRepository.save(brand);
        return toFilterResponseDto(brand);
    }

    private FilterResponseDto toFilterResponseDto(Brand brand){
        return new FilterResponseDto(
                brand.getId(),
                brand.getName(),
                brand.getActive()
        );
    }
}
