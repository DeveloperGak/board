package site.board.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.board.domain.Crew;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, String> {

    @Query("SELECT c FROM Crew c WHERE c.id = :id")
    Optional<Crew> getWithRoles(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE Crew c SET c.password = :password WHERE c.id = :id")
    void updatePassword(String password, String id);
}
