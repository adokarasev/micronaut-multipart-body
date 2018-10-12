# Sample project to verify Micronaut issue [#722](https://github.com/micronaut-projects/micronaut-core/issues/722)

## ISSUE RESOLVED

The problem was I did not specify the content type on the declarative client's `upload()` method.
The correct way to declare a method for file uploads is this:

```$java
@Client("upload-service")
public interface UploadClient {
    @Post(produces = MediaType.MULTIPART_FORM_DATA)
    List<String> upload(@Body MultipartBody files);
}
```

## Steps to reproduce

### Run tests

```
$ ./gradlew test

multipart.body.UploadTests > uploadWithDeclarativeClient FAILED
    io.micronaut.http.codec.CodecException

2 tests completed, 1 failed
```

### Manual test of UploadController

#### Start service

`$ ./gradlew run`

#### Upload a file

with CURL

`$ curl -v -F file=@image.png http://localhost:8080/`

with HTTPie

```
$ http --form :8080 file@image.png

HTTP/1.1 200 OK
Date: Fri, 12 Oct 2018 12:47:11 GMT
connection: keep-alive
content-length: 89
content-type: application/json

[
    "/var/folders/r1/qrpnd991245bvh1h2f0n6lcw0000gn/T/micronaut-7660193161300299761.upload"
]
```
