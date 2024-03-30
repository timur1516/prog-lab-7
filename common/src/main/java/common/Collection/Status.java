package common.Collection;

import java.io.Serializable;

/**
 * Constants to describe worker's status
 */
public enum Status implements Serializable {
    FIRED,
    HIRED,
    REGULAR,
    PROBATION;
}
