package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractController<T extends Identifiable> {
  private final Map<Integer, T> data = new HashMap<>();
  private int id = 0;

  private int getNextId() {
    return ++id;
  }

  public int addItem(T item) {
    log.info("Add new item");
    validate(item);
    int itemId = getNextId();
    log.info("Storing item with id " + itemId);
    data.put(itemId, item);
    item.setId(itemId);
    return itemId;
  }

  public void updateItem(T item) {
    validate(item);
    int itemId = item.getId();
    if (!data.containsKey(itemId)) {
      log.warn("Item with id {} not found", itemId);
      throw new ValidationException("Обновление невозможно, идентификатор " + itemId + "не найден");
    }
    log.info("Update item with id " + itemId);
    data.put(itemId, item);
  }

  public Collection<T> getData() {
    log.info("Get all items");
    return data.values();
  }

  protected void validate(T item) {
    if (item == null) {
      log.warn("Item is null");
      throw new IllegalArgumentException("Объект не задан");
    }
  }

}
