package com.godeltech.currencyexchange.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.godeltech.currencyexchange.model.Authority;
import com.godeltech.currencyexchange.model.CurrencyUser;
import com.godeltech.currencyexchange.repository.CurrencyUserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CurrencyUserServiceTest {

  @Mock private CurrencyUserRepository currencyUserRepository;

  @InjectMocks private CurrencyUserService currencyUserService;

  private CurrencyUser testUser;

  @BeforeEach
  void setUp() {
    Authority userAuthority = new Authority();
    userAuthority.setAuthority("USER");

    testUser = new CurrencyUser();
    testUser.setUsername("testuser");
    testUser.setPassword("password");
    testUser.setAuthorities(List.of(userAuthority));
  }

  @Test
  void loadUserByUsername() {

    final var userName = "testuser";
    final var password = "password";

    final var expectedUserDetails =
        User.builder()
            .username(userName)
            .password(password)
            .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
            .build();

    when(currencyUserRepository.findByUsername(userName)).thenReturn(Optional.of(testUser));

    UserDetails actualUserDetails = currencyUserService.loadUserByUsername(userName);

    assertEquals(expectedUserDetails, actualUserDetails);

    verify(currencyUserRepository).findByUsername(userName);
  }

  @Test
  void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {

    final var nonExistingUser = "nonexistentuser";

    when(currencyUserRepository.findByUsername(nonExistingUser)).thenReturn(Optional.empty());

    UsernameNotFoundException exception =
        assertThrows(
            UsernameNotFoundException.class,
            () -> currencyUserService.loadUserByUsername(nonExistingUser));

    assertEquals("User nonexistentuser not found", exception.getMessage());

    verify(currencyUserRepository).findByUsername(nonExistingUser);
  }
}
