package com.mftplus.mftonlinestoreee.organization.cache;

public record CacheInvalidation(Region region, Long id) {
    public enum Region {
        ORGANIZATION,
        DEPARTMENT,
        ALL_DEPARTMENTS
    }
}
