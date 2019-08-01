public enum IOMode {
    OFF("off"),
    STARTUP("startup"),
    REGULAR("regular");

    public String mode;

    IOMode(String mode) {
        this.mode = mode;
    }
}
