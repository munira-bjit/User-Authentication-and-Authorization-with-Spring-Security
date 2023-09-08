package com.spring.securityPractice.service.impl;

import com.spring.securityPractice.constants.AppConstants;
import com.spring.securityPractice.entity.UserEntity;
import com.spring.securityPractice.model.UserDto;
import com.spring.securityPractice.repository.UserRepository;
import com.spring.securityPractice.service.UserService;
import com.spring.securityPractice.utils.JWTUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new Exception("User already exists!!");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        String publicUserId = JWTUtils.generateUserID(10);
        userEntity.setUserId(publicUserId);
        userEntity.setRole(user.getRole());

        UserEntity storedUserDetails =userRepository.save(userEntity);
        UserDto returnedValue = modelMapper.map(storedUserDetails,UserDto.class);
        String accessToken = JWTUtils.generateToken(userEntity.getEmail());
        returnedValue.setAccessToken(AppConstants.TOKEN_PREFIX + accessToken);

        return returnedValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if(userEntity == null)
            throw new UsernameNotFoundException("No user found!");
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) throws Exception {
        UserEntity userEntity = userRepository.findByUserId(userId).orElse(null);
        if(userEntity == null)
            throw new Exception("No user found");
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if(userEntity==null)
            throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(),userEntity.getPassword(),
                true,true,true,true,
                new ArrayList<>());
    }

//
//    @Override
//    public UserDto createAdmin(UserDto userDto) throws Exception {
//        ModelMapper modelMapper = new ModelMapper();
//        if(userRepository.findByEmail(userDto.getEmail()).isPresent())
//            throw new Exception("Admin already exists!");
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail(userDto.getEmail());
//        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
//        String publicUserId = JWTUtils.generateUserID(10);
//        userEntity.setRoles(Collections.singleton("ROLE_ADMIN"));
//
//        UserEntity storedAdminDetails = userRepository.save(userEntity);
//        UserDto returnedValue = modelMapper.map(storedAdminDetails, UserDto.class);
//        return returnedValue;
//    }
}