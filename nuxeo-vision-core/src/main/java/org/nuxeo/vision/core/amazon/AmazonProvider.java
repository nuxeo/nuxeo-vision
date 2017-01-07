package org.nuxeo.vision.core.amazon;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.util.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.core.service.VisionResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loopingz on 05/01/2017.
 */
public class AmazonProvider implements VisionProvider {

    private AmazonRekognitionClient client;

    public AmazonProvider(AmazonVisionDescriptor config) {
        if (config.isValid()) {
            client = new AmazonRekognitionClient(config);
        } else {
            client = new AmazonRekognitionClient();
        }
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<VisionFeature> features, int maxResults) throws IOException, GeneralSecurityException, IllegalStateException {
        ArrayList<VisionResponse> result = new ArrayList<>();
        for (Blob blob : blobs) {
            result.add(new AmazonRekognitionResponse(client.detectLabels(new DetectLabelsRequest().withImage(
                    new com.amazonaws.services.rekognition.model.Image()
                            .withBytes(ByteBuffer.wrap(blob.getByteArray())))
                            .withMaxLabels(maxResults).withMinConfidence(0.9f))));
        }
        return result;
    }
}
