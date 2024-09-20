package everex.redisdemo;

import everex.redisdemo.enitty.GroupDori;
import everex.redisdemo.enitty.GroupDori2;
import everex.redisdemo.repo.GroupDori2Repository;
import everex.redisdemo.repo.GroupDoriRepository;
import everex.redisdemo.service.FacadeGroupDoriService;
import everex.redisdemo.service.GroupDoriService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class 동시성_테스트 {

    @Autowired
    private GroupDoriService groupDoriService;

    @Autowired
    private GroupDoriRepository groupDoriRepository;

    @Autowired
    private FacadeGroupDoriService facadeGroupDoriService;

    @Autowired
    private GroupDori2Repository groupDori2Repository;

    /*
    * 동시성 문제*/
    @Test
    void 동시에_1000명이_운동해버리기() throws InterruptedException {
        // given
        GroupDori groupDori = GroupDori.builder()
                .quantity(1000)
                .build();
        groupDoriRepository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    groupDoriService.startExercising(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori updatedGroupDori = groupDoriRepository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

    /*
    * 실패
    * 트랜젝션보다 락이 먼저 풀렸음.*/
    @Test
    void 레스드사용() throws InterruptedException {
        // given
        GroupDori groupDori = GroupDori.builder()
                .quantity(1000)
                .build();
        groupDoriRepository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    groupDoriService.startExercisingByRedisson(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori updatedGroupDori = groupDoriRepository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

    @Test
    void 파사드로감싸서1000명동시() throws InterruptedException {
        // given
        GroupDori groupDori = GroupDori.builder()
                .quantity(1000)
                .build();
        groupDoriRepository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    facadeGroupDoriService.startExercisingByRedisson(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori updatedGroupDori = groupDoriRepository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

    @Test
    void 어노테이션으로감싼_레디스() throws InterruptedException {
        // given
        GroupDori groupDori = GroupDori.builder()
                .quantity(1000)
                .build();
        groupDoriRepository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    groupDoriService.startExercisingAnno(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori updatedGroupDori = groupDoriRepository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

    @Test
    void mysql_네임드락_사용_동시성체크() throws InterruptedException {
        // given
        GroupDori groupDori = GroupDori.builder()
                .quantity(1000)
                .build();
        groupDoriRepository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    groupDoriService.mysqlLock(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori updatedGroupDori = groupDoriRepository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

    @Test
    void 비관적락() throws InterruptedException {
        // given
        GroupDori groupDori = GroupDori.builder()
                .quantity(1000)
                .build();
        groupDoriRepository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    groupDoriService.pessimisticWrite(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori updatedGroupDori = groupDoriRepository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

    @Test
    void 낙관적락() throws InterruptedException {
        // given
        GroupDori2 groupDori = GroupDori2.builder()
                .quantity(1000)
                .build();
        groupDori2Repository.save(groupDori);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    groupDoriService.versionWrite(groupDori.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("소요시간:"+(System.currentTimeMillis()-stime)+"ms");
        // then
        GroupDori2 updatedGroupDori = groupDori2Repository.findById(groupDori.getId()).orElseThrow();
        assertThat(updatedGroupDori.getQuantity()).isEqualTo(0);
    }

}
