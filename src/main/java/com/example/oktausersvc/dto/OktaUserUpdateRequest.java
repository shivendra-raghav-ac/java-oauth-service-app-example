package com.example.oktausersvc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OktaUserUpdateRequest {

    @JsonProperty("profile")
    private final Profile profile = new Profile();

    public OktaUserUpdateRequest() {
    }

    public OktaUserUpdateRequest(String firstName, String lastName) {
        this.profile.setFirstName(firstName);
        this.profile.setLastName(lastName);
    }

    public Profile getProfile() {
        return profile;
    }

    // Convenience accessors to set directly on outer object
    public String getFirstName() {
        return profile.getFirstName();
    }

    public void setFirstName(String firstName) {
        profile.setFirstName(firstName);
    }

    public String getLastName() {
        return profile.getLastName();
    }

    public void setLastName(String lastName) {
        profile.setLastName(lastName);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Profile {
        @Size(min = 1, max = 100)
        @JsonProperty("firstName")
        private String firstName;

        @Size(min = 1, max = 100)
        @JsonProperty("lastName")
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
