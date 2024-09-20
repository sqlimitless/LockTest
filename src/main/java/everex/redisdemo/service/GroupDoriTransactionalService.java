package everex.redisdemo.service;

import everex.redisdemo.enitty.GroupDori2;
import everex.redisdemo.repo.GroupDori2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupDoriTransactionalService {

    private final GroupDori2Repository groupDori2Repository;

    @Transactional
    public void performOptimisticLockingOperation(Long id) {
        GroupDori2 groupDori = groupDori2Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GroupDori not found"));
        groupDori.decreaseQuantity();
    }

}
