package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    Path currentPath = Paths.get("");
    final WatchService watchService = FileSystems.getDefault().newWatchService();
    currentPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
    Thread watchThread = new Thread(() -> {
      try {
        WatchKey key;
        while((key = watchService.take()) != null) {
          key.pollEvents().forEach(event -> {
            System.out.printf("event kind: %s\n", event.kind());
            System.out.printf("event context: %s\n", event.context());
          });
          key.reset();
        }
      } catch (Exception e) {
        //e.printStackTrace();
      }

    });
    watchThread.start();

    Thread.sleep(1);
    try (PrintWriter writer = new PrintWriter(Files.newOutputStream(Paths.get("test.txt"), StandardOpenOption.CREATE,
        StandardOpenOption.WRITE, StandardOpenOption.APPEND))) {
      writer.println("Hello world1");
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("test.txt"))))) {
      reader.lines().forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      Files.writeString(Paths.get("test2.txt"), "Hello world2\n", StandardOpenOption.CREATE,
          StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      System.out.println(Files.readString(Paths.get("test2.txt")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    watchThread.interrupt();
  }
}
