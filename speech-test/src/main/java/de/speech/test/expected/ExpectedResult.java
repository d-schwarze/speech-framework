package de.speech.test.expected;

public class ExpectedResult {

    private int id;

    private String path;

    private String actual;

    public ExpectedResult() {

    }

    public ExpectedResult(int id, String path, String actual) {
        this.id = id;
        this.path = path;
        this.actual = actual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }
}
