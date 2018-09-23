package gt.controllers;

import gt.entities.User;
import gt.graph.UsersRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @PutMapping(path = "/users/{address}/block")
    public ResponseEntity blockUser(@PathVariable("address") String address) {
        User user = usersRepository.findByAddress(address);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setBlocked(true);
        usersRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/users/{address}/block")
    public ResponseEntity unblockUser(@PathVariable("address") String address) {
        User user = usersRepository.findByAddress(address);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setBlocked(false);
        usersRepository.save(user);

        return ResponseEntity.noContent().build();
    }

}
