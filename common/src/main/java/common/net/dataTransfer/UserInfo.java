package common.net.dataTransfer;

import java.io.Serializable;

/**
 * Record to store and transfer information about user
 * @param userName
 * @param password
 */
public record UserInfo(String userName, String password) implements Serializable {}
