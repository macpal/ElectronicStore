package com.monks.electronic.store.services;

import com.monks.electronic.store.dtos.CategoryDTO;
import com.monks.electronic.store.dtos.PageableResponse;

public interface CategoryService {
    //create
    CategoryDTO create(CategoryDTO categoryDTO);

    //update
    CategoryDTO update(CategoryDTO categoryDTO, String categoryId);

    //delete
    void delete(String categoryId);

    //get all
    PageableResponse<CategoryDTO> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get single category detail
    CategoryDTO get(String categoryId);

    //search
}
