package account.web;

import account.model.User;
import account.model.UserData;
import account.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("signup")
  public UserData signup(@Valid @RequestBody User user) {
    LOGGER.info("{}", user);
    return this.authenticationService.signup(user);
  }

  @PostMapping("changepass")
  public ResponseEntity<?> changePass(@RequestBody Map<String, String> body) {
    var response = this.authenticationService.changePass(body);
    return ResponseEntity.ok(response);
  }




}
