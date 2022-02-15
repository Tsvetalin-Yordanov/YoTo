package com.example.yoto.model.category;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getById(int id) {
        if (id > 0) {
            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isPresent()) {
                return optionalCategory.get();
            }
            throw new NotFoundException("Category not found");
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
