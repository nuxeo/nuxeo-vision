package org.nuxeo.vision.core.amazon;

import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
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
    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<VisionFeature> features, int maxResults) throws IOException, GeneralSecurityException, IllegalStateException {
        AmazonRekognitionClient client = new AmazonRekognitionClient();
        ArrayList<VisionResponse> result = new ArrayList<>();
        for (Blob blob : blobs) {
            ByteBuffer buf = ByteBuffer.allocate((int) blob.getLength());
            buf.put(blob.getByteArray());
            result.add(new AmazonRekognitionResponse(client.detectLabels(new DetectLabelsRequest().withImage(
                    new com.amazonaws.services.rekognition.model.Image().withBytes(buf))
                    .withMaxLabels(maxResults).withMinConfidence(0.9f))));
        }
        return result;
    }
}
