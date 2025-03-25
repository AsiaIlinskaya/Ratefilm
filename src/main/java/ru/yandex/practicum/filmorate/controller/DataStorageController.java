package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DataStorageController<T> {
  private final Map<Integer, T> data = new HashMap<>();
  private int id = 0;

  public int nextId() {
    return ++id;
  }

  public void addItem(int id, T item) {
    log.info("Adding item " + id);
    data.put(id, item);
  }

  public void updateItem(int id, T item) {
    if (!data.containsKey(id)) {
      log.warn("id {} not found", id);
      throw new ValidationException("Обновление невозможно, идентификатор " + id + "не найден");
    }
    log.info("Replacing item " + id);
    data.put(id, item);
  }

  public Collection<T> getData() {
    log.info("GetData call");
    return data.values();
  }

}
