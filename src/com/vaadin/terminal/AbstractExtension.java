/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.server.ClientConnector;

/**
 * An extension is an entity that is attached to a Component or another
 * Extension and independently communicates between client and server.
 * <p>
 * Extensions can use shared state and RPC in the same way as components.
 * <p>
 * AbstractExtension adds a mechanism for adding the extension to any Connector
 * (extend). To let the Extension determine what kind target it can be added to,
 * the extend method is declared as protected.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public abstract class AbstractExtension extends AbstractClientConnector
        implements Extension {
    private boolean previouslyAttached = false;

    /**
     * Gets a type that the parent must be an instance of. Override this if the
     * extension only support certain targets, e.g. if only TextFields can be
     * extended.
     * 
     * @return a type that the parent must be an instance of
     */
    protected Class<? extends ClientConnector> getSupportedParentType() {
        return ClientConnector.class;
    }

    /**
     * Add this extension to the target connector. This method is protected to
     * allow subclasses to require a more specific type of target.
     * 
     * @param target
     *            the connector to attach this extension to
     */
    protected void extend(AbstractClientConnector target) {
        target.addExtension(this);
    }

    /**
     * Remove this extension from its target. After an extension has been
     * removed, it can not be attached again.
     */
    public void removeFromTarget() {
        getParent().removeExtension(this);
    }

    @Override
    public void setParent(ClientConnector parent) {
        if (previouslyAttached && parent != null) {
            throw new IllegalStateException(
                    "An extension can not be set to extend a new target after getting detached from the previous.");
        }

        Class<? extends ClientConnector> supportedParentType = getSupportedParentType();
        if (parent == null || supportedParentType.isInstance(parent)) {
            super.setParent(parent);
            previouslyAttached = true;
        } else {
            throw new IllegalArgumentException(getClass().getName()
                    + " can only be attached to targets of type "
                    + supportedParentType.getName() + " but attach to "
                    + parent.getClass().getName() + " was attempted.");
        }
    }

}