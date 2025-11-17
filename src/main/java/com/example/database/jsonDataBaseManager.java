package com.example.database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class jsonDataBaseManager<T> {

    private final Path path;
    private final Class<T> type;
    private final String idFieldName;
    private final Gson gson;
    private final Function<JsonObject, T> customDeserializer; // optional

    public jsonDataBaseManager(String filepath, Class<T> type, String idFieldName) {
        this(filepath, type, idFieldName, new GsonBuilder().setPrettyPrinting().create(), null);
    }

    public jsonDataBaseManager(String filepath, Class<T> type, String idFieldName, Gson gson, Function<JsonObject, T> customDeserializer) {
        this.path = Paths.get(filepath);
        this.type = type;
        this.idFieldName = idFieldName;
        this.gson = gson != null ? gson : new GsonBuilder().setPrettyPrinting().create();
        this.customDeserializer = customDeserializer;
        ensureFileExists();
    }

    private synchronized void ensureFileExists() {
        try {
            if (Files.notExists(path)) {
                if (path.getParent() != null) Files.createDirectories(path.getParent());
                Files.write(path, "[]".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create JSON file: " + path.toString(), e);
        }
    }

    private synchronized JsonArray readJsonArrayFromFile() {
        ensureFileExists();
        try (Reader r = Files.newBufferedReader(path)) {
            JsonElement elem = JsonParser.parseReader(r);
            if (elem == null || elem.isJsonNull()) return new JsonArray();
            if (elem.isJsonArray()) return elem.getAsJsonArray();
            JsonArray arr = new JsonArray();
            arr.add(elem);
            return arr;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + path.toString(), e);
        } catch (JsonParseException e) {
            try {
                Path backup = Paths.get(path.toString() + ".corrupt." + System.currentTimeMillis());
                Files.copy(path, backup, StandardCopyOption.REPLACE_EXISTING);
                Files.write(path, "[]".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ignored) { }
            return new JsonArray();
        }
    }

    private synchronized void writeJsonArrayToFile(JsonArray array) {
        ensureFileExists();
        try (Writer w = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(array, w);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + path.toString(), e);
        }
    }

    private synchronized List<T> loadAllObjects() {
        JsonArray arr = readJsonArrayFromFile();
        List<T> result = new ArrayList<>(arr.size());
        if (customDeserializer != null) {
            for (JsonElement e : arr) {
                if (e != null && e.isJsonObject()) {
                    T obj = customDeserializer.apply(e.getAsJsonObject());
                    if (obj != null) result.add(obj);
                }
            }
        } else {
            Type listType = TypeToken.getParameterized(List.class, type).getType();
            try (Reader r = Files.newBufferedReader(path)) {
                List<T> list = gson.fromJson(r, listType);
                if (list != null) return list;
            } catch (IOException ignored) { }
            for (JsonElement e : arr) {
                try {
                    T obj = gson.fromJson(e, type);
                    if (obj != null) result.add(obj);
                } catch (JsonSyntaxException ex) { }
            }
        }
        return result;
    }

    private synchronized void saveAllObjects(List<T> list) {
        JsonArray arr = new JsonArray();
        for (T obj : list) {
            JsonElement je = gson.toJsonTree(obj);
            arr.add(je);
        }
        writeJsonArrayToFile(arr);
    }

    private String getIdFromObject(T obj) {
        if (obj == null) return null;
        String[] getters = new String[]{
                "get" + capitalize(idFieldName),
                "getId",
                "get" + capitalize(removeSuffix(idFieldName, "Id"))
        };
        for (String getter : getters) {
            try {
                Method m = obj.getClass().getMethod(getter);
                Object val = m.invoke(obj);
                if (val != null) return val.toString();
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ignored) { }
        }
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(idFieldName);
                f.setAccessible(true);
                Object val = f.get(obj);
                if (val != null) return val.toString();
                break;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Exception ignored) { break; }
        }
        return null;
    }

    private String getIdFromJson(JsonObject json) {
        if (json == null) return null;
        JsonElement e = json.get(idFieldName);
        if (e != null && !e.isJsonNull()) return e.getAsString();
        JsonElement e2 = json.get("id");
        if (e2 != null && !e2.isJsonNull()) return e2.getAsString();
        return null;
    }

    private static String removeSuffix(String s, String suffix) {
        if (s == null) return null;
        return s.endsWith(suffix) ? s.substring(0, s.length() - suffix.length()) : s;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public synchronized void add(T obj) {
        if (obj == null) throw new IllegalArgumentException("Object cannot be null");
        List<T> list = loadAllObjects();
        String id = getIdFromObject(obj);
        if (id != null && idExistsInList(list, id)) throw new IllegalArgumentException("An object with the same id already exists: " + id);
        list.add(obj);
        saveAllObjects(list);
    }

    public synchronized List<T> getAll() { return Collections.unmodifiableList(loadAllObjects()); }

    public synchronized Optional<T> getById(String id) {
        if (id == null) return Optional.empty();
        if (customDeserializer != null) {
            JsonArray arr = readJsonArrayFromFile();
            for (JsonElement e : arr) {
                if (e.isJsonObject()) {
                    String eid = getIdFromJson(e.getAsJsonObject());
                    if (id.equals(eid)) return Optional.ofNullable(customDeserializer.apply(e.getAsJsonObject()));
                }
            }
            return Optional.empty();
        } else {
            List<T> list = loadAllObjects();
            for (T t : list) {
                String tid = getIdFromObject(t);
                if (id.equals(tid)) return Optional.of(t);
            }
            return Optional.empty();
        }
    }

    public synchronized boolean updateById(String id, T newObj) {
        if (id == null) return false;
        List<T> list = loadAllObjects();
        for (int i = 0; i < list.size(); i++) {
            String tid = getIdFromObject(list.get(i));
            if (id.equals(tid)) {
                list.set(i, newObj);
                saveAllObjects(list);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteById(String id) {
        if (id == null) return false;
        List<T> list = loadAllObjects();
        Iterator<T> it = list.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            T t = it.next();
            String tid = getIdFromObject(t);
            if (id.equals(tid)) { it.remove(); removed = true; break; }
        }
        if (removed) saveAllObjects(list);
        return removed;
    }

    public synchronized boolean exists(Predicate<T> predicate) {
        if (predicate == null) return false;
        List<T> list = loadAllObjects();
        for (T t : list) if (predicate.test(t)) return true;
        return false;
    }

    public String generateUniqueId(String prefix) {
        if (prefix == null) prefix = "";
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }

    private boolean idExistsInList(List<T> list, String id) {
        for (T t : list) if (id.equals(getIdFromObject(t))) return true;
        return false;
    }

    public Path getPath() { return path; }
}
