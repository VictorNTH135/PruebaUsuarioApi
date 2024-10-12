package com.prueba.api.controller;

import com.prueba.api.modelado.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {
	
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void testGetUsersWithoutSorting() {
        List<User> users = userController.getUsers(null);
        assertNotNull(users);
        assertEquals(1, users.size()); 
    }

    @Test
    public void testGetUsersSortedByEmail() {
        List<User> sortedUsers = userController.getUsers("email");
        assertNotNull(sortedUsers);
    }
    
    @Test
    public void testGetUsersSortedById() {
        List<User> sortedUsers = userController.getUsers("id");
        assertNotNull(sortedUsers);
    }
    
    @Test
    public void testGetUsersSortedByName() {
        List<User> sortedUsers = userController.getUsers("name");
        assertNotNull(sortedUsers);
    }
    
    public void testGetUsersSortedByCreatedAt() {
        List<User> sortedUsers = userController.getUsers("created_at");
        assertNotNull(sortedUsers);
    }
    
    @Test
    public void testGetUserByIdExists() { 	
        ResponseEntity<User> response = userController.getUserById(123);
        assertNotNull(response.getBody());
        assertEquals(123, response.getBody().getId());
        assertEquals(HttpStatus.OK, response.getStatusCode()); 
    }

    @Test
    public void testGetUserByIdNotFound() {
        ResponseEntity<User> response = userController.getUserById(999);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); 
    }
    
    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setId(456); 
        newUser.setName("newUser");
        newUser.setEmail("newuser@mail.com");
        newUser.setPassword("newpassword");

        ResponseEntity<User> response = userController.createUser(newUser);

        assertNotNull(response.getBody());
        assertEquals(456, response.getBody().getId()); 
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<User> users = userController.getUsers(null);
        assertEquals(2, users.size());
    }
    
    @Test
    public void testUpdateUser() {
        User existingUser = userController.getUserById(123).getBody();
        String originalPassword = existingUser.getPassword();

        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updatedemail@mail.com");

        ResponseEntity<User> response = userController.updateUser(123, updatedUser);

        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName()); 
        assertEquals("updatedemail@mail.com", response.getBody().getEmail()); 
        assertEquals(123, response.getBody().getId()); 
        assertEquals(originalPassword, response.getBody().getPassword()); 
    }


    @Test
    public void testUpdateUserNotFound() {
        User updatedUser = new User();
        updatedUser.setName("Non-Existent Name");

        ResponseEntity<User> response = userController.updateUser(999, updatedUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testDeleteUser() {
        ResponseEntity<Void> response = userController.deleteUser(123);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<User> getUserResponse = userController.getUserById(123);
        assertEquals(HttpStatus.NOT_FOUND, getUserResponse.getStatusCode());
    }

    @Test
    public void testDeleteUserNotFound() {
        ResponseEntity<Void> response = userController.deleteUser(999);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


}

