package gt.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class User {

    @Id
    private String address;

    @Relationship(type = "TRANSFER")
    private Set<User> transfers = new HashSet<>();

    private boolean blocked;

    public User() {

    }

    public User(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public Set<User> getTransfers() {
        return Collections.unmodifiableSet(transfers);
    }

    public void addTransfer(User to) {
        transfers.add(to);
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return String.format("User{address='%s', blocked='%s', transfers=%s}", address, blocked, transfers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
