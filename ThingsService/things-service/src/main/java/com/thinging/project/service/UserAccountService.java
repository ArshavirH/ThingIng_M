package com.thinging.project.service;

import com.thinging.project.response.UserAccountDto;
import com.thinging.project.dto.UserAccountResDto;
import com.thinging.project.entity.UserAccount;
import com.thinging.project.repository.UserAccountRepository;
import com.thinging.project.utils.parser.DataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserAccountService {

    private UserAccountRepository userRepository;
    private DataParser dataParser;

    public UserAccountService(UserAccountRepository userRepository, DataParser dataParser) {
        this.userRepository = userRepository;
        this.dataParser = dataParser;
    }

    public ResponseEntity<String> createUser(UserAccountDto userReq) {

        if(userRepository.existsByEmail(userReq.getEmail()))
            return new ResponseEntity<>("User Already exists", HttpStatus.OK);

        UserAccount newUser = dataParser.dtoToUserAccount(userReq,null);
        userRepository.save(newUser);
        return new ResponseEntity<>("User Successfully Created!", HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateUser(UserAccountDto userReq, Long id) {
        Optional<UserAccount> byId = userRepository.findById(id);
        if (byId.get() != null) {
            UserAccount updated = dataParser.dtoToUserAccount(userReq,byId.get());
            userRepository.save(updated);
            return new ResponseEntity<>("User Successfully Updated!", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("User Does Not Exist", HttpStatus.NOT_FOUND);
    }


    public ResponseEntity<String> deleteUser(Long id) {
        Optional<UserAccount> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            userRepository.delete(byId.get());
            return new ResponseEntity<>("User Successfully Deleted!", HttpStatus.OK);
        }
        return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getUserById(Long id) {
        Optional<UserAccount> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            UserAccountResDto userResDto = new UserAccountResDto(byId.get());
            return new ResponseEntity<>(userResDto, HttpStatus.OK);
        }
        return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<UserAccountDto> getUserByEmail(String email) {
        Optional<UserAccount> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            UserAccountDto userResDto = dataParser.userAccountToDto(byEmail.get());
            return new ResponseEntity<>(userResDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getUserList() {
        List<UserAccount> userList = userRepository.findAll();
        ArrayList<UserAccountResDto> list = userList.stream()
                .map(UserAccountResDto::new)
                .collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


}
