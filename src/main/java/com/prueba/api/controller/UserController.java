package com.prueba.api.controller;

import com.prueba.api.modelado.Address;
import com.prueba.api.modelado.User;
import com.prueba.api.util.PasswordUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private List<User> users = new ArrayList<>();

    public UserController() {
        User user1 = new User();
        user1.setId(123);
        user1.setEmail("user1@mail.com");
        user1.setName("user1");
        user1.setPassword(PasswordUtil.hashPasswordSHA1("contrasenhaprueba"));
        user1.setCreated_at(LocalDateTime.now());
        user1.setAddresses(new ArrayList<>());

        Address address1 = new Address();
        address1.setId(1);
        address1.setName("workaddress");
        address1.setStreet("street No. 1");
        address1.setCountryCode("UK");

        Address address2 = new Address();
        address2.setId(2);
        address2.setName("homeaddress");
        address2.setStreet("street No. 2");
        address2.setCountryCode("AU");

        user1.getAddresses().add(address1);
        user1.getAddresses().add(address2);

        users.add(user1);
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String sortedBy) {
        if (sortedBy == null || sortedBy.isEmpty()) {
            return users;
        }

        return ((Stream<User>) users.stream()
                .sorted((user1, user2) -> {
                    switch (sortedBy) {
                        case "email":
                            return user1.getEmail().compareToIgnoreCase(user2.getEmail());
                        case "id":
                            return Integer.compare(user1.getId(), user2.getId());
                        case "name":
                            return user1.getName().compareToIgnoreCase(user2.getName());
                        case "created_at":
                            if (user1.getCreated_at() == null) return 1;
                            if (user2.getCreated_at() == null) return -1;
                            return user1.getCreated_at().compareTo(user2.getCreated_at());
                        default:
                            return 0;
                    }
                }))
        		.collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/addresses")
    public List<Address> getUserAddresses(@PathVariable int userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .map(User::getAddresses)
                .orElse(new ArrayList<>());
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        newUser.setPassword(PasswordUtil.hashPasswordSHA1(newUser.getPassword()));     
        newUser.setCreated_at(LocalDateTime.now());    
        users.add(newUser);  
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        for (User user : users) {
            if (user.getId() == id) {
                user.setName(updatedUser.getName() != null ? updatedUser.getName() : user.getName());
                user.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : user.getEmail());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    user.setPassword(PasswordUtil.hashPasswordSHA1(updatedUser.getPassword()));
                }

                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        boolean removed = users.removeIf(user -> user.getId() == id);

        if (removed) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Address> updateUserAddress(
            @PathVariable int userId,
            @PathVariable int addressId,
            @RequestBody Address updatedAddress) {

        User user = users.stream() 
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Address address = user.getAddresses().stream()
                .filter(a -> a.getId() == addressId)
                .findFirst()
                .orElse(null);

        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        address.setName(updatedAddress.getName() != null ? updatedAddress.getName() : address.getName());
        address.setStreet(updatedAddress.getStreet() != null ? updatedAddress.getStreet() : address.getStreet());
        address.setCountryCode(updatedAddress.getCountryCode() != null ? updatedAddress.getCountryCode() : address.getCountryCode());

        return ResponseEntity.ok(address);
    }
}


