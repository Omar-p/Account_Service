package account.web;

import account.model.AdminOperation;
import account.services.AdminService;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("api/admin/")
public class AdminController {


  private final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("user")
  public ResponseEntity<?> getUsers() {
    return ResponseEntity.ok(this.adminService.findAllUsers());
  }

  @DeleteMapping("user/{email}")
  public ResponseEntity<?> delete(@PathVariable("email") String email) {
    return ResponseEntity.ok(this.adminService.deleteUser(email));
  }

  @PutMapping("user/role")
  public ResponseEntity<?> updateRole(@RequestBody AdminOperation adminOperation) {
    LOGGER.info("OP: {}" , adminOperation);
    return ResponseEntity.ok(this.adminService.apply(adminOperation));
  }

  @PutMapping("user/access")
  public ResponseEntity<?> lockOrUnlockUser(@RequestBody AdminOperation operation) {
    LOGGER.info("OP: {}" , operation);
    final Map<String, String> response = this.adminService.lockOrUnlock(operation);
    return ResponseEntity.ok(response);
  }
}
