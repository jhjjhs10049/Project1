package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.mid = :mid and m.social = false and m.del = false")
    Optional<Member> getWithRoles(String mid);    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.email = :email and m.del = false")
    Optional<Member> findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("update Member m set m.mpw =:mpw , m.social = false where m.mid = :mid ")
    void updatePassword(@Param("mpw") String password, @Param("mid") String mid);
}
