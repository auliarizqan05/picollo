package com.app.picollo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.app.picollo.domain.transaction.repository.BalanceRepository;
import com.app.picollo.domain.user.dto.UserRequest;
import com.app.picollo.domain.user.entity.User;
import com.app.picollo.domain.user.repository.UserRepository;
import com.app.picollo.domain.user.service.UserService;
import com.app.picollo.infrastructure.model.BaseResponse;

@SpringBootTest
class UserTests {


	static final String username = "maria";
	@Mock
	UserService userService;
	@Mock
	UserRepository userRepository;
	@Mock
	BalanceRepository balanceRepository;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository, balanceRepository);
	}

	@Test
	@DisplayName("Register user and create a token")
	void createUser() {

		UserRequest userRequest = UserRequest.of(username);

		Mockito.when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());

		BaseResponse baseResponse = userService.createUser(userRequest);
		assertNotNull(baseResponse.getData());
	}

	@Test
	@DisplayName("Register user and user already exist")
	void userAlareadyExist() {

		UserRequest userRequest = UserRequest.of(username);

		Mockito.when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(
				Optional.of(User.builder()
						.username(username)
						.token(UUID.randomUUID().toString())
						.build()));
		
		BaseResponse baseResponse = userService.createUser(userRequest);
		assertNull(baseResponse.getData());
		assertEquals(HttpStatus.CONFLICT.value(), baseResponse.getStatus());
	}

}
