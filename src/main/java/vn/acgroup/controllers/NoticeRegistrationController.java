package vn.acgroup.controllers;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import vn.acgroup.entities.NoticeRegister;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.NoticeRegisterRepository;
import vn.acgroup.service.mail.MailService;

@Controller
@CrossOrigin(origins = "*")
public class NoticeRegistrationController {

  @Autowired NoticeRegisterRepository noticeRegisterRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;
  @Autowired AuctionRepository auctionRepository;
  @Autowired MailService mailService;

  @GetMapping(value = "/notice_registration/{email}")
  @ResponseBody
  @ApiOperation(value = "/")
  public ResponseEntity<String> noticeRegister(@PathVariable String email)
      throws InterruptedException, ExecutionException {
    try {
      Optional<NoticeRegister> noticeRegister =
          noticeRegisterRepository.findByEmailAndIsVerify(email, true);
      if (noticeRegister.isPresent())
        return new ResponseEntity<String>("Existed", HttpStatus.FORBIDDEN);
      mailService.noticeRegister(email);
      return new ResponseEntity<String>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "getAll")
  @ResponseBody
  @ApiOperation(value = "/noticeRegister/getAll")
  public Iterable<NoticeRegister> getAll() throws InterruptedException, ExecutionException {
    return noticeRegisterRepository.findAll();
  }
}
