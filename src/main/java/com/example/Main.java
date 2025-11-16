package com.example;

import com.google.gson.Gson;

public class Main {
    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static void main(String[] args) {
        Gson gson = new Gson();

        Person original = new Person("Alice", 30);
        String json = gson.toJson(original);
        System.out.println("Serialized JSON: " + json);

        Person roundTrip = gson.fromJson(json, Person.class);
        System.out.println("Deserialized -> name=" + roundTrip.name + ", age=" + roundTrip.age);
    }
}