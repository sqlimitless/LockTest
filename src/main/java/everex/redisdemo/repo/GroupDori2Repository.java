package everex.redisdemo.repo;

import everex.redisdemo.enitty.GroupDori2;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupDori2Repository extends JpaRepository<GroupDori2, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<GroupDori2> findById(Long id);
}
