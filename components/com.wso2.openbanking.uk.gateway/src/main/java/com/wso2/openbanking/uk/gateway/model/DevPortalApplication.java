package com.wso2.openbanking.uk.gateway.model;

import java.util.Map;

/**
 * This class represents the DevPortalApplication model.
 */
public class DevPortalApplication {
    private final String applicationId;
    private final String name;
    private final String throttlingPolicy;
    private final String description;
    private final String tokenType;
    private final String[] groups;
    private final Map<String, String> attributes;
    private final String[] subscriptionScopes;

    /**
     * Constructs a new DevPortalApplication with the specified details.
     *
     * @param applicationId      the application ID.
     * @param name               the application name.
     * @param throttlingPolicy   the throttling policy.
     * @param description        the description.
     * @param tokenType          the token type.
     * @param groups             the groups.
     * @param attributes         the attributes.
     * @param subscriptionScopes the subscription scopes.
     */
    public DevPortalApplication(
            String applicationId,
            String name,
            String throttlingPolicy,
            String description,
            String tokenType,
            String[] groups,
            Map<String, String> attributes,
            String[] subscriptionScopes
    ) {
        this.applicationId = applicationId;
        this.name = name;
        this.throttlingPolicy = throttlingPolicy;
        this.description = description;
        this.tokenType = tokenType;
        this.groups = groups;
        this.attributes = attributes;
        this.subscriptionScopes = subscriptionScopes;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getName() {
        return name;
    }

    public String getThrottlingPolicy() {
        return throttlingPolicy;
    }

    public String getDescription() {
        return description;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String[] getGroups() {
        return groups;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String[] getSubscriptionScopes() {
        return subscriptionScopes;
    }
}
