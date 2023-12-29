package com.mogak.spring.jwt;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(useremail).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저 존재하지 않음")
        );
        if(user != null){
            return new CustomUserDetails(user);
        }
        return null;
    }
}
