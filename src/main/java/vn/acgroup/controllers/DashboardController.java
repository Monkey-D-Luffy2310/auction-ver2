package vn.acgroup.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.acgroup.repositories.UserRepository;

@Controller
@CrossOrigin(origins = "*")
public class DashboardController {

  @Autowired UserRepository userRepository;

  @GetMapping(value = "/user/details/{id}")
  public String details(@PathVariable long id, Model model)
      throws InterruptedException, ExecutionException {

    model.addAttribute("user", userRepository.findById(id).get());
    return "views/user/details";
  }
}
