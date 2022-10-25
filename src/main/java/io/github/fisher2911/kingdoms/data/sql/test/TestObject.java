package io.github.fisher2911.kingdoms.data.sql.test;

import java.util.List;
import java.util.Objects;

public class TestObject {

    private final int id;
    private final String name;
    private final String description;
    private final List<Member> members;

    public TestObject(int id, String name, String description, List<Member> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Member> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members=" + members +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TestObject that = (TestObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
