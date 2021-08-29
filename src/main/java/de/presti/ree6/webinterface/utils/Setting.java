package de.presti.ree6.webinterface.utils;

public class Setting {

    private String name;
    private Object value;

    public Setting(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public boolean isBooleanValue() {
        return value instanceof Boolean;
    }

    public boolean isStringValue() {
        return value instanceof String;
    }

    public boolean getBooleanValue() {
        if (isBooleanValue()) {
            return (Boolean)value;
        } else if (isStringValue()) {
            return Boolean.parseBoolean((String)value);
        }

        return true;
    }

    public String getStringValue() {
        if (isStringValue()) {
            return (String) value;
        } else if (isBooleanValue()) {
            return (Boolean)value + "";
        } else if (getName().equalsIgnoreCase("chatprefix")) {
            return "ree!";
        }

        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        if (value instanceof Boolean) {
            return getBooleanValue();
        } else if (value instanceof  String) {
            return getStringValue();
        }

        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
