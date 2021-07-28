package vn.acgroup.controllers;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import vn.acgroup.controllers.api.AddAddressRequest;
import vn.acgroup.entities.Address;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.UserRepository;

@RestController
public class AddressController {

  @Autowired UserRepository userRepository;
  @Autowired AddressRepository addressRepository;

  @PostMapping(value = "/user/address")
  @ResponseBody
  @ApiOperation(
      value = "add_shipping_address",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity addAddress(@RequestBody AddAddressRequest addAddressRequest)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    User user = optional.get();
    if (addressRepository
        .findByUserAndAddress(user.getId(), addAddressRequest.getAddress())
        .isPresent()) return new ResponseEntity<>("Existed", HttpStatus.FORBIDDEN);
    else {
      if (addressRepository.findByUserAndIsDefault(user.getId(), true).isPresent()) {
        Address addressOld = addressRepository.findByUserAndIsDefault(user.getId(), true).get();
        addressOld.setDefault(false);
      }
      Address address = new Address();
      address.setUser(user.getId());
      address.setName(addAddressRequest.getName());
      address.setMobile(addAddressRequest.getMobile());
      address.setAddress(addAddressRequest.getAddress());
      address.setDefault(true);
      addressRepository.save(address);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    }
  }

  @GetMapping(value = "/user/address")
  @ResponseBody
  @ApiOperation(
      value = "getAllAddress",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity getAllAddress() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(
        addressRepository.findByUser(optional.get().getId()), HttpStatus.OK);
  }

  @GetMapping(value = "/user/getAddressById/{id}")
  @ResponseBody
  @ApiOperation(
      value = "get_address_by_id",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity get_address_by_id(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    Optional<User> user = userRepository.findById(id);
    if (!user.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    else
      return new ResponseEntity<>(
          addressRepository.findByUserAndIsDefault(user.get().getId(), true), HttpStatus.OK);
  }

  @GetMapping(value = "/user/address/set_default/{id}")
  @ResponseBody
  @ApiOperation(
      value = "set_default_address",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity set_default(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      if (!optional.isPresent())
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
      User user = optional.get();

      try {
        Address address = addressRepository.findByUserAndIsDefault(user.getId(), true).get();
        address.setDefault(false);
      } catch (Exception e) {
      }
      Address address = addressRepository.findById(id).get();
      address.setDefault(true);
      addressRepository.save(address);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping(value = "/user/address/{id}")
  @ResponseBody
  @ApiOperation(
      value = "delete_address",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity delete_address(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      if (!optional.isPresent())
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
      User user = optional.get();
      addressRepository.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PutMapping(value = "/user/address/{id}")
  @ResponseBody
  @ApiOperation(
      value = "edit_address",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity edit_address(
      @PathVariable long id, @RequestBody AddAddressRequest addAddressRequest)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    User user = optional.get();
    try {
      Address address = addressRepository.findById(id).get();
      if (address.getUser() != user.getId())
        return new ResponseEntity<>(
            "Address " + id + " not belong to user " + user.getId(), HttpStatus.NOT_FOUND);
      address.setName(addAddressRequest.getName());
      address.setMobile(addAddressRequest.getMobile());
      address.setAddress(addAddressRequest.getAddress());
      addressRepository.save(address);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error " + e.getMessage(), HttpStatus.OK);
    }
  }
}
