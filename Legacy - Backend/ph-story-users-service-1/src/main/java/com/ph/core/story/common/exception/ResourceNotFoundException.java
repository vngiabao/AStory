package com.ph.core.story.common.exception;

public class ResourceNotFoundException extends BaseBusinessException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(
                ErrorCode.NOT_FOUND,
                resourceName + " not found with id: " + id
        );
    }
}

//public class ResourceNotFoundException extends BaseBusinessException {
//
//    private final String resourceName;
//    private final Serializable identifier;
//
//    public ResourceNotFoundException(String message) {
//        super(message);
//        this.resourceName = null;
//        this.identifier = null;
//    }
//
//    public ResourceNotFoundException(String resourceName, Serializable identifier) {
//        super(String.format("%s not found with id: %s", resourceName, identifier));
//        this.resourceName = resourceName;
//        this.identifier = identifier;
//    }
//
//    public String getResourceName() {
//        return resourceName;
//    }
//
//    public Serializable getIdentifier() {
//        return identifier;
//    }
//}
