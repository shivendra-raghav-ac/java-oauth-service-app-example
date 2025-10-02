package com.example.oktausersvc.dto;

import java.util.List;

public class OktaPagedUsersResponse {
    private List<OktaUserResponse> items;
    private String nextCursor; // null if no next page

    public OktaPagedUsersResponse() {
    }

    public OktaPagedUsersResponse(List<OktaUserResponse> items, String nextCursor) {
        this.items = items;
        this.nextCursor = nextCursor;
    }

    public List<OktaUserResponse> getItems() {
        return items;
    }

    public void setItems(List<OktaUserResponse> items) {
        this.items = items;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
