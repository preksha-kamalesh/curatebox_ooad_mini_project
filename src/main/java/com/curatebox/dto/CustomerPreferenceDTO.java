package com.curatebox.dto;

public class CustomerPreferenceDTO {
    private Long preferenceOptionId;
    private boolean isLike;

    public Long getPreferenceOptionId() {
        return preferenceOptionId;
    }

    public void setPreferenceOptionId(Long preferenceOptionId) {
        this.preferenceOptionId = preferenceOptionId;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
