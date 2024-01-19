package site.board.config.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.board.domain.Crew;
import site.board.dto.CrewDTO;
import site.board.repository.CrewRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CrewRepository crewRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Crew> result = crewRepository.findById(username);
        Crew crew = result.orElseThrow(() -> new UsernameNotFoundException("not found id"));

        return new CrewDTO(
                crew.getId(),
                crew.getPassword(),
                List.of(new SimpleGrantedAuthority(crew.getRole().toString()))
        );
    }
}
