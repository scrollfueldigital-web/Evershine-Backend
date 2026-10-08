package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.FilterOptionNotFoundException;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Brand;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.entity.SubBrand;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.productApi.repository.SubBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubBrandService {
    private final SubBrandRepository subBrandRepository;
    private final ProductRepository productRepository;

    @Transactional
    public FilterResponseDto addSubBrand(String brandName){
        if(subBrandRepository.existsByName(brandName)){
            throw new DuplicateFilterException("Sub-Brand name already exist with name: "+brandName);
        }

        SubBrand subBrand = new SubBrand();
        subBrand.setActive(true);
        subBrand.setName(brandName);
        subBrandRepository.save(subBrand);
        return toFilterResponseDto(subBrand);
    }

    @Transactional(readOnly = true)
    public FilterResponseDto getSubBrand(UUID id){
        SubBrand brand = subBrandRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Sub-brand does not exists with id: "+id)
                );
        // for admin can see deactivated as well as
        return toFilterResponseDto(brand);
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllSubBrands(){
        return subBrandRepository.findAll()
                .stream()
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllActiveSubBrands(){
        return subBrandRepository.findAll()
                .stream()
                .filter(SubBrand::getActive)
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional
    public String deleteSubBrand(UUID id){
        SubBrand brand = subBrandRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Sub-Brand does not exists with id: "+id)
                );
        brand.setActive(false);
        List<Product> products = productRepository
                .findBySubBrandId(id)
                .stream()
                .peek(product -> product.setProductStatus(ProductStatus.UNAVAILABLE))
                .toList();
        productRepository.saveAll(products);
        subBrandRepository.save(brand);

        return "SubBrand "+brand.getName()+" deactivated successfully and affected "+products.size()+" products";
    }

    @Transactional
    public FilterResponseDto updateBrand(String name, UUID id){
        SubBrand brand = subBrandRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("SubBrand does not exists with id: "+id)
                );

        // if xx already in db, and new update with same yy to xx so check new name as well as
        // that new name does not exist

        if(!brand.getName().equals(name) && subBrandRepository.existsByName(name)) {
            throw new DuplicateFilterException("SubBrand name already exist with name: "+name);
        }

        brand.setName(name);
        brand = subBrandRepository.save(brand);
        return toFilterResponseDto(brand);
    }

    private FilterResponseDto toFilterResponseDto(SubBrand subBrand) {
        return new FilterResponseDto(
                subBrand.getId(),
                subBrand.getName(),
                subBrand.getActive()
        );
    }
}
