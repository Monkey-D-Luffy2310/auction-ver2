package vn.acgroup.controllers;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.controllers.api.FeedbackRequest;
import vn.acgroup.entities.Feedback;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.FeedbackRepository;
import vn.acgroup.repositories.UserRepository;

@RestController
public class FeedbackController {

  @Autowired UserRepository userRepository;

  @Autowired FeedbackRepository feedbackRepository;

  @Autowired AuctionRepository auctionRepository;

  @PostMapping(value = "/feedback/create")
  @ResponseBody
  @ApiOperation(
      value = "feedback/create",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> feedback(@RequestBody FeedbackRequest request)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    User user = optional.get();

    if (!auctionRepository.findById(request.getAuction()).isPresent()) {
      return new ResponseEntity<>("Auction not found", HttpStatus.NOT_FOUND);
    }

    Feedback feedback = new Feedback(request, user.getId());
    feedbackRepository.save(feedback);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @GetMapping(value = "/feedback/all")
  @ResponseBody
  @ApiOperation(
      value = "feedback/all",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> allFeedback() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    // User user = optional.get();
    return new ResponseEntity<>(feedbackRepository.findAllOrderByCreatedLimit(1000), HttpStatus.OK);
  }

  @GetMapping(value = "/feedback/detail/{id}")
  @ResponseBody
  @ApiOperation(
      value = "feedback/detail",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> feedbackDetail(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    if (!feedbackRepository.findById(id).isPresent()) {
      return new ResponseEntity<>("Feedback not found", HttpStatus.NOT_FOUND);
    }
    // User user = optional.get();
    return new ResponseEntity<>(feedbackRepository.findById(id), HttpStatus.OK);
  }

  @GetMapping(value = "/feedback/user/{id}")
  @ResponseBody
  @ApiOperation(
      value = "feedback/user",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> allFeedbackByUser(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    // User user = optional.get();
    return new ResponseEntity<>(feedbackRepository.findByUserOrderByCreatedDesc(id), HttpStatus.OK);
  }

  @GetMapping(value = "/feedback/auction/{id}")
  @ResponseBody
  @ApiOperation(
      value = "feedback/auction",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> allFeedbackByAuction(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    // User user = optional.get();
    return new ResponseEntity<>(
        feedbackRepository.findByAuctionOrderByCreatedDesc(id), HttpStatus.OK);
  }

  @PostMapping(value = "/feedback/update/{id}")
  @ResponseBody
  @ApiOperation(
      value = "feedback/auction",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> updateFeedback(
      @PathVariable long id, @RequestBody FeedbackRequest request)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    // User user = optional.get();
    Optional<Feedback> feedbackOpt = feedbackRepository.findById(id);
    if (!feedbackOpt.isPresent()) {
      return new ResponseEntity<>("feedback not found", HttpStatus.NOT_FOUND);
    }
    Feedback feedback = feedbackOpt.get();
    feedback.setStatus(request.getStatus());
    feedbackRepository.save(feedback);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }
}
