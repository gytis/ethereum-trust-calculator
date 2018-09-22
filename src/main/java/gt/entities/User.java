package gt.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class User {

    @Id
    private String address;

    @Relationship(type = "TRANSFER")
    private Set<User> transfers;

    public User(String address) {
        this.address = address;
        this.transfers = new HashSet<>();
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
}
