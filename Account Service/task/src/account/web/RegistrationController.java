package account.web;

import account.model.User;
import account.model.UserData;
import account.services.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class RegistrationController {

  private final RegistrationService registrationService;

  private final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping("signup")
  public UserData signup(@Valid @RequestBody User user) {
    LOGGER.info("{}", user);
    return this.registrationService.signup(user);
  }

  @PostMapping("changepass")
  public ResponseEntity<?> changePass(@RequestBody Map<String, String> body) {
    var response = this.registrationService.changePass(body);
    return ResponseEntity.ok(response);
  }




}
