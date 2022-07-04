import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Output
 *
 * Doing work for taskId: task1
 * Doing work for taskId: task3
 * Doing work for taskId: task4
 * Doing work for taskId: task5
 * All done
 * Doing work for taskId: task1
 * Doing work for taskId: task3
 * Doing work for taskId: task4
 * Doing work for taskId: task5
 * All done. Successful tasks: 4
 *
 */
public class RunnableVsCallable {
  public static void main(String[] args) throws InterruptedException {
    TaskExecutor taskExecutor = new TaskExecutor();
    taskExecutor.executeRunnable();
    taskExecutor.executeCallable();
  }
}

class Task {
  String taskId;

  Task(String taskId) {
    this.taskId = taskId;
  }

  public void doWork() throws Exception {
    if (taskId.endsWith("2")) // Throwing exception for Task2
      throw new Exception("Task errored");

    System.out.println("Doing work for taskId: " + taskId);
  }
}

class TaskExecutor {
  List<Task> tasks;
  private ExecutorService executorService;
  private static final String COMPLETED = "Completed";
  private static final String FAILED = "Failed";

  public TaskExecutor() {
    tasks = getTasks();
  }

  public void executeRunnable() {
    executorService = Executors.newFixedThreadPool(tasks.size());
    List<Future> futures = new ArrayList<>();

    tasks.stream().forEach(task -> {
      Future<?> future = executorService.submit(() -> {
        try {
          task.doWork();
        } catch (Exception e) {
        }
      });

      futures.add(future);
    });

    futures.stream().forEach(future -> {
      try {
        final Object o = future.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    });

    System.out.println("All done");
    executorService.shutdown();
  }

  public void executeCallable() throws InterruptedException {
    executorService = Executors.newFixedThreadPool(tasks.size());
    List<Callable<String>> callables = new ArrayList<>();

    tasks.stream().forEach(task -> {
      Callable<String> callableTask = () -> {
        try {
          task.doWork();
          return COMPLETED;
        } catch (Exception e) {
          return FAILED;
        }
      };

      callables.add(callableTask);
    });

    int successfulTasks = 0;
    List<Future<String>> futures = executorService.invokeAll(callables);

    for (Future future: futures) {
      try {
        String o = (String) future.get();
        if (o.equals(COMPLETED)) {
          successfulTasks++;
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    System.out.println("All done. Successful tasks: " + successfulTasks);
    executorService.shutdown();
  }

  private List<Task> getTasks() {
    List<Task> tasks = new ArrayList<>();
    tasks.add(new Task("task1"));
    tasks.add(new Task("task2"));
    tasks.add(new Task("task3"));
    tasks.add(new Task("task4"));
    tasks.add(new Task("task5"));
    return tasks;
  }
}

