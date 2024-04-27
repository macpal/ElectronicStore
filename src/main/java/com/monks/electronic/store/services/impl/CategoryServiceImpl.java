package com.monks.electronic.store.services.impl;

import com.monks.electronic.store.dtos.CategoryDTO;
import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.entities.Category;
import com.monks.electronic.store.exceptions.ResourceNotFound;
import com.monks.electronic.store.helper.ObjectListToPageableResponse;
import com.monks.electronic.store.repositories.CategoryRepository;
import com.monks.electronic.store.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    /* constructor */
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /*Create category*/
    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(UUID.randomUUID().toString());
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /*Update category*/
    @Override
    public CategoryDTO update(CategoryDTO categoryDTO, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFound("Category not found to update !!"));
        category.setTitle(categoryDTO.getTitle());
        category.setDescription(categoryDTO.getDescription());
        category.setCoverImage(categoryDTO.getCoverImage());
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    /*Delete category*/
    @Override
    public void delete(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFound("Category not found to delete !!"));
        categoryRepository.delete(category);
    }

    /*Get all categories*/
    @Override
    public PageableResponse<CategoryDTO> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        return ObjectListToPageableResponse.getPageableResponse(page, CategoryDTO.class);
    }

    /*Get single category*/
    @Override
    public CategoryDTO get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFound("Category not found !!"));
        return modelMapper.map(category, CategoryDTO.class);
    }
}
