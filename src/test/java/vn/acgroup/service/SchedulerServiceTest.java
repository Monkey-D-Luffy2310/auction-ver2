package vn.acgroup.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vn.acgroup.service.scheduler.SchedulerService;
import vn.acgroup.spring.Application;

@SpringBootTest(classes = {Application.class})
// @Disabled
public class SchedulerServiceTest {

  private final Logger log = LoggerFactory.getLogger(SchedulerServiceTest.class);

  @Autowired SchedulerService scheduler;

  protected Runnable runTask(String id) {
    return new Runnable() {
      @Override
      public void run() {
        try {
          log.warn("thread {} start", id);
          Thread.sleep(3000);
          log.warn("thread {} completed", id);
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    };
  }

  @Test
  public void testScheduler() throws InterruptedException {
    Instant execTime =
        LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
            .plusSeconds(2)
            .atZone(ZoneId.systemDefault())
            .toInstant();
    scheduler.createTask("1", runTask("1"), execTime);
    scheduler.createTask("2", runTask("2"), execTime);

    Thread.sleep(2000);
    log.info("cancel task 1");
    scheduler.cancelTask("1");
    Thread.sleep(5000);
  }
}
