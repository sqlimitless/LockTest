package everex.redisdemo.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class FacadeGroupDoriService {
    private final RedissonClient redissonClient;
    private final GroupDoriService groupDoriService;

    public void startExercisingByRedisson(Long groupDoriId) {
        RLock lock = redissonClient.getLock("groupDori:"+groupDoriId);

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }
            groupDoriService.startExercising(groupDoriId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
