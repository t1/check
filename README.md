Just add this jar to your Java-EE application and write Check classes like this one:

```java
public class FooCheck implements Check {
    @Override
    public CheckResult get() {
        return status(OK).comment("foo").build();
    }
}
```

Access the health status of an application on `<app>/-system/checks`,
e.g. for a `ping` application, this could be `http://localhost:8080/ping/-system/checks`

```yaml
summary:
  status: OK
  counters:
    OK: 1
    UNKNOWN: 0
    WARNING: 0
    FAILURE: 0
checks:
- type: com.github.t1.ping.FooCheck
  status: OK
  comment: foo
```
