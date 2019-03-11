package ru.iteco.springl.components;

import java.math.BigDecimal;
import java.security.SecureRandom;

public interface Shape {
    SecureRandom random = new SecureRandom();

    BigDecimal getArea();

    void setName(String name);

    String getName();
}
