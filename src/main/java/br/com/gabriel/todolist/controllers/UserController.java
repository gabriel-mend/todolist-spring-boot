package br.com.gabriel.todolist.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.gabriel.todolist.repositories.IUserRepository;
import br.com.gabriel.todolist.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @PostMapping("/")
    public ResponseEntity create (@RequestBody UserModel userModel) {
        try {
            var user = this.userRepository.findByUsername(userModel.getUsername());

            if(user != null) {
                System.out.println("Usuário já existente!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existente!");
            }

            var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

            userModel.setPassword(passwordHashred);

            var useCreated = this.userRepository.save(userModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(useCreated);
        } catch (Exception error) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
