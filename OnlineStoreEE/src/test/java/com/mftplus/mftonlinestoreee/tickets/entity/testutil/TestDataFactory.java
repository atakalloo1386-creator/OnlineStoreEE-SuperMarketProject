package com.mftplus.mftonlinestoreee.tickets.entity.testutil;

import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.users.entity.Permission;
import com.mftplus.mftonlinestoreee.users.entity.Profile;
import com.mftplus.mftonlinestoreee.users.entity.Role;
import com.mftplus.mftonlinestoreee.users.entity.User;

import java.time.LocalDate;
import java.util.Set;

public class TestDataFactory {

    private TestDataFactory() {}

    public static Permission createPermission() {
        return Permission.builder()
                .name("Master")
                .build();
    }

    public static Role createRole() {
        return Role.builder()
                .name("Asqar")
                .permissions(Set.of(createPermission()))
                .build();
    }

    public static User createUser() {
        return User.builder()
                .username("Asqar")
                .password("1234")
                .enabled(true)
                .roles(Set.of(createRole()))
                .build();
    }

    public static Profile createProfile() {
        return Profile.builder()
                .firstName("Asqar")
                .lastName("Asqari")
                .phoneNumber("4321")
                .birthDate(LocalDate.of(1999, 11, 8))
                .address("Bahar st")
                .user(createUser())
                .build();
    }

    public static Ticket createTicket() {
        return Ticket.builder()
                .user(createUser())
                .build();
    }
}
