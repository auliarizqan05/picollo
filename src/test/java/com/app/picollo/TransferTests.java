package com.app.picollo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.app.picollo.domain.transaction.dto.TopTransactionPerUserResponse;
import com.app.picollo.domain.transaction.dto.TopUserResponse;
import com.app.picollo.domain.transaction.dto.TransferRequest;
import com.app.picollo.domain.transaction.entity.Balance;
import com.app.picollo.domain.transaction.entity.Transaction;
import com.app.picollo.domain.transaction.repository.BalanceRepository;
import com.app.picollo.domain.transaction.repository.TransactionRepository;
import com.app.picollo.domain.transaction.service.TransferService;
import com.app.picollo.domain.user.entity.User;
import com.app.picollo.domain.user.repository.UserRepository;
import com.app.picollo.infrastructure.constant.TransactionConstant;
import com.app.picollo.infrastructure.model.BaseResponse;

@SpringBootTest
class TransferTests {

	static final String username = "maria";
	static final String usernameDestination = "alex";
	@Mock
	TransferService transferService;
	@Mock
	UserRepository userRepository;
	@Mock
	BalanceRepository balanceRepository;
	@Mock
	TransactionRepository transactionRepository;

	@BeforeEach
	void setUp() {
		transferService = new TransferService(userRepository, balanceRepository, transactionRepository);
	}

	@Test
	@DisplayName("Do a transfer amount")
	void transfer() {

		TransferRequest transferRequest = TransferRequest.of(new BigDecimal(2000), usernameDestination);

		// check username destination is found or not
		Mockito.when(userRepository.findByUsername(usernameDestination)).thenReturn(
				Optional.of(User.builder()
						.username(usernameDestination)
						.token(UUID.randomUUID().toString())
						.build()));

		// find username
		Mockito.when(balanceRepository.findByUsername(username)).thenReturn(
				Optional.of(Balance.builder()
						.balance(new BigDecimal(5000))
						.username(username)
						.build()));

		// find username destination
		Mockito.when(balanceRepository.findByUsername(usernameDestination)).thenReturn(
				Optional.of(Balance.builder()
						.balance(new BigDecimal(3000))
						.username(usernameDestination)
						.build()));

		BaseResponse baseResponse = transferService.transfer(transferRequest, username);
		assertEquals(HttpStatus.NO_CONTENT.value(), baseResponse.getStatus());
	}

	@Test
	@DisplayName("Do a transfer with amount not sufficient")
	void transferWithAmountNotSufficient() {

		TransferRequest transferRequest = TransferRequest.of(new BigDecimal(2000), usernameDestination);

		// check username destination is found or not
		Mockito.when(userRepository.findByUsername(usernameDestination)).thenReturn(
				Optional.of(User.builder()
						.username(usernameDestination)
						.token(UUID.randomUUID().toString())
						.build()));

		// find username
		Mockito.when(balanceRepository.findByUsername(username)).thenReturn(
				Optional.of(Balance.builder()
						.balance(new BigDecimal(1000))
						.username(username)
						.build()));

		// find username destination
		Mockito.when(balanceRepository.findByUsername(usernameDestination)).thenReturn(
				Optional.of(Balance.builder()
						.balance(new BigDecimal(3000))
						.username(usernameDestination)
						.build()));

		BaseResponse baseResponse = transferService.transfer(transferRequest, username);
		assertEquals(HttpStatus.BAD_REQUEST.value(), baseResponse.getStatus());
		assertNull(baseResponse.getData());

	}

	@Test
	@DisplayName("Do a transfer with user destination not exist")
	void transferWithUserDestinationNotFound() {

		TransferRequest transferRequest = TransferRequest.of(new BigDecimal(2000), usernameDestination);

		// set username destination is not
		Mockito.when(userRepository.findByUsername(usernameDestination)).thenReturn(Optional.empty());

		BaseResponse baseResponse = transferService.transfer(transferRequest, username);
		assertEquals(HttpStatus.NOT_FOUND.value(), baseResponse.getStatus());
		assertNull(baseResponse.getData());

	}

	@Test
	@DisplayName("Find top users with summed up debit transfer")
	void findTopUsers() {

		Mockito.when(transactionRepository.findByType(TransactionConstant.TRANSFER)).thenReturn(
				List.of(TopUserResponse.builder()
						.username("jhon")
						.transactedValue(new BigDecimal(2000))
						.build()));

		List<TopUserResponse> topUserResponse = List.of(TopUserResponse.builder()
				.username("jhon")
				.transactedValue(new BigDecimal(2000))
				.build());
		BaseResponse baseResponse = transferService.getTopUsers();
		assertEquals(topUserResponse, baseResponse.getData());
	}

	@Test
	@DisplayName("Find top transactions per user")
	void findTopTransactionsPerUsers() {

		// set username destination is not
		Mockito.when(transactionRepository.findByTypeAndUsernameOrToUsername(TransactionConstant.TRANSFER, username,
				username, PageRequest.of(0, 10, Sort.Direction.DESC, "amount")))
				.thenReturn(
						List.of(
								Transaction.builder()
										.username(username)
										.amount(new BigDecimal(3000))
										.toUsername("john")
										.type(TransactionConstant.TRANSFER)
										.build(),
								Transaction.builder()
										.username("brad")
										.amount(new BigDecimal(2000))
										.toUsername(username)
										.type(TransactionConstant.TRANSFER)
										.build()));

		List<TopTransactionPerUserResponse> topTransactionPerUserResponses = List.of(
			TopTransactionPerUserResponse.builder()
				.username(username)
				.amount(new BigDecimal(3000).negate())
				.build(),
			TopTransactionPerUserResponse.builder()
				.username(username)
				.amount(new BigDecimal(2000))
				.build());
		BaseResponse baseResponse = transferService.getTopTransactionPerUser(username);
		assertEquals(topTransactionPerUserResponses, baseResponse.getData());
	}

}
