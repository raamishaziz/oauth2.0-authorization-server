package com.oauthserver.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oauthserver.entity.User;
import com.oauthserver.repository.UserRepository;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repo;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		List<GrantedAuthority> roles = null;
		User user = repo.findByEmail(email);
		if (null == user) {
			throw new UsernameNotFoundException("No user found");
		}
		if (null != user.getRole()) {
			roles = getRoles(List.of(user.getRole()));
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true,
				true, true, true, roles);
	}

	private List<GrantedAuthority> getRoles(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
