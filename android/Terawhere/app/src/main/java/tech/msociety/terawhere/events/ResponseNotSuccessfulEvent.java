package tech.msociety.terawhere.events;

public class ResponseNotSuccessfulEvent {
    private Throwable throwable;
    
    public ResponseNotSuccessfulEvent(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public Throwable getThrowable() {
        return throwable;
    }
}
