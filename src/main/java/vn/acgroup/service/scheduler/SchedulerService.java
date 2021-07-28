package vn.acgroup.service.scheduler;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

  @SuppressWarnings("unused")
  private final Logger log = LoggerFactory.getLogger(SchedulerService.class);

  Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>(50);

  @Autowired TaskScheduler executor;

  /**
   * @param task
   * @param execTime
   * @return
   */
  protected ScheduledFuture<?> schedulingTask(Runnable task, Instant execTime) {
    if ((new Date()).after(Date.from(execTime))) {
      throw new IllegalArgumentException("Time param must be in the future");
    }

    return executor.schedule(task, execTime);
  }

  protected Runnable createRemovableIdTask(String id, Runnable task) {
    return new Runnable() {
      @Override
      public void run() {
        task.run();
        tasks.remove(id);
      }
    };
  }

  /**
   * Schedule a {@link Runnable} task to run at execTime
   *
   * @param taskId - id of the task
   * @param task
   * @param execTime
   * @return
   */
  public void createTask(String taskId, Runnable task, Instant execTime) {
    tasks.putIfAbsent(taskId, schedulingTask(createRemovableIdTask(taskId, task), execTime));
  }

  /**
   * cancel task with id
   *
   * @param taskId
   */
  public void cancelTask(String taskId) {
    tasks.computeIfPresent(
        taskId,
        (key, val) -> {
          val.cancel(true);
          return val;
        });
    tasks.remove(taskId);
  }

  public ScheduledFuture<?> getTask(String id) {
    return tasks.get(id);
  };
}
