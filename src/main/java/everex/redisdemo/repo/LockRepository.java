package everex.redisdemo.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean getLock(@Param("lockName") String lockName, @Param("timeout") int timeout) {
        String sql = "SELECT GET_LOCK(?, ?)";
        Integer result = jdbcTemplate.queryForObject(sql, new Object[]{lockName, timeout}, Integer.class);
        return result != null && result == 1;
    }

    public boolean releaseLock(String lockName) {
        String sql = "SELECT RELEASE_LOCK(?)";
        Integer result = jdbcTemplate.queryForObject(sql, new Object[]{lockName}, Integer.class);
        return result != null && result == 1;
    }
}