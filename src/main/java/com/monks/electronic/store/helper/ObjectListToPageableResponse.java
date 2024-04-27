package com.monks.electronic.store.helper;

import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.services.impl.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;

public class ObjectListToPageableResponse {

    public  static <U,V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type) {
        List<U> entities = page.getContent();
        List<V> dtoList = entities.stream().map(object -> new ModelMapper().map(object,type)).toList();
        PageableResponse<V> response= new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLastPage(page.isLast());
        return response;
    }
}
