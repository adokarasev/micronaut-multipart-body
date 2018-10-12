package multipart.body;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@Controller("/")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public Single<List<String>> upload(Publisher<StreamingFileUpload> file) {
        return Flowable.fromPublisher(file)
                .flatMapSingle(part -> {
                    log.debug("Uploading {} ({})", part.getFilename(), part.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE));
                    File tempFile = File.createTempFile("micronaut-", ".upload");
                    return Single.fromPublisher(part.transferTo(tempFile))
                            .map(success -> {
                                if (!success) {
                                    tempFile.delete();
                                    throw new RuntimeException("Could not write content to " + tempFile.getAbsolutePath());
                                }
                                return tempFile.getAbsolutePath();
                            });
                })
                .toList();
    }
}
