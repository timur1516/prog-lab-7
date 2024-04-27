package common;

import java.io.Serializable;

public record UserInfo(String userName, String password) implements Serializable {}
