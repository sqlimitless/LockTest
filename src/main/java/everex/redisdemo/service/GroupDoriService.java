package everex.redisdemo.service;

import everex.redisdemo.common.MysqlLock;
import everex.redisdemo.common.RedissonLock;
import everex.redisdemo.enitty.GroupDori;
import everex.redisdemo.repo.GroupDoriRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GroupDoriService {

    private final GroupDoriRepository groupDoriRepository;
    private final RedissonClient redissonClient;
    private final GroupDoriTransactionalService groupDoriTransactionalService;


    @Transactional
    public void startExercising(Long groupDoriId) {
        GroupDori groupDori = groupDoriRepository.findById(groupDoriId).orElseThrow();
        groupDori.decreaseQuantity();
    }

    @Transactional
    public void startExercisingByRedisson(Long groupDoriId) {
        RLock lock = redissonClient.getLock("groupDori:" + groupDoriId);

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }
            GroupDori groupDori = groupDoriRepository.findById(groupDoriId).orElseThrow();
            groupDori.decreaseQuantity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @RedissonLock(group = "groupDori:", value = "#groupDoriId")
    public void startExercisingAnno(Long groupDoriId) {
        GroupDori groupDori = groupDoriRepository.findById(groupDoriId).orElseThrow();
        groupDori.decreaseQuantity();
    }

    @Transactional
    @MysqlLock(group = "dori", value = "#id")
    public void mysqlLock(Long id) {
        GroupDori groupDori = groupDoriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GroupDori not found"));
        groupDori.decreaseQuantity();
    }

    @Transactional
    public void pessimisticWrite(Long id) {
        GroupDori groupDori = groupDoriRepository.findByIdForUpdate(id);
        groupDori.decreaseQuantity();
    }

    public void versionWrite(Long id) {
        try {
            groupDoriTransactionalService.performOptimisticLockingOperation(id);
        } catch (ObjectOptimisticLockingFailureException e) {
            this.versionWrite(id);
        }
    }
}
