package multipart.body;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UploadTests {

    static EmbeddedServer embeddedServer;

    @BeforeClass
    public static void setup() {
        embeddedServer = ApplicationContext.run(EmbeddedServer.class);
    }

    @AfterClass
    public static void cleanup() {
        if (embeddedServer != null) {
            embeddedServer.stop();
        }
    }

    @Test
    public void uploadWithHttpClient() throws Exception {
        RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient.class, embeddedServer.getURL());

        final MultipartBody body = MultipartBody.builder()
                .addPart("file", "hello.txt", MediaType.TEXT_PLAIN_TYPE, "Hello".getBytes("UTF-8"))
                .addPart("file", "world.txt", MediaType.TEXT_PLAIN_TYPE, "World".getBytes("UTF-8"))
                .build();

        List<String> files = client.toBlocking().exchange(
                HttpRequest.POST("/", body)
                        .contentType(MediaType.MULTIPART_FORM_DATA_TYPE), ArrayList.class)
                .body();
        assertEquals(2, files.size());
        assertTrue(files.get(0).endsWith(".upload"));
        assertTrue(files.get(1).endsWith(".upload"));
    }

    @Test
    public void uploadWithDeclarativeClient() throws Exception {
        UploadClient client = embeddedServer.getApplicationContext().createBean(UploadClient.class, embeddedServer.getURL());

        final MultipartBody body = MultipartBody.builder()
                .addPart("file", "hello.txt", MediaType.TEXT_PLAIN_TYPE, "Hello".getBytes("UTF-8"))
                .addPart("file", "world.txt", MediaType.TEXT_PLAIN_TYPE, "World".getBytes("UTF-8"))
                .build();

        List<String> files = client.upload(body);

        assertEquals(2, files.size());
        assertTrue(files.get(0).endsWith(".upload"));
        assertTrue(files.get(1).endsWith(".upload"));
    }
}
