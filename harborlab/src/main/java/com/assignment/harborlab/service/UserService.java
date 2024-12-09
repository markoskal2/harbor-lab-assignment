package com.assignment.harborlab.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignment.harborlab.assembler.AcmeAssembler;
import com.assignment.harborlab.dto.UserDTO;
import com.assignment.harborlab.entity.User;
import com.assignment.harborlab.exception.UserExistsException;
import com.assignment.harborlab.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AcmeAssembler acmeAssembler;

    public UserDTO createUser(final String name, final String email) {

        final var userEmail = userRepository.findByEmail(email);
        if (Objects.nonNull(userEmail)) {
					throw new UserExistsException(String.format("A user with an email %s already exists", email));
				}

        final var user = User.builder()
            .name(name)
            .email(email)
            .build();

        final var savedUser = userRepository.save(user);
        return acmeAssembler.assembleUser(savedUser);
    }

    public UserDTO findUserByEmail(final String email) {

        final var userEmail = userRepository.findByEmail(email);
        return acmeAssembler.assembleUser(userEmail);
    }

}
