package everex.redisdemo.repo;

import everex.redisdemo.enitty.GroupDori;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupDoriRepository extends JpaRepository<GroupDori, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GroupDori g WHERE g.id = :id")
    GroupDori findByIdForUpdate(@Param("id") Long id);
}
