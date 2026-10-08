package com.evershine.EvershineServer.productApi.service;

import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.DuplicateFilterException;
import com.evershine.EvershineServer.productApi.ProductApiExceptionHandler.FilterOptionNotFoundException;
import com.evershine.EvershineServer.productApi.dto.FilterResponseDto;
import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Grade;
import com.evershine.EvershineServer.productApi.entity.Product;
import com.evershine.EvershineServer.productApi.repository.GradeRepository;
import com.evershine.EvershineServer.productApi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public FilterResponseDto addGrade(String name){
        if(gradeRepository.existsByName(name)){
            throw new DuplicateFilterException("Grade name already exist with name: "+name);
        }

        Grade grade = new Grade();
        grade.setActive(true);
        grade.setName(name);
        gradeRepository.save(grade);
        return toFilterResponseDto(grade);
    }

    @Transactional(readOnly = true)
    public FilterResponseDto getGrade(UUID id){
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Grade does not exists with id: "+id)
                );
        // for admin can see deactivated as well as
        return toFilterResponseDto(grade);
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllGrade(){
        return gradeRepository.findAll()
                .stream()
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FilterResponseDto> getAllActiveGrades(){
        return gradeRepository.findAll()
                .stream()
                .filter(Grade::getActive)
                .map(this::toFilterResponseDto)
                .toList();
    }

    @Transactional
    public String deleteGrade(UUID id){
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Grade does not exists with id: "+id)
                );
        grade.setActive(false);
        List<Product> products = productRepository
                .findByGradeId(id)
                .stream()
                .peek(product -> product.setProductStatus(ProductStatus.UNAVAILABLE))
                .toList();
        productRepository.saveAll(products);
        gradeRepository.save(grade);

        return "Grade "+grade.getName()+" deactivated successfully and affected "+products.size()+" products";
    }

    @Transactional
    public FilterResponseDto updateGrade(String name, UUID id){
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(()->
                        new FilterOptionNotFoundException("Grade does not exists with id: "+id)
                );

        // if xx already in db, and new update with same yy to xx so check new name as well as
        // that new name does not exist

        if(!grade.getName().equals(name) && gradeRepository.existsByName(name)) {
            throw new DuplicateFilterException("Grade name already exist with name: "+name);
        }

        grade.setName(name);
        grade = gradeRepository.save(grade);
        return toFilterResponseDto(grade);
    }

    private FilterResponseDto toFilterResponseDto(Grade grade){
        return new FilterResponseDto(
                grade.getId(),
                grade.getName(),
                grade.getActive()
        );
    }
}
