package com.bridgelabz.fundoo.user.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.dto.LoginDTo;
import com.bridgelabz.fundoo.dto.UserDTo;
import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.model.User;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.response.ResponseToken;
//import com.bridgelabz.fundoo.dto.LoginDTo;
//import com.bridgelabz.fundoo.dto.UserDTo;
//import com.bridgelabz.fundoo.model.User;

@Service
public interface UserService {

	Response onRegister(UserDTo userDto) throws UserException, UnsupportedEncodingException;

	ResponseToken onLogin(LoginDTo loginDto) throws UserException, UnsupportedEncodingException;

	ResponseToken authentication(Optional<User> user, String password);

	Response validateEmailId(String token);

	Response resetPaswords(String token, String password);

	Response forgetPassword(String emailId) throws UserException, UnsupportedEncodingException;
}
