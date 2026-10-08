package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.FilterOptionNotFoundException;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.entity.SubGrade;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import com.evershine.EvershineServer.productApi.repository.SubGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class SubGradeService {

    private final SubGradeRepository subGradeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public FilterResponseDto addSubGrade(String name){
        if(subGradeRepository.existsByName(name)){
            throw new DuplicateFilterException("sub-Grade name already exist with name: "+name);
        }

        SubGrade subGrade = new SubGrade();
        subGrade.setActive(true);
        subGrade.setName(name);
        subGradeRepository.save(subGrade);
        return toFilterResponseDto(subGrade);
    }

    @Transactional(readOnly = true)
    public FilterResponseDto getSubGrade(UUID id){
        SubGrade subGrade = subGradeRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("SubGrade does not exists with id: "+id)
                );
        // for admin can see deactivated as well as
        return toFilterResponseDto(subGrade);
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllSubGrade(){
        return subGradeRepository.findAll()
                .stream()
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllActiveSubGrades(){
        return subGradeRepository.findAll()
                .stream()
                .filter(SubGrade::getActive)
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional
    public String deleteSubGrade(UUID id){
        SubGrade subGrade = subGradeRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("SubGrade does not exists with id: "+id)
                );
        subGrade.setActive(false);
        List<Product> products = productRepository
                .findBySubGradeId(id)
                .stream()
                .peek(product -> product.setProductStatus(ProductStatus.UNAVAILABLE))
                .toList();
        productRepository.saveAll(products);
        subGradeRepository.save(subGrade);

        return "SubGrade "+subGrade.getName()+" deactivated successfully and affected "+products.size()+" products";
    }

    @Transactional
    public FilterResponseDto updateSubGrade(String name, UUID id){
        SubGrade subGrade = subGradeRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("SubGrade does not exists with id: "+id)
                );

        // if xx already in db, and new update with same yy to xx so check new name as well as
        // that new name does not exist

        if(!subGrade.getName().equals(name) && subGradeRepository.existsByName(name)) {
            throw new DuplicateFilterException("SubGrade name already exist with name: "+name);
        }

        subGrade.setName(name);
        subGrade = subGradeRepository.save(subGrade);
        return toFilterResponseDto(subGrade);
    }

    private FilterResponseDto toFilterResponseDto(SubGrade subGrade){
        return new FilterResponseDto(
                subGrade.getId(),
                subGrade.getName(),
                subGrade.getActive()
        );
    }
}
