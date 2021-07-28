package vn.acgroup.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.controllers.api.CategoryRequest;
import vn.acgroup.entities.Category;
import vn.acgroup.repositories.CategoryRepository;

@RestController
@CrossOrigin(origins = "*")
public class CategoryController {

  @Autowired CategoryRepository categoryRepository;

  @GetMapping(value = "/category")
  @ResponseBody
  @ApiOperation(
      value = "/category",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<Category> getAll() throws InterruptedException, ExecutionException {
    return categoryRepository.findAll();
  }

  @PostMapping(value = "/category")
  @ResponseBody
  @ApiOperation(
      value = "/category",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity add(@RequestBody CategoryRequest categoryRequest)
      throws InterruptedException, ExecutionException {
    try {
      Category category = new Category();
      category.setName(categoryRequest.getName());
      category.setAlias(categoryRequest.getAlias());
      category.setAvatar(categoryRequest.getAvatar());
      categoryRepository.save(category);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping(value = "/category/{id}")
  @ResponseBody
  @ApiOperation(
      value = "/category",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity delete(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    try {
      categoryRepository.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PutMapping(value = "/edit/category/{id}")
  @ResponseBody
  @ApiOperation(
      value = "/category",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity edit(@PathVariable long id, @RequestBody CategoryRequest categoryRequest)
      throws InterruptedException, ExecutionException {
    try {
      Category category = categoryRepository.findById(id).get();
      category.setName(categoryRequest.getName());
      category.setAlias(categoryRequest.getAlias());
      category.setAvatar(categoryRequest.getAvatar());
      categoryRepository.save(category);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }
}
