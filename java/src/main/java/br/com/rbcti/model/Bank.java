package br.com.rbcti.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Renato Cunha
 *
 */
public class Bank implements Serializable {

    private static final long serialVersionUID = -941969583967776676L;

    private Integer code;
    private String name;

    public Bank() {
        super();
    }

    public Bank(Integer code, String name) {
        super();
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Bank other = (Bank) obj;
        return Objects.equals(code, other.code);
    }

    @Override
    public String toString() {
        return "Bank [code=" + code + ", name=" + name + "]";
    }

}
