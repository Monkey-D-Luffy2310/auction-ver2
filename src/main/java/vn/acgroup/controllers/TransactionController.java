package vn.acgroup.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import vn.acgroup.repositories.TransactionRepository;
import vn.acgroup.repositories.UserRepository;

@CrossOrigin(origins = "*")
@RestController
public class TransactionController {

  @Autowired UserRepository userRepository;
  @Autowired TransactionRepository transactionRepository;

  Logger log = Logger.getLogger(this.getClass().getName());
}
