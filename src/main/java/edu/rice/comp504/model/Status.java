package edu.rice.comp504.model;

/**
 * The status code sent to frontend.
 */
public class Status {
    private int status;

    /**
     * Constructor.
     * @param status the status code
     */
    public Status(int status) {
        this.status = status;
    }

    /**
     * Get the status.
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the status
     * @param status the status
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
