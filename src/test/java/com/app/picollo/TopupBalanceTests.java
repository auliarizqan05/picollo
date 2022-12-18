package com.app.picollo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.app.picollo.domain.transaction.dto.BalanceResponse;
import com.app.picollo.domain.transaction.dto.TopupBalanceRequest;
import com.app.picollo.domain.transaction.entity.Balance;
import com.app.picollo.domain.transaction.repository.BalanceRepository;
import com.app.picollo.domain.transaction.repository.TransactionRepository;
import com.app.picollo.domain.transaction.service.TopupBalanceService;
import com.app.picollo.infrastructure.model.BaseResponse;

@SpringBootTest
class TopupBalanceTests {

	static final String username = "maria";
	@Mock
	TopupBalanceService topupService;
	@Mock
	BalanceRepository balanceRepository;
	@Mock
	TransactionRepository transactionRepository;

	@BeforeEach
	void setUp() {
		topupService = new TopupBalanceService(balanceRepository, transactionRepository);
	}

	@Test
	@DisplayName("Do a topup balance")
	void topupBalance() {

		TopupBalanceRequest topupBalanceRequest = TopupBalanceRequest.of(new BigDecimal(1000));

		Mockito.when(balanceRepository.findByUsername(username)).thenReturn(
				Optional.of(Balance.builder()
						.balance(topupBalanceRequest.getAmount())
						.username(username)
						.build()));
						
		BaseResponse baseResponse = topupService.balanceTopup(topupBalanceRequest, username);
		assertEquals(HttpStatus.NO_CONTENT.value(), baseResponse.getStatus());

	}

	@Test
	@DisplayName("Do a topup balance with amount not satisfied")
	void topupBalanceAmountNegative() {

		TopupBalanceRequest topupBalanceRequest = TopupBalanceRequest.of(new BigDecimal(-1000));

		Mockito.when(balanceRepository.findByUsername(username)).thenReturn(
				Optional.of(Balance.builder()
						.balance(topupBalanceRequest.getAmount())
						.username(username)
						.build()));

		BaseResponse baseResponse = topupService.balanceTopup(topupBalanceRequest, username);
		assertEquals(HttpStatus.BAD_REQUEST.value(), baseResponse.getStatus());
		assertNull(baseResponse.getData());

	}

	@Test
	@DisplayName("Check balance")
	void checkBalance() {

		BalanceResponse balanceResponse = BalanceResponse.of(new BigDecimal(1000));

		Mockito.when(balanceRepository.findByUsername(username)).thenReturn(
				Optional.of(Balance.builder()
						.balance(new BigDecimal(1000))
						.username(username)
						.build()));

		BaseResponse baseResponse = topupService.checkBalance(username);
		assertEquals(balanceResponse, baseResponse.getData());

	}

}
